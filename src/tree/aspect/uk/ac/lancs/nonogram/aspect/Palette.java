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

package uk.ac.lancs.nonogram.aspect;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import uk.ac.lancs.nonogram.clue.Colors;

/**
 * Represents the selection of colours and forms used to display a
 * Nonogram image.
 * 
 * @author simpsons
 */
public final class Palette {
    private final List<Tile> colors;

    private final Tile unknown;

    /**
     * Prepare to create a palette.
     * 
     * @return an empty palette configuration
     * 
     * @constructor
     */
    public static Builder start() {
        return new Builder();
    }

    /**
     * @resume A mutable configuration for a palette
     * 
     * @author simpsons
     */
    public static final class Builder {
        private Builder() {}

        private Tile unknown = Tile.WORKING_MONOCHROME_UNKNOWN;

        private final List<Tile> colors = new ArrayList<>();

        /**
         * Add a tile to this palette. It will occupy the next available
         * colour number.
         * 
         * @param tile the tile to be added
         * 
         * @return this builder
         */
        public Builder add(Tile tile) {
            colors.add(tile);
            return this;
        }

        /**
         * Set the ‘unknown’ colour. The default is
         * {@link Tile#WORKING_MONOCHROME_UNKNOWN}.
         * 
         * @param tile the new ‘unknown’ colour
         * 
         * @return this builder
         */
        public Builder setUnknown(Tile tile) {
            unknown = tile;
            return this;
        }

        /**
         * Create an immutable palette from this configuration. The
         * configuration can be used to create other palettes without
         * affecting the one created here.
         * 
         * @return the new palette
         */
        public Palette create() {
            return new Palette(this);
        }
    }

    /**
     * Create a palette from mutable configuration. The state is copied,
     * so the configuration can be used to build other palettes.
     * 
     * @param builder the mutable configuration
     */
    private Palette(Builder builder) {
        this.colors = new ArrayList<>(builder.colors);
        this.unknown = builder.unknown;
    }

    /**
     * Create an initial working state for a cell in a puzzle using this
     * palette.
     * 
     * @return a bit set with bits 0 to <var>n</var>-1 set, where
     * <var>n</var> is the number of colours
     */
    public BitSet createWorkingState() {
        return Colors.newCell(colors());
    }

    /**
     * Get the number of colours in this palette.
     * 
     * @return the number of colours in this palette
     */
    public int colors() {
        return colors.size();
    }

    /**
     * Get the tile type for unknown cells.
     * 
     * @return the tile type for unknown cells
     */
    public Tile getUnknown() {
        return unknown;
    }

    /**
     * Get the tile type for a given colour number.
     * 
     * @param code the colour number
     * 
     * @return the tile for the given colour number
     * 
     * @throws IndexOutOfBoundsException if the colour number is out of
     * range
     */
    public Tile getColor(int code) {
        return colors.get(code);
    }

    /**
     * Get a tile type to represent a set of possible cell colours.
     * 
     * @param allowed the set of possible cell colours
     * 
     * @return if only one colour is possible, the tile type for that
     * colour; otherwise, the ‘unknown’ tile type
     * 
     * @throws IllegalArgumentException if no colours are possible
     */
    public Tile getColor(BitSet allowed) {
        int first = allowed.nextSetBit(0);
        if (first == -1)
            throw new IllegalArgumentException("No colours possible");
        if (first >= colors()) throw new IllegalArgumentException("Colour "
            + first + " out of range (max " + (colors() - 1) + ")");
        int next = allowed.nextSetBit(first + 1);
        if (next == -1) return getColor(first);
        if (next >= colors()) throw new IllegalArgumentException("Colour "
            + next + " out of range (max " + (colors() - 1) + ")");
        return getUnknown();
    }

    /**
     * This consists of white for unknown cells, black for solids, and
     * white with a dot for background cells.
     * 
     * @resume The standard palette for solving a monochrome puzzle
     * 
     * @see Tile#WORKING_MONOCHROME_UNKNOWN
     * 
     * @see Tile#WORKING_MONOCHROME_EMPTY
     * 
     * @see Tile#WORKING_MONOCHROME_FILLED
     */
    public static final Palette WORKING_MONOCHROME_PALETTE =
        Palette.start().setUnknown(Tile.WORKING_MONOCHROME_UNKNOWN)
            .add(Tile.WORKING_MONOCHROME_EMPTY)
            .add(Tile.WORKING_MONOCHROME_FILLED).create();

    /**
     * This consists of {@code null} for unknown cells, black for
     * solids, and white for background cells.
     * 
     * @resume The standard palette for a completed monochrome puzzle
     * 
     * @see Tile#COMPLETE_MONOCHROME_EMPTY
     * 
     * @see Tile#COMPLETE_MONOCHROME_FILLED
     */
    public static final Palette COMPLETE_MONOCHROME_PALETTE =
        Palette.start().setUnknown(null).add(Tile.COMPLETE_MONOCHROME_EMPTY)
            .add(Tile.COMPLETE_MONOCHROME_FILLED).create();
}
