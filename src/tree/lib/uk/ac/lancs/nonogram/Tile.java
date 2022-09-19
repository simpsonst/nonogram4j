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

import java.awt.Color;

/**
 * Describes how to colour a cell of a Nonogram.
 * 
 * @author simpsons
 */
public final class Tile {
    /**
     * @resume The foreground colour, used to draw the shape
     * 
     * @see #shape
     */
    public final Color foreground;

    /**
     * @resume The background colour, used to fill the cell
     */
    public final Color background;

    /**
     * @resume The shape, plotted in the foreground colour
     * 
     * @see #foreground
     */
    public final Shape shape;

    /**
     * Create a tile from colours and a shape.
     * 
     * @param foreground the tile's foreground colour
     * 
     * @param background the tile's background colour
     * 
     * @param shape the tile's shape
     * 
     * @return the specified tile
     */
    public static Tile of(Color foreground, Color background, Shape shape) {
        return new Tile(foreground, background, shape);
    }

    private Tile(Color foreground, Color background, Shape shape) {
        this.foreground = foreground;
        this.background = background;
        this.shape = shape;
    }

    /**
     * Get the hash code for this tile.
     * 
     * @return the hash code for this tile
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((background == null) ? 0 : background.hashCode());
        result =
            prime * result + ((foreground == null) ? 0 : foreground.hashCode());
        result = prime * result + ((shape == null) ? 0 : shape.hashCode());
        return result;
    }

    /**
     * Determine whether this tile is equivalent to another object.
     * 
     * @param obj the other object
     * 
     * @return true if the other object is a tile of the same colours
     * and shape
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Tile)) return false;
        Tile other = (Tile) obj;
        if (background == null) {
            if (other.background != null) return false;
        } else if (!background.equals(other.background)) return false;
        if (foreground == null) {
            if (other.foreground != null) return false;
        } else if (!foreground.equals(other.foreground)) return false;
        if (shape != other.shape) return false;
        return true;
    }

    /**
     * @resume The tile type for an unknown cell during the solving of a
     * simple monochrome puzzle
     */
    public static final Tile WORKING_MONOCHROME_UNKNOWN =
        Tile.of(Color.BLACK, Color.WHITE, Shape.BLANK);

    /**
     * @resume The tile type for an empty cell during the solving of a
     * simple monochrome puzzle
     */
    public static final Tile WORKING_MONOCHROME_EMPTY =
        Tile.of(Color.BLACK, Color.WHITE, Shape.DOT);

    /**
     * @resume The tile type for a filled cell during the solving of a
     * simple monochrome puzzle
     */
    public static final Tile WORKING_MONOCHROME_FILLED =
        Tile.of(Color.BLACK, Color.WHITE, Shape.SOLID);

    /**
     * @resume The tile type for an empty cell in a completed, simple
     * monochrome puzzle
     */
    public static final Tile COMPLETE_MONOCHROME_EMPTY =
        WORKING_MONOCHROME_UNKNOWN;

    /**
     * @resume The tile type for a filled cell in a completed, simple
     * monochrome puzzle
     */
    public static final Tile COMPLETE_MONOCHROME_FILLED =
        WORKING_MONOCHROME_FILLED;
}
