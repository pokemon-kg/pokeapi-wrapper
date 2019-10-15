package com.outofbits.pokemon.pokeapi.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.outofbits.pokemon.pokeapi.ontology.Pokemon;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIOption;
import com.outofbits.pokemon.pokeapi.stream.PokeAPIStreamBuilder;
import com.outofbits.pokemon.pokeapi.stream.exceptions.StreamIOException;
import com.outofbits.pokemon.pokeapi.transformer.util.IRIUtil;
import com.outofbits.pokemon.pokeapi.transformer.util.LanguageTagMapper;
import com.outofbits.pokemon.pokeapi.transformer.util.PokemonTypeMapper;
import java.util.List;
import java.util.Optional;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MoveTransformer extends AbstractTransformer {

  private static final Logger logger = LoggerFactory.getLogger(MoveTransformer.class);

  private static final ValueFactory vf = SimpleValueFactory.getInstance();

  private IRIUtil iriUtil;
  private PokemonTypeMapper pokemonTypeMapper;
  private PokeAPIStreamBuilder pokeAPIStreamBuilder;

  @Autowired
  public MoveTransformer(PokeAPIStreamBuilder pokeAPIStreamBuilder, IRIUtil iriUtil,
      PokemonTypeMapper pokemonTypeMapper,
      LanguageTagMapper languageTagMapper) {
    super(languageTagMapper);
    this.pokeAPIStreamBuilder = pokeAPIStreamBuilder;
    this.iriUtil = iriUtil;
    this.pokemonTypeMapper = pokemonTypeMapper;
  }

  @Override
  public void transform(ModelBuilder modelBuilder) throws StreamIOException {
    List<JsonNode> movesNode = pokeAPIStreamBuilder.build(PokeAPIOption.MOVE).get();
    for (JsonNode moveNode : movesNode) {
      JsonNode nameNode = moveNode.get("name");
      if (nameNode != null && nameNode.isValueNode()) {
        String name = nameNode.asText();
        modelBuilder.subject(iriUtil.createMoveIRI(name))
            .add(RDF.TYPE, Pokemon.Move);
        // parse names
        JsonNode namesNode = moveNode.get("names");
        if (namesNode != null) {
          for (Literal ll : getLanguageLiterals(namesNode, "name")) {
            modelBuilder.add(RDFS.LABEL, ll);
          }
        }

        parseMoveClass(modelBuilder, moveNode);

        JsonNode flavorTextNode = moveNode.get("flavor_text_entries");
        if (flavorTextNode != null) {
          for (Literal ll : getLanguageLiterals(flavorTextNode, "flavor_text")) {
            modelBuilder.add(Pokemon.EffectDescription, ll);
          }
        }

        JsonNode typeNode = moveNode.at("/type/name");
        if (typeNode != null && typeNode.isValueNode()) {
          Optional<IRI> optionalType = pokemonTypeMapper.map(typeNode.asText());
          if (optionalType.isPresent()) {
            modelBuilder.add(Pokemon.HasType, optionalType.get());
          } else {
            logger.warn("Move '{}' has an unknown type '{}'.", name, typeNode.asText());
          }
        }

      }
    }
  }

  private void parseMoveClass(ModelBuilder builder, JsonNode moveNode) {
    if (moveNode != null) {
      JsonNode dClassNode = moveNode.at("/damage_class/name");
      if (dClassNode != null && dClassNode.isValueNode()) {
        String dClass = dClassNode.asText();
        if (dClass != null) {
          switch (dClass.trim()) {
            case "physical":
              builder.add(RDF.TYPE, Pokemon.PhysicalMove);
              break;
            case "special":
              builder.add(RDF.TYPE, Pokemon.SpecialMove);
              break;
            case "status":
              builder.add(RDF.TYPE, Pokemon.StatusMove);
              break;
            default:
              logger.warn("Damage class '{}' is unknown.", dClass);
          }
        }
      }
    }
  }

}
