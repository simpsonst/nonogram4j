// -*- c-basic-offset: 4; indent-tabs-mode: nil -*-

package uk.ac.lancs.nonogram;

import uk.ac.lancs.nonogram.clue.Block;
import java.util.List;

/**
 * Describes a line within a puzzle.
 * 
 * @author simpsons
 */
public interface Line {
    /**
     * Get an immutable description of the clue for the line.
     * 
     * @return the line's clue, or {@code null} if not defined
     */
    List<Block> clue();

    /**
     * Get an immutable description of the cell population of the line.
     * 
     * @return the cells of the line
     */
    List<Cell> cells();
}
