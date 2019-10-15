package com.outofbits.pokemon.pokeapi.metainformation;

import static com.google.common.base.Preconditions.checkArgument;

import java.time.Instant;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;

public final class PokeAPIDataset {

  private static final ValueFactory vf = SimpleValueFactory.getInstance();

  public static Model getInfo(IRI datasetIRI) {
    checkArgument(datasetIRI != null, "The given dataset IRI must not be null.");
    ModelBuilder modelBuilder = new ModelBuilder()
        .subject(datasetIRI)
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

    return modelBuilder.build();
  }

}
