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
 * Provides utilities for manipulating sets of internal colour numbers.
 * These are always in the range 0 to <var>n</var>&minus;1, where
 * <var>n</var> is the total number of colours. Colour 0 is always
 * interpreted as the background colour, i.e., no block has this colour.
 * {@code long} is used to represent a set of up to 64 colours, where
 * bit <var>i</var> is set if and only if colour <var>i</var> is
 * possible. The static methods of this class help to convert between
 * colour numbers in [0, <var>n</var>) and colour sets held as
 * {@code long}s.
 * 
 * <p>
 * The class {@link CellSequence} provides an abstraction over an array
 * of {@code long}s, providing many of the operations here on a colour
 * set at a given index. {@link CellIterator} permits iteration over
 * such a sequence.
 *
 * @author simpsons
 */
public final class Colors {
    private Colors() {}

    /**
     * Create a colour set of only one colour.
     * 
     * @param color the colour to include
     * 
     * @return the set of the specified colour
     */
    public static long of(int color) {
        return 1 << color;
    }

    /**
     * Create a range of colours.
     * 
     * @param color0 the lowest colour
     * 
     * @param color1 one plus the highest colour
     * 
     * @return the requested set
     */
    public static long ofRange(int color0, int color1) {
        return ~(~0l << (color1 - color0)) << color0;
    }

    /**
     * Get the minimum number of colours required to account for a
     * colour set.
     * 
     * @param colorSet the set whose width is to be determined
     * 
     * @return the width of the set
     */
    public static int width(long colorSet) {
        return 64 - Long.numberOfLeadingZeros(colorSet);
    }

    /**
     * Test whether a colour set includes a specific colour.
     * 
     * @param colorSet the test to be tested
     * 
     * @param color the sought colour
     * 
     * @return {@code true} if the sought colour is in the set;
     * {@code false} otherwise
     * 
     * @see #lacks(long, int) for the opposite sense
     */
    public static boolean has(long colorSet, int color) {
        return (colorSet & (1 << color)) != 0;
    }

    /**
     * Test whether a colour set excludes a specific colour.
     * 
     * @param colorSet the test to be tested
     * 
     * @param color the sought colour
     * 
     * @return {@code false} if the sought colour is in the set;
     * {@code true} otherwise
     * 
     * @see #has(long, int) for the opposite sense
     */
    public static boolean lacks(long colorSet, int color) {
        return (colorSet & (1 << color)) == 0;
    }

    /**
     * Create an initial working state for a cell in a puzzle with a
     * given number of colours.
     * 
     * @param colors the number of colours in the puzzle
     * 
     * @return a long integer with bits 0 to <var>n</var>-1 set, where
     * <var>n</var> is the number of colours
     *
     * @throws IllegalArgumentException if the number of colours is too
     * low
     */
    public static long all(int colors) {
        if (colors < 0)
            throw new IllegalArgumentException("-ve colors: " + colors);
        if (colors > 64)
            throw new IllegalArgumentException("too many colors: " + colors);
        return ~(~0 << colors);
    }

    /**
     * Determine whether a colour set holds fewer than two member.
     * 
     * @param colorSet the set to be tested
     * 
     * @return {@code true} if the set has 0 or 1 members; {@code false}
     * if it has 2 or more
     */
    public static boolean oneLeft(long colorSet) {
        return (colorSet & (colorSet - 1)) == 0;
    }

    /**
     * Indicates an impossible colour. This usually means that all
     * possible colours for a cell have been eliminated, indicating that
     * a puzzle has no solutions. The value is returned from
     * {@link #color(long)} as an invalid (negative) colour number.
     */
    public static final int INCONSISTENT_COLOR = -1;

    /**
     * Indicates an unknown colour. This means that at least two colours
     * have not been eliminated for a cell. The value is returned from
     * {@link #color(long)} as an invalid (negative) colour number.
     */
    public static final int INDETERMINATE_COLOR = -2;

    /**
     * Determine whether a colour set is empty, or has more than one
     * member, or which sole member it contains.
     * 
     * @param colorSet the set to be tested
     * 
     * @return {@link #INCONSISTENT_COLOR} if the set is empty;
     * {@link #INDETERMINATE_COLOR} if the set has more than one member;
     * or the sole member itself
     */
    public static int color(long colorSet) {
        if (colorSet == 0) return INCONSISTENT_COLOR;
        if (!oneLeft(colorSet)) return INDETERMINATE_COLOR;
        return Long.numberOfTrailingZeros(colorSet);
    }
}
