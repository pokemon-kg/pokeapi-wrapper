package com.outofbits.pokemon.pokeapi.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.outofbits.pokemon.pokeapi.ontology.Pokemon;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIOption;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIStreamBuilder;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import com.outofbits.pokemon.pokeapi.transformer.util.IRIUtil;
import com.outofbits.pokemon.pokeapi.transformer.util.LanguageTagMapper;
import java.util.List;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class is an implementation of {@link PokeAPITransformer} that transforms region data.
 *
 * @author Kevin Haller
 * @version 1.0
 * @see <a href="https://pokeapi.co/api/v2/region/">Region PokeAPI</a>
 * @since 1.0
 */
@Component
public class RegionTransformer extends AbstractTransformer {

  private static final ValueFactory vf = SimpleValueFactory.getInstance();

  private final IRIUtil iriUtil;
  private final PokeAPIStreamBuilder pokeAPIStreamBuilder;

  @Autowired
  public RegionTransformer(PokeAPIStreamBuilder pokeAPIStreamBuilder, IRIUtil iriUtil,
      LanguageTagMapper languageTagMapper) {
    super(languageTagMapper);
    this.pokeAPIStreamBuilder = pokeAPIStreamBuilder;
    this.iriUtil = iriUtil;
  }

  @Override
  public void transform(ModelBuilder modelBuilder) throws StreamIOException {
    List<JsonNode> regionNodes = pokeAPIStreamBuilder.build(PokeAPIOption.REGION).get();
    for (JsonNode regionNode : regionNodes) {
      JsonNode nameNode = regionNode.get("name");
      if (nameNode != null && nameNode.isValueNode()) {
        String name = nameNode.asText();
        modelBuilder.subject(iriUtil.createRegionIRI(name))
            .add(RDF.TYPE, Pokemon.Region);

        // parse names
        JsonNode namesNode = regionNode.get("names");
        if (namesNode != null) {
          for (Literal ll : getLanguageLiterals(namesNode, "name")) {
            modelBuilder.add(RDFS.LABEL, ll);
          }
        }

        // link to locations
        JsonNode locationsNode = regionNode.get("locations");
        if (locationsNode != null && locationsNode.isArray()) {
          for (JsonNode locationNode : (ArrayNode) locationsNode) {
            if (locationNode != null && locationNode.isObject()) {
              JsonNode locationNameNode = locationNode.get("name");
              if (locationNameNode != null && locationNameNode.isValueNode()) {
                String locationName = locationNameNode.asText();
                if (locationName != null && !locationName.trim().isEmpty()) {
                  modelBuilder.add(Pokemon.ContainsPlace, iriUtil.createPlaceIRI(locationName));
                }
              }
            }
          }
        }

      }
    }
  }
}
