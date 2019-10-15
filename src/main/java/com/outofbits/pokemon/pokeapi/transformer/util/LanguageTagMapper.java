package com.outofbits.pokemon.pokeapi.transformer.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIOption;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIStreamBuilder;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Language tags for RDF must be of the 'ISO 639' standard and this class helps to map the name of
 * languages in PokeAPI to their corresponding ISO code.
 *
 * @author Kevin Haller
 * @version 1.0
 * @since 1.0
 */
@Component
public final class LanguageTagMapper {

  private final Map<String, String> codeMap;

  @Autowired
  public LanguageTagMapper(PokeAPIStreamBuilder pokeAPIStreamBuilder) throws StreamIOException {
    this.codeMap = generateCodeMap(pokeAPIStreamBuilder.build(PokeAPIOption.LANGUAGE).get());
  }

  private Map<String, String> generateCodeMap(List<JsonNode> languageList) {
    Map<String, String> codeMap = new HashMap<>();
    for (JsonNode languageNode : languageList) {
      JsonNode nameNode = languageNode.get("name");
      JsonNode iso639CodeNode = languageNode.get("iso639");
      if (nameNode != null && iso639CodeNode != null) {
        String name = nameNode.asText();
        String iso639Code = iso639CodeNode.asText();
        if (name != null && !name.trim().isEmpty() && iso639Code != null && !iso639Code.trim()
            .isEmpty()) {
          codeMap.put(name.trim(), iso639Code.trim());
        }
      }
    }
    return codeMap;
  }

  /**
   * Gets the ISO code for he given {@code languageName}.
   *
   * @param languageName for which the ISO code shall be returned. It can be null, or empty.
   * @return ISO code for the given {@code languageName}, if it can be found.
   */
  public String getISOCodeFor(String languageName) {
    return languageName != null ? codeMap.get(languageName) : null;
  }

}
