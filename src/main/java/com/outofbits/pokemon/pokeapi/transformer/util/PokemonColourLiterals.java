package com.outofbits.pokemon.pokeapi.transformer.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIOption;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIStreamBuilder;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import com.outofbits.pokemon.pokeapi.transformer.AbstractTransformer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.rdf4j.model.Literal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PokemonColourLiterals {

  private LanguageTagMapper languageTagMapper;
  private final Map<String, List<Literal>> colourMap;

  @Autowired
  public PokemonColourLiterals(LanguageTagMapper languageTagMapper,
      PokeAPIStreamBuilder pokeAPIStreamBuilder) throws StreamIOException {
    this.languageTagMapper = languageTagMapper;
    this.colourMap = generateColourMap(pokeAPIStreamBuilder.build(PokeAPIOption.COLOR).get());
  }

  public Map<String, List<Literal>> generateColourMap(List<JsonNode> colourNodes) {
    Map<String, List<Literal>> cMap = new HashMap<>();
    for (JsonNode colourNode : colourNodes) {
      JsonNode nameNode = colourNode.get("name");
      if (nameNode != null && nameNode.isValueNode()) {
        String name = nameNode.asText();
        if (name != null && !name.trim().isEmpty()) {

          JsonNode namesNode = colourNode.get("names");
          if (namesNode != null) {
            cMap.put(name, AbstractTransformer
                .getLanguageLiterals(languageTagMapper, namesNode, "name"));
          }
        }
      }
    }
    return cMap;
  }

  public List<Literal> getLiteralsFor(String colourName) {
    if (colourName != null) {
      List<Literal> literals = colourMap.get(colourName);
      if (literals != null) {
        return literals;
      }
    }
    return Collections.emptyList();
  }

}
