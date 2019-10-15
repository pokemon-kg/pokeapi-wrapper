package com.outofbits.pokemon.pokeapi.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.outofbits.pokemon.pokeapi.ontology.Pokemon;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIOption;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIStreamBuilder;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import com.outofbits.pokemon.pokeapi.transformer.util.IRIUtil;
import com.outofbits.pokemon.pokeapi.transformer.util.LanguageTagMapper;
import java.util.List;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class is an implementation of {@link PokeAPITransformer} that transforms generation data.
 *
 * @author Kevin Haller
 * @version 1.0
 * @see <a href="https://pokeapi.co/api/v2/generation/">Generations PokeAPI</a>
 * @since 1.0
 */
@Component
public class GenerationTransformer extends AbstractTransformer {

  private static final ValueFactory vf = SimpleValueFactory.getInstance();

  private final IRIUtil iriUtil;
  private final PokeAPIStreamBuilder pokeAPIStreamBuilder;

  @Autowired
  public GenerationTransformer(PokeAPIStreamBuilder pokeAPIStreamBuilder, IRIUtil iriUtil,
      LanguageTagMapper languageTagMapper) {
    super(languageTagMapper);
    this.pokeAPIStreamBuilder = pokeAPIStreamBuilder;
    this.iriUtil = iriUtil;
  }

  @Override
  public void transform(ModelBuilder modelBuilder) throws StreamIOException {
    List<JsonNode> generationNodes = pokeAPIStreamBuilder.build(PokeAPIOption.GENERATION).get();
    for (JsonNode generationNode : generationNodes) {
      JsonNode nameNode = generationNode.get("name");
      if (nameNode != null && nameNode.isValueNode()) {
        String name = nameNode.asText();
        modelBuilder.subject(iriUtil.createGenerationIRI(name.replace("generation-", "")))
            .add(RDF.TYPE, Pokemon.Generation);

        // parse names
        JsonNode namesNode = generationNode.get("names");
        if (namesNode != null) {
          for (Literal ll : getLanguageLiterals(namesNode, "name")) {
            modelBuilder.add(RDFS.LABEL, ll);
          }
        }

        // Pokemon species
        JsonNode pokemonSpeciesNode = generationNode.get("pokemon_species");
        if (pokemonSpeciesNode != null) {
          for (JsonNode pokemonNode : (ArrayNode) pokemonSpeciesNode) {
            if (pokemonNode != null && pokemonNode.isObject()) {
              JsonNode pokemonNameNode = pokemonNode.get("name");
              if (pokemonNameNode != null) {
                String pokemonName = pokemonNameNode.asText();
                if (pokemonName != null && !pokemonName.isEmpty()) {
                  modelBuilder.add(Pokemon.FeaturesPokémon, iriUtil.createPokemonIRI(pokemonName));
                }
              }
            }
          }
        }



      }
    }
  }
}
