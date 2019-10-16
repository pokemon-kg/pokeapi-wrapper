package com.outofbits.pokemon.pokeapi.ld;

import static com.google.common.base.Preconditions.checkArgument;

import com.outofbits.pokemon.pokeapi.ld.exceptions.PipelineBlockExecutionException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A pipeline helps to process a number of {@link PipelineBlock}s. It processes a given list of
 * {@link PipelineBlock}s sequentially.
 *
 * @author Kevin Haller
 * @version 1.0
 * @since 1.0
 */
public final class Pipeline {

  private static final Logger logger = LoggerFactory.getLogger(Pipeline.class);

  private final List<PipelineBlock> blocks;

  private Pipeline(List<PipelineBlock> blocks) {
    checkArgument(blocks != null);
    this.blocks = new LinkedList<>(blocks);
  }

  public static Pipeline of(List<PipelineBlock> blocks) {
    return new Pipeline(blocks);
  }

  /**
   * Executes this pipeline and returns success map storing whether a block ahs been executed
   * successfully. The key of the success map is the {@link PipelineBlock#name()} of the block and
   * the value is {@link Report#success()}, if the block has been executed successfully, otherwise
   * {@link Report#failed(Exception)}}.
   *
   * @return success map storing whether a block ahs been executed successfully.
   */
  public Map<String, Report> execute() {
    Map<String, Report> successMap = new HashMap<>();
    for (PipelineBlock block : blocks) {
      try {
        logger.info("Block [{}] is now being processed in the pipeline.", block.name());
        block.run(PokeAPIDataset.get());
        successMap.put(block.name(), Report.success());
        logger.info("Block [{}] has been processed successfully.", block.name());
      } catch (PipelineBlockExecutionException e) {
        logger.info("Block [{}] processing failed.", block.name());
        successMap.put(block.name(), Report.failed(e));
      }
    }
    return successMap;
  }

  public static class Report {

    public enum STATUS {SUCCESS, FAILED}

    private final STATUS status;
    private final Exception exception;

    private Report(STATUS status) {
      this(status, null);
    }

    private Report(STATUS status, Exception exception) {
      this.status = status;
      this.exception = exception;
    }

    static Report success() {
      return new Report(STATUS.SUCCESS);
    }

    static Report failed(Exception exception) {
      return new Report(STATUS.FAILED, exception);
    }

    public STATUS getStatus() {
      return status;
    }

    public Exception getException() {
      return exception;
    }
  }

}
