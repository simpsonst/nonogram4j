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

package uk.ac.lancs.nonogram;

import java.util.BitSet;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Identifies a cell in a puzzle, and all lines that intersect at it.
 * 
 * @author simpsons
 */
public interface Cell {
    /**
     * Create a map out of a collection of cell descriptions.
     *
     * @param cells the cell descriptions, such as returned by
     * {@link Layout#getCells()}
     *
     * @return a map of the cells indexed by each cell's
     * {@link Cell#index() index}
     */
    public static NavigableMap<Integer, Cell>
        createCellMap(Iterable<? extends Cell> cells) {
        TreeMap<Integer, Cell> cellMap = new TreeMap<>();
        for (Cell cell : cells)
            cellMap.put(cell.index(), cell);
        return cellMap;
    }

    /**
     * Get the cell's identifier.
     * 
     * @return the cell's unique identifier
     */
    int index();

    /**
     * Get the set of indices of lines intersecting this cell. If this
     * cell was obtained by first indexing a line, the line's index need
     * not appear in the collection.
     * 
     * @return a bit set whose indices indicate lines intersecting this
     * cell
     */
    BitSet intersects();
}