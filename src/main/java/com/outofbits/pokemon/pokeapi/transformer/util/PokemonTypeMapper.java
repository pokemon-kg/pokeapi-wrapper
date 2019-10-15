package com.outofbits.pokemon.pokeapi.transformer.util;

import com.google.common.collect.ImmutableMap;
import com.outofbits.pokemon.pokeapi.ontology.Pokemon;
import java.util.Map;
import java.util.Optional;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.stereotype.Component;

/**
 * Pokemons, moves and abilities can have special types and this class provides a method to map the
 * names used in PokeAPI to the type instances in the ontology.
 *
 * @author Kevin Haller
 * @version 1.0
 * @since 1.0
 */
@Component
public class PokemonTypeMapper {

  private Map<String, IRI> pokemonTypeMap = ImmutableMap.<String, IRI>builder()
      .put("normal", Pokemon.PokétypeNormal)
      .put("fighting", Pokemon.PokétypeFighting)
      .put("flying", Pokemon.PokétypeFlying)
      .put("poison", Pokemon.PokétypePoison)
      .put("ground", Pokemon.PokétypeGround)
      .put("rock", Pokemon.PokétypeRock)
      .put("bug", Pokemon.PokétypeBug)
      .put("ghost", Pokemon.PokétypeGhost)
      .put("steel", Pokemon.PokétypeSteel)
      .put("fire", Pokemon.PokétypeFire)
      .put("water", Pokemon.PokétypeWater)
      .put("grass", Pokemon.PokétypeGrass)
      .put("electric", Pokemon.PokétypeElectric)
      .put("psychic", Pokemon.PokétypePsychic)
      .put("ice", Pokemon.PokétypeIce)
      .put("dragon", Pokemon.PokétypeDragon)
      .put("dark", Pokemon.PokétypeDark)
      .put("fairy", Pokemon.PokétypeFairy)
      .build();

  public Optional<IRI> map(String typeName){
    if(typeName != null){
      IRI typeIRI = pokemonTypeMap.get(typeName);
      if(typeIRI != null){
        return Optional.of(typeIRI);
      }
    }
    return Optional.empty();
  }

}
