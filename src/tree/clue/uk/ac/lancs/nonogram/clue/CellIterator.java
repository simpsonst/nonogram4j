// -*- c-basic-offset: 4; indent-tabs-mode: nil -*-

/*
 * Copyright (c) 2022, Lancaster University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the
 *   distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 *  Author: Steven Simpson <s.simpson@lancaster.ac.uk>
 */

package uk.ac.lancs.nonogram.clue;

/**
 * Iterates over a cell sequence. The idiom is:
 * 
 * <pre>
 * CellSequence seq = <var>...</var>;
 * for (CellIterator iter = seq.iterator(); iter.more(); iter.next()) {
 *   <var>...</var>
 * }
 * </pre>
 * 
 * @see CellSequence
 * 
 * @see Colors
 * 
 * @author simpsons
 */
public interface CellIterator {
    /**
     * Move to the next position in the cell sequence.
     */
    void next();

    /**
     * Get the colour set of the current cell.
     * 
     * @return the colour set
     * 
     * @see CellSequence#get(int)
     */
    long get();

    /**
     * Replace the colour set of the current cell.
     * 
     * @param colorSet the new colour set
     * 
     * @return the old colour set
     * 
     * @see CellSequence#put(int, long)
     */
    long put(long colorSet);

    /**
     * Get the current position.
     * 
     * @return the current position
     */
    int at();

    /**
     * Add colours of another set to the current cell's set.
     * 
     * @param colorSet the colours to add
     * 
     * @return the old colour set
     * 
     * @see CellSequence#addAll(int, long)
     */
    long addAll(long colorSet);

    /**
     * Remove colours of another set from the current cell's set.
     * 
     * @param colorSet the colours to remove
     * 
     * @return the old colour set
     * 
     * @see CellSequence#removeAll(int, long)
     */
    long removeAll(long colorSet);

    /**
     * Invert the presence of colours of another set in the current
     * cell's set.
     * 
     * @param colorSet the colours to invert
     * 
     * @return the old colour set
     * 
     * @see CellSequence#toggleAll(int, long)
     */
    long toggleAll(long colorSet);

    /**
     * Retain colours of another set in the current cell's set.
     * 
     * @param colorSet the colours to retain
     * 
     * @return the old colour set
     * 
     * @see CellSequence#retainAll(int, long)
     */
    long retainAll(long colorSet);

    /**
     * Get the width of the current cell's set.
     * 
     * @return the width of the set
     * 
     * @see Colors#width(long)
     * 
     * @see CellSequence#width(int)
     */
    int width();

    /**
     * Test whether the current cell's set includes a colour.
     * 
     * @param color the sought colour
     * 
     * @return {@code true} if the cell's set includes the colour;
     * {@code false} otherwise
     * 
     * @see Colors#has(long, int)
     * 
     * @see CellSequence#has(int, int)
     */
    boolean has(int color);

    /**
     * Test whether the current cell's set excludes a colour.
     * 
     * @param color the sought colour
     * 
     * @return {@code true} if the cell's set excludes the colour;
     * {@code false} otherwise
     * 
     * @default By default, {@link #has(int)} is invoked, and the result
     * inverted.
     * 
     * @see Colors#lacks(long, int)
     * 
     * @see CellSequence#lacks(int, int)
     */
    default boolean lacks(int color) {
        return !has(color);
    }

    /**
     * Test whether there are more elements in the iteration.
     * 
     * @return {@code true} if there are more elements; {@code false}
     * otherwise
     */
    boolean more();

    /**
     * Get the colour number of the current cell's colour set.
     * 
     * @return the colour if the set only contains that colour;
     * {@link Colors#INCONSISTENT_COLOR} if the set is empty; or
     * {@link Colors#INDETERMINATE_COLOR} otherwise
     * 
     * @see Colors#color(long)
     * 
     * @see CellSequence#color(int)
     */
    int color();
}
