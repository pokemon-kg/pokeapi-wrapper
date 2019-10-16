package com.outofbits.pokemon.pokeapi.ld;

import com.google.common.collect.Lists;
import com.outofbits.pokemon.pokeapi.ld.blocks.AbilitiesBlock;
import com.outofbits.pokemon.pokeapi.ld.blocks.BerryBlock;
import com.outofbits.pokemon.pokeapi.ld.blocks.GameBlock;
import com.outofbits.pokemon.pokeapi.ld.blocks.MovesBlock;
import com.outofbits.pokemon.pokeapi.ld.blocks.PlacesBlock;
import com.outofbits.pokemon.pokeapi.ld.blocks.PokemonBlock;
import com.outofbits.pokemon.pokeapi.ld.blocks.WriterBlock;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A list of {@link PipelineBlock} as a default setup for a pipeline.
 *
 * @author Kevin Haller
 * @version 1.0
 * @since 1.0
 */
@Component
public final class DefaultBlocks {

  private final PokemonBlock pokemonBlock;
  private final BerryBlock berryBlock;
  private final MovesBlock movesBlock;
  private final AbilitiesBlock abilitiesBlock;
  private final GameBlock gameBlock;
  private final PlacesBlock placesBlock;

  private final WriterBlock writerBlock;

  @Autowired
  public DefaultBlocks(PokemonBlock pokemonBlock,
      BerryBlock berryBlock,
      MovesBlock movesBlock,
      AbilitiesBlock abilitiesBlock,
      GameBlock gameBlock, PlacesBlock placesBlock,
      WriterBlock writerBlock) {
    this.pokemonBlock = pokemonBlock;
    this.berryBlock = berryBlock;
    this.movesBlock = movesBlock;
    this.abilitiesBlock = abilitiesBlock;
    this.gameBlock = gameBlock;
    this.placesBlock = placesBlock;
    this.writerBlock = writerBlock;
  }

  public List<PipelineBlock> get() {
    return Lists
        .newArrayList(pokemonBlock, berryBlock, movesBlock, abilitiesBlock, gameBlock, placesBlock,
            writerBlock);
  }

}
