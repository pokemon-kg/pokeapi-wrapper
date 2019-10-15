package com.outofbits.pokemon.pokeapi.stream;

public enum PokeAPIOption {

  Ability {
    @Override
    public String getAPIPath() {
      return "ability";
    }
  },
  POKEMON {
    @Override
    public String getAPIPath() {
      return "pokemon-species";
    }
  },
  POKEDEX {
    @Override
    public String getAPIPath() {
      return "pokedex";
    }
  },
  MOVE {
    @Override
    public String getAPIPath() {
      return "move";
    }
  },
  GENERATION {
    @Override
    public String getAPIPath() {
      return "generation";
    }
  },
  REGION {
    @Override
    public String getAPIPath() {
      return "region";
    }
  },
  LANGUAGE {
    @Override
    public String getAPIPath() {
      return "language";
    }
  },
  COLOR {
    @Override
    public String getAPIPath() {
      return "pokemon-color";
    }
  },
  ;

  public abstract String getAPIPath();
}
