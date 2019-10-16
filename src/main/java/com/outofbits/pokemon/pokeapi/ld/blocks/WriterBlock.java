package com.outofbits.pokemon.pokeapi.ld.blocks;

import com.google.common.collect.Lists;
import com.outofbits.pokemon.pokeapi.config.WriterConfig;
import com.outofbits.pokemon.pokeapi.ld.PipelineBlock;
import com.outofbits.pokemon.pokeapi.ld.exceptions.PipelineBlockExecutionException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WriterBlock implements PipelineBlock {

  private final WriterConfig writerConfig;

  @Autowired
  public WriterBlock(WriterConfig writerConfig) {
    this.writerConfig = writerConfig;
  }

  @Override
  public String name() {
    return "Writer";
  }

  @Override
  public void run(ModelBuilder dataset) throws PipelineBlockExecutionException {
    RDFFormat rdfFormat = writerConfig.getRdfFormat();
    if (rdfFormat == null) {
      Optional<RDFFormat> foundFormat = RDFFormat.matchFileName(writerConfig.getOutputPath(),
          Lists.newArrayList(RDFFormat.TRIG, RDFFormat.NQUADS));
      if (foundFormat.isPresent()) {
        rdfFormat = foundFormat.get();
      } else {
        throw new PipelineBlockExecutionException("No recognizable RDF format could be detected.");
      }
    }
    try (OutputStream rdfOut = new BufferedOutputStream(
        new FileOutputStream(writerConfig.getOutputPath()))) {
      Rio.write(dataset.build(), rdfOut, rdfFormat);
    } catch (IOException e) {
      throw new PipelineBlockExecutionException(
          String.format("Dataset could not be written to '%s'.", writerConfig.getOutputPath()));
    }
  }
}
