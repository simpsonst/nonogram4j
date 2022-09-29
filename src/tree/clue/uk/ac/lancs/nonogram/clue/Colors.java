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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

/**
 * Provides utilities for manipulating sets of internal colour numbers.
 *
 * @author simpsons
 */
public final class Colors {
    private Colors() {}

    /**
     * Create an initial working state for a cell in a puzzle with a
     * given number of colours.
     *
     * @param colors the number of colours in the puzzle
     *
     * @return a bit set with bits 0 to <var>n</var>-1 set, where
     * <var>n</var> is the number of colours
     *
     * @throws IllegalArgumentException if the number of colours is too
     * low
     */
    public static BitSet newCell(int colors) {
        if (colors < 2)
            throw new IllegalArgumentException("Insufficient colours");
        BitSet result = new BitSet();
        result.set(0, colors);
        return result;
    }

    /**
     * Create a list of a line's cells within an array. The arguments
     * specify an array, a start position, and the steps required to
     * identify all other positions in the line.
     *
     * @param data the base set of cell states
     *
     * @param start the index into the array of the first cell of the
     * line
     *
     * @param offsets pairs of integers giving the count and step of the
     * next cells
     *
     * @return a list of cell states copied from the supplied data
     *
     * @throws IllegalArgumentException if an offset is non-positive; if
     * the offset array has an odd number of elements
     *
     * @throws IndexOutOfBoundsException if the start position is not
     * within the data array; if a computed offset is not within the
     * data array
     *
     * @throws NullPointerException if either of the array arguments is
     * {@code null}
     */
    public static List<BitSet> listLine(BitSet[] data, int start,
                                        int... offsets) {
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(offsets, "offsets");
        if (offsets.length % 2 != 0)
            throw new IllegalArgumentException("offsets not pair array; length "
                + offsets.length);
        int sum = 1;
        for (int i = 0; i < offsets.length; i += 2) {
            final int os = offsets[i + 1];
            if (os < 1) throw new IllegalArgumentException("offset " + i
                + " non-positive " + os);
            sum += offsets[i];
        }
        List<BitSet> result = new ArrayList<>(sum);
        result.add(copy(data[start]));
        for (int i = 0; i < offsets.length; i += 2) {
            int count = offsets[i];
            int step = offsets[i + 1];
            while (count-- > 0) {
                start += step;
                result.add(copy(data[start]));
            }
        }
        return result;
    }

    /**
     * Create a mutable copy of a line.
     *
     * @param in the source line
     *
     * @return a list of copies of the bit sets from the source line
     */
    public static List<BitSet> copyLine(List<? extends BitSet> in) {
        List<BitSet> result = new ArrayList<>(in.size());
        for (BitSet c : in)
            result.add(copy(c));
        return result;
    }

    /**
     * Copy a cell's state. {@link BitSet#size()} is used to determine
     * how much of the source state to copy, and
     * {@link BitSet#get(int, int)} is used to perform the copy.
     *
     * @param from the cell's state
     *
     * @return the copy of the cell's state
     */
    public static BitSet copy(BitSet from) {
        return from.get(0, from.size());
    }
}
