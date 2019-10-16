package com.outofbits.pokemon.pokeapi.ld.blocks;

import com.outofbits.pokemon.pokeapi.ld.PipelineBlock;
import com.outofbits.pokemon.pokeapi.ld.exceptions.PipelineBlockExecutionException;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import com.outofbits.pokemon.pokeapi.transformer.MoveTransformer;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MovesBlock implements PipelineBlock {

  private final MoveTransformer moveTransformer;

  @Autowired
  public MovesBlock(MoveTransformer moveTransformer) {
    this.moveTransformer = moveTransformer;
  }

  @Override
  public String name() {
    return "Moves";
  }

  @Override
  public void run(ModelBuilder dataset) throws PipelineBlockExecutionException {
    try {
      moveTransformer.transform(dataset);
    } catch (StreamIOException e) {
      throw new PipelineBlockExecutionException("Transforming moves failed.", e);
    }
  }
}
