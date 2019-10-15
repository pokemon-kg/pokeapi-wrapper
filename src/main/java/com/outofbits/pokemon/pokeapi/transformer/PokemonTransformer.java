package com.outofbits.pokemon.pokeapi.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.outofbits.pokemon.pokeapi.ontology.Pokemon;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIOption;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIStreamBuilder;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import com.outofbits.pokemon.pokeapi.transformer.util.IRIUtil;
import com.outofbits.pokemon.pokeapi.transformer.util.LanguageTagMapper;
import com.outofbits.pokemon.pokeapi.transformer.util.PokemonColourLiterals;
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
 * This class is an implementation of {@link PokeAPITransformer} that transforms Pokemon data.
 *
 * @author Kevin Haller
 * @version 1.0
 * @see <a href="https://pokeapi.co/api/v2/pokemon-species/">Pokemon Species PokeAPI</a>
 * @since 1.0
 */
@Component
public class PokemonTransformer extends AbstractTransformer {

  private static final ValueFactory vf = SimpleValueFactory.getInstance();

  private final IRIUtil iriUtil;
  private final PokemonColourLiterals pokemonColourLiterals;
  private final PokeAPIStreamBuilder pokeAPIStreamBuilder;

  @Autowired
  public PokemonTransformer(PokeAPIStreamBuilder pokeAPIStreamBuilder,
      PokemonColourLiterals pokemonColourLiterals, IRIUtil iriUtil,
      LanguageTagMapper languageTagMapper) {
    super(languageTagMapper);
    this.pokemonColourLiterals = pokemonColourLiterals;
    this.pokeAPIStreamBuilder = pokeAPIStreamBuilder;
    this.iriUtil = iriUtil;
  }

  @Override
  public void transform(ModelBuilder modelBuilder) throws StreamIOException {
    List<JsonNode> pokemonNodes = pokeAPIStreamBuilder.build(PokeAPIOption.POKEMON).get();
    for (JsonNode pokemonNode : pokemonNodes) {
      JsonNode nameNode = pokemonNode.get("name");
      if (nameNode != null && nameNode.isValueNode()) {
        String name = nameNode.asText();
        modelBuilder.subject(iriUtil.createPokemonIRI(name))
            .add(RDF.TYPE, Pokemon.Pokémon);

        // parse names
        JsonNode namesNode = pokemonNode.get("names");
        if (namesNode != null) {
          for (Literal ll : getLanguageLiterals(namesNode, "name")) {
            modelBuilder.add(RDFS.LABEL, ll);
          }
        }

        // parse species
        JsonNode generaNode = pokemonNode.get("genera");
        if (generaNode != null) {
          for (Literal ll : getLanguageLiterals(generaNode, "genus")) {
            modelBuilder.add(Pokemon.Species, ll);
          }
        }

        JsonNode colourNode = pokemonNode.at("/color/name");
        if (colourNode != null && colourNode.isValueNode()) {
          for(Literal ll : pokemonColourLiterals.getLiteralsFor(colourNode.asText())) {
            modelBuilder.add(Pokemon.HasColor, ll);
          }
        }
      }
    }
  }
}
