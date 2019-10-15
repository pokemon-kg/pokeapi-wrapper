package com.outofbits.pokemon.pokeapi.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.outofbits.pokemon.pokeapi.ontology.Pokemon;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIOption;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIStreamBuilder;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import com.outofbits.pokemon.pokeapi.transformer.util.IRIUtil;
import com.outofbits.pokemon.pokeapi.transformer.util.LanguageTagMapper;
import java.util.List;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class is an implementation of {@link PokeAPITransformer} that transforms ability data.
 *
 * @author Kevin Haller
 * @version 1.0
 * @see <a href="https://pokeapi.co/api/v2/ability/">Ability PokeAPI</a>
 * @since 1.0
 */
@Component
public class AbilityTransformer  extends AbstractTransformer {

  private final IRIUtil iriUtil;
  private final PokeAPIStreamBuilder pokeAPIStreamBuilder;

  @Autowired
  public AbilityTransformer(
      LanguageTagMapper languageTagMapper,
      IRIUtil iriUtil, PokeAPIStreamBuilder pokeAPIStreamBuilder) {
    super(languageTagMapper);
    this.iriUtil = iriUtil;
    this.pokeAPIStreamBuilder = pokeAPIStreamBuilder;
  }

  @Override
  public void transform(ModelBuilder modelBuilder) throws StreamIOException {
    List<JsonNode> abilityNodes = pokeAPIStreamBuilder.build(PokeAPIOption.Ability).get();
    for (JsonNode abilityNode : abilityNodes) {
      JsonNode nameNode = abilityNode.get("name");
      if (nameNode != null && nameNode.isValueNode()) {
        String name = nameNode.asText();
        modelBuilder.subject(iriUtil.createAbilityIRI(name))
            .add(RDF.TYPE, Pokemon.Ability);
        // parse names
        JsonNode namesNode = abilityNode.get("names");
        if (namesNode != null) {
          for (Literal ll : getLanguageLiterals(namesNode, "name")) {
            modelBuilder.add(RDFS.LABEL, ll);
          }
        }

        JsonNode flavorTextNode = abilityNode.get("flavor_text_entries");
        if (flavorTextNode != null) {
          for (Literal ll : getLanguageLiterals(flavorTextNode, "flavor_text")) {
            modelBuilder.add(Pokemon.EffectDescription, ll);
          }
        }

      }
    }
  }
}
