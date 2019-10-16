package com.outofbits.pokemon.pokeapi;

import com.outofbits.pokemon.pokeapi.ld.DefaultBlocks;
import com.outofbits.pokemon.pokeapi.ld.Pipeline;
import com.outofbits.pokemon.pokeapi.ld.Pipeline.Report;
import com.outofbits.pokemon.pokeapi.ld.Pipeline.Report.STATUS;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Entry point to the Command Line Application.
 *
 * @author Kevin Haller
 * @version 1.0
 * @since 1.0
 */
@Component
public class CLIMain {

  private final DefaultBlocks defaultBlocks;

  @Autowired
  public CLIMain(DefaultBlocks defaultBlocks) {
    this.defaultBlocks = defaultBlocks;
  }

  public Map<String, Report> run() {
    return Pipeline.of(defaultBlocks.get()).execute();
  }

  public static void main(String[] args) {
    try {
      int code = 0;
      Map<String, Report> successMap = new AnnotationConfigApplicationContext(
          "com.outofbits.pokemon.pokeapi").getBean(CLIMain.class).run();
      for (Entry<String, Report> entry : successMap.entrySet()) {
        if (entry.getValue().getStatus() == STATUS.FAILED) {
          Exception e = entry.getValue().getException();
          System.err.println(String.format("Failed [%s]: %s", entry.getKey(),
              e != null ? entry.getValue().getException().getMessage() : ""));
          code = 1;
        }
      }
      System.exit(code);
    } catch (Exception e) {
      System.err.println("Failed: " + e.getMessage());
      System.exit(1);
    }
  }

}
