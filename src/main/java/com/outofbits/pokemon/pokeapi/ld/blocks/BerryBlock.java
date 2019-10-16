package com.outofbits.pokemon.pokeapi.ld.blocks;

import com.outofbits.pokemon.pokeapi.ld.PipelineBlock;
import com.outofbits.pokemon.pokeapi.ld.exceptions.PipelineBlockExecutionException;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import com.outofbits.pokemon.pokeapi.transformer.BerryFlavorTransformer;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BerryBlock implements PipelineBlock {

  private final BerryFlavorTransformer berryFlavorTransformer;

  @Autowired
  public BerryBlock(BerryFlavorTransformer berryFlavorTransformer) {
    this.berryFlavorTransformer = berryFlavorTransformer;
  }

  @Override
  public String name() {
    return "Berry";
  }

  @Override
  public void run(ModelBuilder dataset) throws PipelineBlockExecutionException {
    try {
      berryFlavorTransformer.transform(dataset);
    } catch (StreamIOException e) {
      throw new PipelineBlockExecutionException("Transforming berry flavors failed.", e);
    }
  }
}
