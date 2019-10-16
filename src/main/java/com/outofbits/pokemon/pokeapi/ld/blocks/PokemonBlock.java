package com.outofbits.pokemon.pokeapi.ld.blocks;

import com.outofbits.pokemon.pokeapi.ld.PipelineBlock;
import com.outofbits.pokemon.pokeapi.ld.exceptions.PipelineBlockExecutionException;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import com.outofbits.pokemon.pokeapi.transformer.PokedexTransformer;
import com.outofbits.pokemon.pokeapi.transformer.PokemonTransformer;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PokemonBlock implements PipelineBlock {

  private final PokemonTransformer pokemonTransformer;
  private final PokedexTransformer pokedexTransformer;

  @Autowired
  public PokemonBlock(PokemonTransformer pokemonTransformer,
      PokedexTransformer pokedexTransformer) {
    this.pokemonTransformer = pokemonTransformer;
    this.pokedexTransformer = pokedexTransformer;
  }

  @Override
  public String name() {
    return "Pokemon";
  }

  @Override
  public void run(ModelBuilder dataset) throws PipelineBlockExecutionException {
    try {
      pokemonTransformer.transform(dataset);
    } catch (StreamIOException e) {
      throw new PipelineBlockExecutionException("Transforming Pokemons failed.", e);
    }
    try {
      pokedexTransformer.transform(dataset);
    } catch (StreamIOException e) {
      throw new PipelineBlockExecutionException("Transforming Pokedex failed.", e);
    }
  }
}
