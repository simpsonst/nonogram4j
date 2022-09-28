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

/**
 * Describes a block by its length and colour index.
 * 
 * @author simpsons
 */
public final class Block {
    /**
     * Specifies the block's length. This is always a positive value.
     */
    public final int length;

    /**
     * Specifies the block's colour. As the background colour is always
     * zero, and no block ever uses the background colour, this can
     * never be zero. A colour index is always positive.
     */
    public final int color;

    private Block(int length, int color) {
        assert length >= 1;
        assert color >= 1;
        this.length = length;
        this.color = color;
    }

    /**
     * Create a block with indexed colour.
     * 
     * @param length the block length
     * 
     * @param color the index of the block's colour
     * 
     * @return the new block
     * 
     * @throws IllegalArgumentException if the length is not positive or
     * the colour index is not positive
     */
    public static Block of(int length, int color) {
        if (length < 1)
            throw new IllegalArgumentException("Illegal block length "
                + length);
        if (color < 1)
            throw new IllegalArgumentException("Illegal block color index "
                + color);
        return new Block(length, color);
    }

    /**
     * Create a block with the main foreground colour.
     * 
     * @param length the block length
     * 
     * @return the new block
     * 
     * @throws IllegalArgumentException if the length is not positive or
     * the colour index is not positive
     */
    public static Block of(int length) {
        if (length < 1)
            throw new IllegalArgumentException("Illegal block length "
                + length);
        return new Block(length, 1);
    }

    /**
     * Get a string representation of this block.
     * 
     * @return the string representation
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
        int hash = 3;
        hash = 47 * hash + this.length;
        hash = 47 * hash + this.color;
        return hash;
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
        if (getClass() != obj.getClass()) return false;
        final Block other = (Block) obj;
        if (this.length != other.length) return false;
        return this.color == other.color;
    }
}
