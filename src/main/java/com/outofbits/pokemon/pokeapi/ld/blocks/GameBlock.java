package com.outofbits.pokemon.pokeapi.ld.blocks;

import com.outofbits.pokemon.pokeapi.ld.PipelineBlock;
import com.outofbits.pokemon.pokeapi.ld.exceptions.PipelineBlockExecutionException;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import com.outofbits.pokemon.pokeapi.transformer.GenerationTransformer;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameBlock implements PipelineBlock {

  private final GenerationTransformer generationTransformer;

  @Autowired
  public GameBlock(GenerationTransformer generationTransformer) {
    this.generationTransformer = generationTransformer;
  }

  @Override
  public String name() {
    return "Game";
  }

  @Override
  public void run(ModelBuilder dataset) throws PipelineBlockExecutionException {
    try {
      generationTransformer.transform(dataset);
    } catch (StreamIOException e) {
      throw new PipelineBlockExecutionException("Transforming generations failed.", e);
    }
  }
}
