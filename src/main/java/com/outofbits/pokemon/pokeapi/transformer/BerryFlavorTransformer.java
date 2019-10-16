package com.outofbits.pokemon.pokeapi.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIOption;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIStreamBuilder;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import com.outofbits.pokemon.pokeapi.transformer.util.IRIUtil;
import com.outofbits.pokemon.pokeapi.transformer.util.LanguageTagMapper;
import java.util.List;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BerryFlavorTransformer extends AbstractTransformer {

  private static final ValueFactory vf = SimpleValueFactory.getInstance();

  private final IRIUtil iriUtil;
  private final PokeAPIStreamBuilder pokeAPIStreamBuilder;

  @Autowired
  public BerryFlavorTransformer(
      LanguageTagMapper languageTagMapper,
      IRIUtil iriUtil, PokeAPIStreamBuilder pokeAPIStreamBuilder) {
    super(languageTagMapper);
    this.iriUtil = iriUtil;
    this.pokeAPIStreamBuilder = pokeAPIStreamBuilder;
  }

  @Override
  public void transform(ModelBuilder modelBuilder) throws StreamIOException {
    List<JsonNode> berryFlavorNodes = pokeAPIStreamBuilder.build(PokeAPIOption.BERRY_FALVOR).get();
    for (JsonNode berryFlavorNode : berryFlavorNodes) {
      JsonNode name = berryFlavorNode.get("name");
      if (name != null && name.isValueNode()) {
        String nameText = name.asText();
        if (nameText != null && !nameText.isEmpty()) {
          IRI berryFlavorIRI = iriUtil.createBerryFlavorIRI(nameText);
          modelBuilder.subject(berryFlavorIRI);
          // parse names
          JsonNode namesNode = berryFlavorNode.get("names");
          if (namesNode != null) {
            for (Literal ll : getLanguageLiterals(namesNode, "name")) {
              modelBuilder.add(RDFS.LABEL, ll);
            }
          }
          //todo: continue
        }
      }
    }
  }
}
