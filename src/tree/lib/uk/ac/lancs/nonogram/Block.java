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

import java.util.Objects;

/**
 * Represents a coloured block in a Nonogram clue.
 * 
 * @author simpsons
 */
public final class Block {
    /**
     * Specifies the block's length. This is always a positive value.
     * 
     * @resume The length of the block
     */
    public final int length;

    /**
     * Specifies the block's colour.
     * 
     * @resume The colour of the block
     */
    public final Hue color;

    private Block(int length, Hue color) {
        assert length >= 1;
        assert color != null;
        assert color != Hue.BACKGROUND;
        assert color != Hue.UNKNOWN;
        this.length = length;
        this.color = color;
    }

    /**
     * Create a block of a given length and colour.
     * 
     * @param length the block's length, a positive integer
     * 
     * @param color the block's colour, a positive integer
     * 
     * @return the new block
     * 
     * @throws IllegalArgumentException if the length or colour are
     * invalid
     * 
     * @throws NullPointerException if the colour is {@code null}
     */
    public static Block of(int length, Hue color) {
        if (length < 1)
            throw new IllegalArgumentException("Illegal block length "
                + length);
        Objects.requireNonNull(color, "color");
        if (color == Hue.BACKGROUND)
            throw new IllegalArgumentException("Illegal color (background)");
        if (color == Hue.UNKNOWN)
            throw new IllegalArgumentException("Illegal color (unknown)");
        return new Block(length, color);
    }

    /**
     * Get a string representation of this block.
     * 
     * @return the string representation of this block
     */
    @Override
    public String toString() {
        return Integer.toString(length) + '(' + color + ')';
    }

    /**
     * Get the hash code for this block.
     * 
     * @return this block's hash code
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + color.hashCode();
        result = prime * result + length;
        return result;
    }

    /**
     * Determine whether this block is equivalent to another object.
     * 
     * @param obj the other object
     * 
     * @return true if the other object is a block of the same length
     * and colour
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Block)) return false;
        Block other = (Block) obj;
        if (color != other.color) return false;
        if (length != other.length) return false;
        return true;
    }
}
