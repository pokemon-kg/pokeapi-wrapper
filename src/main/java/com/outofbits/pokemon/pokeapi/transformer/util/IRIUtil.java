package com.outofbits.pokemon.pokeapi.transformer.util;

import static com.google.common.base.Preconditions.checkArgument;

import com.outofbits.pokemon.pokeapi.TransformerConfiguration;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A class for generating {@link IRI} for all relevant instances in the PokeAPI.
 *
 * @author Kevin Haller
 * @version 1.0
 * @since 1.0
 */
@Component
public final class IRIUtil {

  private static final ValueFactory vf = SimpleValueFactory.getInstance();

  private String baseIRI;

  @Autowired
  public IRIUtil(TransformerConfiguration transformerConfiguration) {
    this.baseIRI = transformerConfiguration.getBaseIRI();
    if (!baseIRI.endsWith("/")) {
      baseIRI += "/";
    }
    baseIRI += "instance/";
  }

  /**
   * Constructs the {@code :baseiri/pokemon/:name} for a Pokemon with the given {@code
   * pokemonName}.
   *
   * @param pokemonName name of the Pokemon for which the IRI shall be generated. It must not be
   * null or empty.
   * @return {@link IRI} of the form {@code :baseiri/pokemon/:name}.
   * @throws IllegalArgumentException if the given  {@code pokemonName} is empty or null.
   */
  public IRI createPokemonIRI(String pokemonName) {
    checkArgument(pokemonName != null && !pokemonName.trim().isEmpty(),
        "The given name for Pokemon must not be null or empty.");
    return vf.createIRI(baseIRI + "pokemon/" + pokemonName);
  }

  /**
   * Constructs the {@code :baseiri/pokedex/:name} for a Pokedex with the given {@code
   * pokedexName}.
   *
   * @param pokedexName name of the Pokedex for which the IRI shall be generated. It must not be
   * null or empty.
   * @return {@link IRI} of the form {@code :baseiri/pokedex/:name}.
   * @throws IllegalArgumentException if the given  {@code pokedexName} is empty or null.
   */
  public IRI createPokedexIRI(String pokedexName) {
    checkArgument(pokedexName != null && !pokedexName.trim().isEmpty(),
        "The given name for Pokedex must not be null or empty.");
    return vf.createIRI(baseIRI + "pokedex/" + pokedexName);
  }

  /**
   * Constructs the {@code :baseiri/pokedex/:name/entry/:number} for a Pokedex entry with the given
   * {@code pokedexName} and {@code entryNumber}.
   *
   * @param pokedexName name of the Pokedex for which the IRI shall be generated. It must not be
   * null or empty.
   * @param entryNumber the number of the entry in the Pokedex, which must be a positive integer.
   * @return {@link IRI} of the form {@code :baseiri/pokedex/:name/entry/:number}.
   */
  public IRI createPokedexEntryIRI(String pokedexName, int entryNumber) {
    checkArgument(pokedexName != null && !pokedexName.trim().isEmpty(),
        "The given name for Pokedex must not be null or empty for a Pokedex entry.");
    checkArgument(entryNumber >= 0, "");
    return vf.createIRI(baseIRI + "pokedex/" + pokedexName + "/entry/" + entryNumber);
  }

  /**
   * Constructs the {@code :baseiri/move/:name} for a move with the given {@code moveName}.
   *
   * @param moveName name of the move for which the IRI shall be generated.
   * @return {@link IRI} of the form {@code :baseiri/move/:name}.
   */
  public IRI createMoveIRI(String moveName) {
    checkArgument(moveName != null, "The move name must not be null or empty.");
    return vf.createIRI(baseIRI + "move/" + moveName);
  }

  /**
   * Constructs the {@code :baseiri/generation/:number} for a generation with the given {@code
   * number}.
   *
   * @param number for which the IRI shall be generated.
   * @return {@link IRI} of the form {@code :baseiri/generation/:version}.
   */
  public IRI createGenerationIRI(String number) {
    checkArgument(number != null && !number.isEmpty(),
        "The given generation number must not be empty or null.");
    return vf.createIRI(baseIRI + "generation/" + number);
  }

  public IRI createRegionIRI(String regionName) {
    checkArgument(regionName != null && !regionName.isEmpty(),
        "The region name must not be null or empty.");
    return vf.createIRI(baseIRI + "region/" + regionName);
  }

  public IRI createPlaceIRI(String placeName) {
    checkArgument(placeName != null && !placeName.isEmpty(),
        "The place name must not be null or empty.");
    return vf.createIRI(baseIRI + "place/" + placeName);
  }

  public IRI createAbilityIRI(String abilityName) {
    checkArgument(abilityName != null && !abilityName.isEmpty(),
        "The ability name must not be null or empty.");
    return vf.createIRI(baseIRI + "ability/" + abilityName);
  }

}
