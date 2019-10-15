package com.outofbits.pokemon.pokeapi.transformer;

import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import org.eclipse.rdf4j.model.util.ModelBuilder;

/**
 *
 * @author Kevin Haller
 * @version 1.0
 * @since 1.0
 */
public interface PokeAPITransformer {

  /**
   *
   * @param modelBuilder which shall be used to construct the knowledge graph.
   * @throws StreamIOException if transforming failed due to an error in fetching the stream.
   */
  void transform(ModelBuilder modelBuilder) throws StreamIOException;

}
