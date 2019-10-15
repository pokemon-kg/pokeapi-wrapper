package com.outofbits.pokemon.pokeapi.stream.exceptions;

public class StreamIOException extends Exception {

  public StreamIOException() {
  }

  public StreamIOException(String message) {
    super(message);
  }

  public StreamIOException(String message, Throwable cause) {
    super(message, cause);
  }

  public StreamIOException(Throwable cause) {
    super(cause);
  }

  public StreamIOException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
