package com.outofbits.pokemon.pokeapi.stream;

import static com.google.common.base.Preconditions.checkArgument;

import com.outofbits.pokemon.pokeapi.config.PokeAPIConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A builder for {@link PokeAPIStream}.
 *
 * @author Kevin Haller
 * @version 1.0
 * @since 1.0
 */
@Component
public class PokeAPIStreamBuilder {

  private PokeAPIConfig pokeAPIConfig;

  @Autowired
  public PokeAPIStreamBuilder(PokeAPIConfig pokeAPIConfig) {
    this.pokeAPIConfig = pokeAPIConfig;
  }

  public PokeAPIStream build(PokeAPIOption option) {
    checkArgument(option != null, "The PokeAPI option must not be null.");
    String apiURL = pokeAPIConfig.getApiURL();
    if (!apiURL.endsWith("/")) {
      apiURL += "/";
    }
    return new PokeAPIStream(apiURL + option.getAPIPath(), pokeAPIConfig.getCacheDirPath(),
        pokeAPIConfig.isNoCache());
  }
}
