// -*- c-basic-offset: 4; indent-tabs-mode: nil -*-

/*
 * Copyright (c) 2011,2022, Lancaster University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the
 *    distribution.
 * 
 *  * Neither the name of the copyright holder nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package uk.ac.lancs.nonogram.line;

import uk.ac.lancs.nonogram.IndexedBlock;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * Holds the state of a line for working on in isolation.
 * 
 * @author simpsons
 */
public class LineWorkUnit {
    /**
     * @resume The number of colours in the puzzle
     */
    public final int colors;

    /**
     * This is an unmodifiable collection of immutable entries.
     * 
     * @resume The line's clue
     */
    public final List<IndexedBlock> clue;

    /**
     * This is an unmodifiable collection of mutable entries.
     * 
     * <p>
     * Each entry is a bit set, with each bit indicating whether a
     * certain colour can still appear in that cell. The provided state
     * gives the cells' current states before submission to a line
     * solver, and they can be modified by the line solver to indicate
     * the new state, by clearing bits for colours which have been
     * eliminated. No additional bits should be set by the solver.
     * 
     * @resume The state of the cells
     */
    public final List<BitSet> cells;

    /**
     * @resume The cache of line-solver states pertaining to this line
     */
    public final Cache cache;

    /**
     * Create a structure to hold the state of a line for solving.
     * 
     * @param colors the number of colours in the source puzzle
     * 
     * @param clue the line's clue
     * 
     * @param cells the initial state of the cells, whose entries are to
     * be modified in-place
     * 
     * @param cache the cache of line-solver states pertaining to this
     * line
     */
    public LineWorkUnit(int colors, List<IndexedBlock> clue, List<BitSet> cells,
                        Cache cache) {
        this.colors = colors;
        this.cells = Collections.unmodifiableList(cells);
        this.clue = Collections.unmodifiableList(clue);
        this.cache = cache;
    }
}
