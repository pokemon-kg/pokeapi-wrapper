package com.outofbits.pokemon.pokeapi;

import com.outofbits.pokemon.pokeapi.metainformation.PokeAPIDataset;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import com.outofbits.pokemon.pokeapi.transformer.AbilityTransformer;
import com.outofbits.pokemon.pokeapi.transformer.GenerationTransformer;
import com.outofbits.pokemon.pokeapi.transformer.MoveTransformer;
import com.outofbits.pokemon.pokeapi.transformer.PokedexTransformer;
import com.outofbits.pokemon.pokeapi.transformer.PokemonTransformer;
import com.outofbits.pokemon.pokeapi.transformer.RegionTransformer;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Kevin Haller
 * @version 1.0
 * @since 1.0
 */
@Component
public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  private static final IRI DT_IRI = SimpleValueFactory.getInstance()
      .createIRI("http://pokemon.outofbits.com/dataset/pokeapi-co");

  private PokemonTransformer pokemonTransformer;
  private PokedexTransformer pokedexTransformer;
  private MoveTransformer moveTransformer;
  private AbilityTransformer abilityTransformer;
  private GenerationTransformer generationTransformer;
  private RegionTransformer regionTransformer;

  @Autowired
  public Main(PokemonTransformer pokemonTransformer,
      PokedexTransformer pokedexTransformer,
      MoveTransformer moveTransformer,
      AbilityTransformer abilityTransformer,
      GenerationTransformer generationTransformer,
      RegionTransformer regionTransformer) {
    this.pokemonTransformer = pokemonTransformer;
    this.pokedexTransformer = pokedexTransformer;
    this.moveTransformer = moveTransformer;
    this.abilityTransformer = abilityTransformer;
    this.generationTransformer = generationTransformer;
    this.regionTransformer = regionTransformer;
  }

  public void run() {
    logger.info("Starting to transform PokeAPI data.");
    ModelBuilder modelBuilder = new ModelBuilder();

    logger.info("Transforming Pokemon ...");
    try {
      pokemonTransformer.transform(modelBuilder);
    } catch (StreamIOException e) {
      logger.error("Transforming the Pokemon failed. {}", e.getMessage());
    }

    logger.info("Transforming Pokedex ...");
    try {
      pokedexTransformer.transform(modelBuilder);
    } catch (StreamIOException e) {
      logger.error("Transforming the Pokedex failed. {}", e.getMessage());
    }

    logger.info("Transforming Moves ...");
    try {
      moveTransformer.transform(modelBuilder);
    } catch (StreamIOException e) {
      logger.error("Transforming the moves failed. {}", e.getMessage());
    }

    logger.info("Transforming Abilities ...");
    try {
      abilityTransformer.transform(modelBuilder);
    } catch (StreamIOException e) {
      logger.error("Transforming the abilities failed. {}", e.getMessage());
    }

    logger.info("Transforming Generations ...");
    try {
      generationTransformer.transform(modelBuilder);
    } catch (StreamIOException e) {
      logger.error("Transforming the generations failed. {}", e.getMessage());
    }

    logger.info("Transforming Regions ...");
    try {
      regionTransformer.transform(modelBuilder);
    } catch (StreamIOException e) {
      logger.error("Transforming the regions failed. {}", e.getMessage());
    }

    Model pokeAPIModel = new LinkedHashModel();
    for (Statement stmt : modelBuilder.build()) {
      pokeAPIModel.add(stmt.getSubject(), stmt.getPredicate(), stmt.getObject(), DT_IRI);
    }
    for (Statement stmt : PokeAPIDataset.getInfo(DT_IRI)) {
      pokeAPIModel.add(stmt.getSubject(), stmt.getPredicate(), stmt.getObject(), DT_IRI);
    }

    logger.info("Writing results.");
    try (OutputStream rdfOut = new BufferedOutputStream(new FileOutputStream("pokeapi-co.trig"))) {
      Rio.write(pokeAPIModel, rdfOut, RDFFormat.TRIG);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    new AnnotationConfigApplicationContext(
        "com.outofbits.pokemon.pokeapi").getBean(Main.class).run();
  }

}
