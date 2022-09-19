// -*- c-basic-offset: 4; indent-tabs-mode: nil -*-

package uk.ac.lancs.nonogram.geom;

import java.util.List;

import uk.ac.lancs.nonogram.IndexedBlock;

/**
 * Describes a line within a puzzle.
 * 
 * @author simpsons
 */
public interface Line {
    /**
     * Get the clue for the line.
     * 
     * @return the line's clue, or {@code null} if not defined
     */
    List<IndexedBlock> clue();

    /**
     * Get the state of all cells in the line, and details of which
     * other lines intersect.
     * 
     * @return the cells of the line
     */
    List<Cell> cells();
}
