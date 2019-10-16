package com.outofbits.pokemon.pokeapi.config;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class WriterConfig {

  private final Map<String, RDFFormat> formats = ImmutableMap.<String, RDFFormat>builder()
      .put("TRIG", RDFFormat.TRIG)
      .put("NQUADS", RDFFormat.NQUADS)
      .build();

  @Value("${writer.output:output.trig}")
  private String outputPath;

  @Value("${writer.format:#{null}}")
  private String rdfFormat;

  public String getOutputPath() {
    return outputPath;
  }

  public RDFFormat getRdfFormat() {
    if (rdfFormat != null && !formats.containsKey(rdfFormat)) {
      throw new IllegalStateException(String
          .format("Format %s is not recognized. Supported formats are %s.", rdfFormat,
              formats.keySet()));
    }
    return rdfFormat != null ? formats.get(rdfFormat) : null;
  }
}
