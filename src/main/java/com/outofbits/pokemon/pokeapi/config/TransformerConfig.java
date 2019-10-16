package com.outofbits.pokemon.pokeapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class TransformerConfig {

  @Value("${transformer.base.iri}")
  private String baseIRI;

  public String getBaseIRI() {
    return baseIRI;
  }
}
