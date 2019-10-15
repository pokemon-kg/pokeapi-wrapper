package com.outofbits.pokemon.pokeapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class PokeAPIConfiguration {

  @Value("${pokeapi.url}")
  private String apiURL;

  @Value("${pokeapi.cache.dir:cache}")
  private String cacheDirPath;

  @Value("${pokeapi.cache.disabled:#{false}}")
  private boolean noCache;

  public String getApiURL() {
    return apiURL;
  }

  public String getCacheDirPath() {
    return cacheDirPath;
  }

  public boolean isNoCache() {
    return noCache;
  }

  @Override
  public String toString() {
    return "PokeAPIConfiguration{" +
        "apiURL='" + apiURL + '\'' +
        ", cacheDirPath='" + cacheDirPath + '\'' +
        ", noCache=" + noCache +
        '}';
  }
}
