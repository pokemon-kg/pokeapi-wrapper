package com.outofbits.pokemon.pokeapi.ld.blocks;

import com.outofbits.pokemon.pokeapi.ld.PipelineBlock;
import com.outofbits.pokemon.pokeapi.ld.exceptions.PipelineBlockExecutionException;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import com.outofbits.pokemon.pokeapi.transformer.AbilityTransformer;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AbilitiesBlock implements PipelineBlock {

  private final AbilityTransformer abilityTransformer;

  @Autowired
  public AbilitiesBlock(AbilityTransformer abilityTransformer) {
    this.abilityTransformer = abilityTransformer;
  }

  @Override
  public String name() {
    return "Ability";
  }

  @Override
  public void run(ModelBuilder dataset) throws PipelineBlockExecutionException {
    try {
      abilityTransformer.transform(dataset);
    } catch (StreamIOException e) {
      throw new PipelineBlockExecutionException("Transforming abilities failed.", e);
    }
  }
}
