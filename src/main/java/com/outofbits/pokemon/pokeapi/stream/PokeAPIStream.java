package com.outofbits.pokemon.pokeapi.stream;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link PokeAPIStream} walks through a list of a certain PokeAPI artifacts and fetches the
 * details of artifacts.
 *
 * @author Kevin Haller
 * @version 1.0
 * @since 1.0
 */
public class PokeAPIStream {

  private static final Logger logger = LoggerFactory.getLogger(PokeAPIStream.class);

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final String apiURL;
  private final String cachingDirPath;
  private final boolean noCache;

  /**
   * Creates a new {@link PokeAPIStream} reading in all Pokemon from the given {@code apiURL} of
   * PokeAPI, where the stream can be requested.
   *
   * @param apiURL API URL of the PokeAPI, where to fetch the relevant data.
   */
  PokeAPIStream(String apiURL) {
    this(apiURL, "cache", false);
  }

  /**
   * Creates a new {@link PokeAPIStream} reading in all Pokemon from the given {@code apiURL} of
   * PokeAPI.
   *
   * @param apiURL API URL of the PokeAPI, where to fetch the relevant data.
   * @param noCache {@code true}, if cached data shall be ignored, otherwise {@code false}.
   */
  PokeAPIStream(String apiURL, boolean noCache) {
    this(apiURL, "cache", noCache);
  }

  /**
   * Creates a new {@link PokeAPIStream} reading in all Pokemon from the given {@code apiURL} of
   * PokeAPI. The path to the directory for caching Pokemon in a JSON file is given with {@code
   * cachingDirPath}. When caching shall not be used, then {@code noCache} must be set to {@code
   * true}.
   *
   * @param apiURL API URL of the PokeAPI, where to fetch the relevant data.
   * @param cachingDirPath path to the directory that shall store the cached data of Pokemon.
   * @param noCache {@code true}, if cached data shall be ignored, otherwise {@code false}.
   */
  PokeAPIStream(String apiURL, String cachingDirPath, boolean noCache) {
    checkArgument(apiURL != null && !apiURL.isEmpty(), "The API Url must not be null or empty.");
    this.apiURL = apiURL;
    this.cachingDirPath = cachingDirPath;
    this.noCache = noCache;
  }

  /**
   * Tries to get the cached data under the given {@code filename}.
   *
   * @param fileName name of the cached file.
   * @return {@link Optional} of the InputStream of the cached file, or {@link Optional#empty()}, if
   * there is no cached file under this name.
   * @throws IOException if the cached file failed to be read in.
   */
  private Optional<InputStream> getCacheFor(String fileName) throws IOException {
    File cacheDir = new File(cachingDirPath);
    if (cacheDir.exists()) {
      File cachedFile = new File(cachingDirPath, fileName);
      if (cachedFile.exists()) {
        return Optional.of(new BufferedInputStream(new FileInputStream(cachedFile)));
      }
    }
    return Optional.empty();
  }

  private void cache(String filename, List<JsonNode> results) throws IOException {
    File cacheDir = new File(cachingDirPath);
    if (!cacheDir.exists() || !cacheDir.isDirectory()) {
      cacheDir.mkdirs();
    }
    try (OutputStream cacheOut = new BufferedOutputStream(
        new FileOutputStream(new File(cacheDir, filename)))) {
      objectMapper.writer().writeValue(cacheOut, results);
    }
  }

  private Optional<String> buildCacheFileName(String apiURL) {
    try {
      URI apiURI = new URI(apiURL);
      String path = apiURI.getPath();
      if (path != null && !path.isEmpty()) {
        return Optional.of(path.replace("/", "-") + ".json");
      }
      return Optional.empty();
    } catch (URISyntaxException e) {
      logger.error("API URL cannot be parsed. {}", e.getMessage());
      return Optional.empty();
    }
  }

