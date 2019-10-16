package com.outofbits.pokemon.pokeapi.ld;

import com.outofbits.pokemon.pokeapi.ld.exceptions.PipelineBlockExecutionException;
import org.eclipse.rdf4j.model.util.ModelBuilder;

/**
 * A pipeline block can be used in a pipeline, which will trigger the {@link PipelineBlock#run(
 *ModelBuilder)} while processing this block. Results of the block that shall be added to the
 * dataset, shall be added to the passed {@link ModelBuilder}.
 *
 * @author Kevin Haller
 * @version 1.0
 * @since 1.0
 */
public interface PipelineBlock {

  /**
   * Name of the pipeline block, which must not be an empty string or null.
   *
   * @return name of the pipeline block, which must not be an empty string or null.
   */
  String name();

  /**
   * Runs this pipeline block working on the given {@link ModelBuilder}.
   *
   * @param dataset on which this pipeline block shall work.
   * @throws PipelineBlockExecutionException if the pipeline block could not be executed
   * successfully.
   */
  void run(ModelBuilder dataset) throws PipelineBlockExecutionException;

}
