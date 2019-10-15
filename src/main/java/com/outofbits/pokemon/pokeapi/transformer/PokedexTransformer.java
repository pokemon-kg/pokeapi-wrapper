package com.outofbits.pokemon.pokeapi.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.outofbits.pokemon.pokeapi.ontology.Pokemon;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIOption;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIStreamBuilder;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import com.outofbits.pokemon.pokeapi.transformer.util.IRIUtil;
import com.outofbits.pokemon.pokeapi.transformer.util.LanguageTagMapper;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class is an implementation of {@link PokeAPITransformer} that transforms Pokedex data.
 *
 * @author Kevin Haller
 * @version 1.0
 * @see <a href="https://pokeapi.co/api/v2/pokedex/">Pokedex PokeAPI</a>
 * @since 1.0
 */
@Component
public class PokedexTransformer extends AbstractTransformer {

  private static final ValueFactory vf = SimpleValueFactory.getInstance();

  private final IRIUtil iriUtil;
  private final PokeAPIStreamBuilder pokeAPIStreamBuilder;

  @Autowired
  public PokedexTransformer(PokeAPIStreamBuilder pokeAPIStreamBuilder, IRIUtil iriUtil,
      LanguageTagMapper languageTagMapper) {
    super(languageTagMapper);
    this.pokeAPIStreamBuilder = pokeAPIStreamBuilder;
    this.iriUtil = iriUtil;
  }

  @Override
  public void transform(ModelBuilder modelBuilder) throws StreamIOException {
    List<JsonNode> pokemonNodes = pokeAPIStreamBuilder.build(PokeAPIOption.POKEDEX).get();
    for (JsonNode pokedexNode : pokemonNodes) {
      JsonNode nameNode = pokedexNode.get("name");
      if (nameNode != null && nameNode.isValueNode()) {
        String name = nameNode.asText();
        IRI pokedexIRI = iriUtil.createPokedexIRI(name);
        modelBuilder.subject(pokedexIRI).add(RDF.TYPE, Pokemon.Pokédex);
        // parse names
        JsonNode namesNode = pokedexNode.get("names");
        if (namesNode != null) {
          for (Literal ll : getLanguageLiterals(namesNode, "name")) {
            modelBuilder.add(RDFS.LABEL, ll);
          }
        }
        // parse descriptions
        JsonNode descriptionsNode = pokedexNode.get("descriptions");
        if (descriptionsNode != null) {
          for (Literal ll : getLanguageLiterals(descriptionsNode, "description")) {
            modelBuilder.add(RDFS.COMMENT, ll);
          }
        }
        // parse Pokedex entries
        List<IRI> pokedexEntries = new LinkedList<>();
        JsonNode pokedexEntriesNode = pokedexNode.get("pokemon_entries");
        if (pokedexEntriesNode != null && pokedexEntriesNode.isArray()) {
          for (JsonNode pokedexEntryNode : (ArrayNode) pokedexEntriesNode) {
            JsonNode entryNumberNode = pokedexEntryNode.get("entry_number");
            JsonNode pokemonNode = pokedexEntryNode.at("/pokemon_species/name");
            if (entryNumberNode != null && entryNumberNode.isNumber() && pokemonNode != null
                && pokemonNode.isValueNode()) {
              String pokemonName = pokemonNode.asText();
              if (pokemonName != null && !pokemonName.trim().isEmpty()) {
                int entryNumber = entryNumberNode.asInt();
                IRI pokedexEntryIRI = iriUtil.createPokedexEntryIRI(name, entryNumber);
                IRI pokemeonIRI = iriUtil.createPokemonIRI(pokemonName);
                modelBuilder.subject(pokedexEntryIRI)
                    .add(RDF.TYPE, Pokemon.PokédexEntry)
                    .add(Pokemon.EntryNumber, vf.createLiteral(entryNumber))
                    .add(Pokemon.DescribesPokémon, pokemeonIRI);
                pokedexEntries.add(pokedexEntryIRI);
              }
            }
          }
        }
        for (IRI pokedexEntry : pokedexEntries) {
          modelBuilder.add(pokedexIRI, Pokemon.HasPokédexEntry, pokedexEntry);
        }
      }
    }
  }
}