  public List<JsonNode> get() throws StreamIOException {
    if (!noCache) {
      Optional<String> cacheFileNameOptional = buildCacheFileName(apiURL);
      if (cacheFileNameOptional.isPresent()) {
        try {
          Optional<InputStream> pokemonStreamOptional = getCacheFor(cacheFileNameOptional.get());
          if (pokemonStreamOptional.isPresent()) {
            try {
              return objectMapper
                  .readValue(pokemonStreamOptional.get(), new TypeReference<List<JsonNode>>() {
                  });
            } finally {
              pokemonStreamOptional.get().close();
            }
          }
        } catch (IOException e) {
          logger.warn("Cache could not read. {}", e.getMessage());
        }
        logger.info("Reading cache failed for API stream {}.", apiURL);
      }
      logger.info("API stream {} is read from the remote HTTP Rest API.", apiURL);
      return getFromAPI();
    } else {
      logger.info("API stream {} will be fetched newly. Cache is not used.", apiURL);
      return getFromAPI();
    }
  }

  private List<JsonNode> getFromAPI() throws StreamIOException {
    List<JsonNode> results = fetch();
    // try to store file to caching directory
    Optional<String> cacheFileNameOpt = buildCacheFileName(apiURL);
    if (cacheFileNameOpt.isPresent()) {
      try {
        cache(cacheFileNameOpt.get(), results);
      } catch (IOException e) {
        logger.error("Could not write to the cache directory {}.", e.getMessage());
      }
    }
    return results;
  }

  private List<JsonNode> fetch() throws StreamIOException {
    List<JsonNode> resultList = new LinkedList<>();
    int attempts = 3;
    String currentAPIUrl = apiURL;
    do {
      HttpResponse<String> response = Unirest.get(currentAPIUrl)
          .header("accept", "application/json").asString();
      if (response.isSuccess()) {
        try {
          JsonNode jsonNode = objectMapper.readTree(response.getBody());
          if (jsonNode.isObject()) {
            if (jsonNode.has("results")) {
              JsonNode results = jsonNode.get("results");
              if (results.isArray()) {
                ArrayNode resultArray = (ArrayNode) results;
                for (JsonNode result : resultArray) {
                  if (result.isObject()) {
                    if (result.has("url")) {
                      JsonNode urlNode = result.get("url");
                      if (urlNode.isValueNode() && !urlNode.isNull()) {
                        String entityUrl = urlNode.asText();
                        try {
                          HttpResponse<String> entityResponse = Unirest.get(entityUrl)
                              .header("accept", "application/json").asString();
                          if (entityResponse.isSuccess()) {
                            resultList.add(objectMapper.readTree(entityResponse.getBody()));
                          } else {
                            logger.warn("Entity '{}' could not be fetched (status={}).", entityUrl,
                                entityResponse.getStatus());
                          }
                        } catch (Exception e) {
                          logger.warn("Entity '{}' could not be fetched. {}", entityUrl,
                              e.getMessage());
                        }
                      }
                    }
                  }
                }
              }
            }
            if (jsonNode.has("next")) {
              JsonNode nextAPIUrlNode = jsonNode.get("next");
              if (nextAPIUrlNode.isNull()) {
                break;
              }
              if (nextAPIUrlNode.isValueNode()) {
                currentAPIUrl = nextAPIUrlNode.asText();
                attempts = 3;
              } else {
                break;
              }
            } else {
              break;
            }
          }
        } catch (Exception e) {
          attempts--;
          e.printStackTrace();
          logger.info("Request '{}': Failed to load JSON response '{}'. {} attempt{} left.", apiURL,
              e.getMessage(), attempts, attempts > 1 ? "s" : "");

        }
      } else {
        attempts--;
        logger.info("Request '{}' failed with status code {}. {} attempt{} left.", apiURL,
            response.getStatus(), attempts, attempts > 1 ? "s" : "");
      }
    } while (attempts > 0);
    if (attempts == 0) {
      throw new StreamIOException(String.format("Could not read data from '%s'.", apiURL));
    }
    return resultList;
  }

}
