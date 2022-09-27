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

package uk.ac.lancs.nonogram.geom.rect;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;
import uk.ac.lancs.nonogram.aspect.Palette;

/**
 * The width and height are fixed. All other state (cell colours,
 * palette, activity and algorithmic levels) is mutable.
 * 
 * @resume The state shared with all types of two-dimensional Nonogram
 * widgets
 * 
 * @author simpsons
 */
final class DisplayState {
    /**
     * @resume The puzzle's width
     */
    final int width;

    /**
     * @resume The puzzle's height
     */
    final int height;

    /**
     * @resume The number of line-solving algorithms being applied to
     * each line
     */
    final int algos;

    /**
     * The initial value for each cell is -1.
     * 
     * @resume The state of each cell, listed row-by-row
     */
    final int[] cellColours;

    /**
     * The initial value for each line is zero.
     * 
     * @resume The level-indicator states for each row, then each column
     */
    final int[] levels;

    /**
     * The initial value for each line is {@code false}.
     * 
     * @resume The activity-indicator states for each row, then each
     * column
     */
    final BitSet activities = new BitSet();

    /**
     * The initial value is {@link Palette#WORKING_MONOCHROME_PALETTE}.
     * 
     * @resume The current palette
     */
    Palette palette = Palette.WORKING_MONOCHROME_PALETTE;

    /**
     * Create a fresh display state.
     * 
     * @param width the puzzle's width
     * 
     * @param height the puzzle's height
     */
    public DisplayState(int width, int height, int algos) {
        this.width = width;
        this.height = height;
        this.algos = algos;
        cellColours = new int[width * height];
        Arrays.fill(cellColours, 2);
        levels = new int[width + height];

        if (false) {
            Random rng = new Random();
            for (int i = 0; i < cellColours.length; i++)
                cellColours[i] = rng.nextInt(3);
            for (int i = 0; i < levels.length; i++) {
                levels[i] = rng.nextInt(algos + 1);
                activities.set(i, rng.nextBoolean());
            }
        }
    }
}
