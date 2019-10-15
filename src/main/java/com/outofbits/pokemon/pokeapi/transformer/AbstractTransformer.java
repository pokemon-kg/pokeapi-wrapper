package com.outofbits.pokemon.pokeapi.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.outofbits.pokemon.pokeapi.transformer.util.LanguageTagMapper;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public abstract class AbstractTransformer implements PokeAPITransformer {

  private static final ValueFactory vf = SimpleValueFactory.getInstance();

  private LanguageTagMapper languageTagMapper;

  public AbstractTransformer(LanguageTagMapper languageTagMapper) {
    this.languageTagMapper = languageTagMapper;
  }

  protected List<Literal> getLanguageLiterals(JsonNode node, String field) {
    return getLanguageLiterals(languageTagMapper, node, field);
  }

  public static List<Literal> getLanguageLiterals(LanguageTagMapper languageTagMapper,
      JsonNode node, String field) {
    if (node.isArray()) {
      List<Literal> literals = new LinkedList<>();
      for (JsonNode entryNode : (ArrayNode) node) {
        JsonNode labelNode = entryNode.get(field);
        JsonNode languageTagNode = entryNode.at("/language/name");
        if ((labelNode != null && labelNode.isValueNode()) && (languageTagNode != null
            && languageTagNode
            .isValueNode())) {
          String label = labelNode.asText();
          String languageTag = languageTagMapper.getISOCodeFor(languageTagNode.asText());
          if (label != null && !label.trim().isEmpty()) {
            literals.add(vf.createLiteral(label, languageTag));
          }
        }
      }
      return literals;
    }
    return Collections.emptyList();
  }

}
