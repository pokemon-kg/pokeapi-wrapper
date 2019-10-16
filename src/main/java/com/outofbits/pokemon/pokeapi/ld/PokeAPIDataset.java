package com.outofbits.pokemon.pokeapi.ld;

import java.time.Instant;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;

/**
 * Dataset for the PokeAPI, which is a singleton. {@link PokeAPIDataset#get()} is creating a new
 * {@link ModelBuilder} at its first call and thenceforward returns always the same model builder.
 *
 * @author Kevin Haller
 * @version 1.0
 * @since 1.0
 */
public final class PokeAPIDataset {

  private static final ValueFactory vf = SimpleValueFactory.getInstance();

  private static final IRI DT_IRI = SimpleValueFactory.getInstance()
      .createIRI("http://pokemon.outofbits.com/dataset/pokeapi-co");

  private static ModelBuilder modelBuilder;

  public static ModelBuilder get() {
    if (modelBuilder == null) {
      modelBuilder = new ModelBuilder()
          .namedGraph(DT_IRI)
          .subject(DT_IRI)
          .add(RDF.TYPE, vf.createIRI("http://www.w3.org/ns/prov#Entity"))
          .add(RDF.TYPE, DCAT.DATASET)
          .add(DCTERMS.TITLE, vf.createLiteral("Pokeapi.co Crawling Dataset", "en"))
          .add(DCTERMS.DESCRIPTION, vf.createLiteral(
              "This dataset contains statements that could be gathered by crawling the pokeapi.co API.",
              "en"))
          .add(DCTERMS.CREATOR, vf.createIRI("https://kevin-haller.outofbits.com/me"))
          .add(DCTERMS.ISSUED, Instant.now())
          .add(DCTERMS.ACCRUAL_PERIODICITY,
              vf.createIRI("http://purl.org/linked-data/sdmx/2009/code#freq-A"))
          .add(DCAT.KEYWORD, vf.createLiteral("pokeapi", "en"))
          .add(DCAT.KEYWORD, vf.createLiteral("pokémon", "en"))
          .add(DCAT.KEYWORD, vf.createLiteral("crawling", "en"))
          .add(vf.createIRI("http://www.w3.org/ns/prov#hadPrimarySource"),
              vf.createIRI("https://pokeapi.co/api/v2/"));
    }
    return modelBuilder;
  }

}
