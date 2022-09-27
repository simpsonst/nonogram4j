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

package uk.ac.lancs.nonogram.line.fast;

import java.util.List;
import uk.ac.lancs.nonogram.Block;
import uk.ac.lancs.nonogram.line.heuristic.LineHeuristic;

/**
 * Computes a line priority based on how many cells the fast
 * line-solving algorithm would discover on first pass.
 * 
 * @author simpsons
 */
public final class FastLineHeuristic implements LineHeuristic {
    private FastLineHeuristic() {}

    /**
     * The prefix for configuration strings for this heuristic, namely *
     * * {@value}
     */
    public static final String HEURISTIC_TYPE = "fast";

    /**
     * This implementation first sums the block lengths, adding minimum
     * space between adjacent blocks of the same colour. It then
     * subtracts this from the line length to compute a shortfall. This
     * shortfall is then subtracted from each block length, and the
     * differences are summed.
     * 
     * @resume {@inheritDoc}
     * 
     * @param lineLength {@inheritDoc}
     * 
     * @param clue {@inheritDoc}
     * 
     * @return {@inheritDoc}
     */
    @Override
    public int compute(int lineLength, List<? extends Block> clue) {
        if (clue.isEmpty()) return lineLength;

        /* Work out the total minimum length of the line. */
        int sum = 0;
        int lastCol = -1;
        for (Block block : clue) {
            if (lastCol == block.color) sum++;
            sum += block.length;
            lastCol = block.color;
        }

        /* Subtract from the line length. */
        final int gap = lineLength - sum;

        /* Subtract gap from each block length. */
        return sum - clue.size() * gap;
    }

    /**
     * This class has no internal state.
     * 
     * @resume The sole instance of this class
     */
    public static final FastLineHeuristic INSTANCE = new FastLineHeuristic();
}
