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

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Defines the constraints on a line of cells. A line with no
 * constraints can also be represented. The state of this class is
 * immutable.
 * 
 * @author simpsons
 */
public final class Clue {
    private final List<Bar> blocks;

    /**
     * Get the blocks constraining the state of a line.
     * 
     * @return a list of the lengths and colours of the blocks, or
     * {@code null} if not defined
     */
    public List<Bar> blocks() {
        return blocks;
    }

    private Clue(List<Bar> blocks) {
        this.blocks = blocks;
    }

    private static final Clue UNSPECIFIED = new Clue(null);

    /**
     * Get a clue representing a lack of constraints on a line.
     * 
     * @return a constraint-less clue
     * 
     * @constructor
     */
    public static Clue unspecified() {
        return UNSPECIFIED;
    }

    /**
     * Get a clue constraining a line to a sequence of blocks of known
     * size and colour, but unknown position. The supplied collection is
     * copied in its natural iteration order.
     * 
     * @param blocks a collection of blocks to form the sequence
     * 
     * @return the requested clue
     */
    public static Clue of(Collection<? extends Bar> blocks) {
        return new Clue(List.copyOf(blocks));
    }

    /**
     * Get the hash code for this object.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(blocks);
    }

    /**
     * Test whether this clue equals another object.
     * 
     * @param obj the other object
     * 
     * @return {@code true} if the other object is a clue with the same
     * block sequence; {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Clue other = (Clue) obj;
        return Objects.equals(this.blocks, other.blocks);
    }
}
