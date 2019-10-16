package com.outofbits.pokemon.pokeapi.ld.exceptions;

/**
 * This exception shall be thrown, if the execution of a {@link PipelineBlockExecutionException},
 * due to whatever reason.
 *
 * @author Kevin Haller
 * @version 1.0
 * @since 1.0
 */
public class PipelineBlockExecutionException extends Exception {

  public PipelineBlockExecutionException() {
  }

  public PipelineBlockExecutionException(String message) {
    super(message);
  }

  public PipelineBlockExecutionException(String message, Throwable cause) {
    super(message, cause);
  }

  public PipelineBlockExecutionException(Throwable cause) {
    super(cause);
  }
}
