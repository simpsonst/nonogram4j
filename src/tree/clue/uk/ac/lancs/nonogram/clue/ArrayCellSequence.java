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

import java.util.Objects;

/**
 * Presents a cell-sequence view of an array of {@code long}.
 * 
 * @author simpsons
 */
public class ArrayCellSequence implements CellSequence {
    private final long[] base;

    private final int offset;

    private final int length;

    /**
     * Create a cell sequence based on part of an array.
     * 
     * @param base the underlying array
     * 
     * @param offset the offset into the array of the element to appear
     * first in the sequence
     * 
     * @param size the size of the sequence
     * 
     * @throws IllegalArgumentException if the length is negative
     * 
     * @throws IndexOutOfBoundsException if the offset is negative or
     * more than the array length; or if the offset plus the sequence
     * size is more than the array length
     */
    public ArrayCellSequence(long[] base, int offset, int size) {
        Objects.requireNonNull(base, "base");
        if (size < 0) throw new IllegalArgumentException("-ve length " + size);
        if (offset < 0 || offset > base.length)
            throw new IndexOutOfBoundsException(offset);
        if (offset + size > base.length)
            throw new IndexOutOfBoundsException(offset + size);
        this.base = base;
        this.offset = offset;
        this.length = size;
    }

    /**
     * Create a cell sequence based on an array.
     * 
     * @param base the underlying array
     */
    public ArrayCellSequence(long[] base) {
        this.base = base;
        this.offset = 0;
        this.length = base.length;
    }

    /**
     * Create a cell sequence based on its own array.
     * 
     * @param size the sequence size
     */
    public ArrayCellSequence(int size) {
        this(new long[size]);
    }

    private void check(int index) {
        if (index < 0 || index >= length)
            throw new IndexOutOfBoundsException(index);
    }

    @Override
    public long get(int index) {
        check(index);
        return base[offset + index];
    }

    @Override
    public long put(int index, long colorSet) {
        check(index);
        final long old = base[offset + index];
        base[offset + index] = colorSet;
        return old;
    }

    private final CellSequence reverseView = new ReverseCellSequence(this);

    @Override
    public CellSequence reverse() {
        return reverseView;
    }

    @Override
    public CellSequence subsequence(int fromIndex, int toIndex) {
        if (toIndex < fromIndex)
            throw new IllegalArgumentException("to before from: " + toIndex
                + "<" + fromIndex);
        if (fromIndex < 0 || fromIndex > length)
            throw new IndexOutOfBoundsException(fromIndex);
        if (toIndex < 0 || toIndex > length)
            throw new IndexOutOfBoundsException(toIndex);
        return new ArrayCellSequence(base, offset + fromIndex,
                                     toIndex - fromIndex);
    }

    @Override
    public int size() {
        return length;
    }

    @Override
    public long addAll(int index, long colorSet) {
        check(index);
        final long old = base[offset + index];
        base[offset + index] |= colorSet;
        return old;
    }

    @Override
    public long toggleAll(int index, long colorSet) {
        check(index);
        final long old = base[offset + index];
        base[offset + index] ^= colorSet;
        return old;
    }

    @Override
    public long retainAll(int index, long colorSet) {
        check(index);
        final long old = base[offset + index];
        base[offset + index] &= colorSet;
        return old;
    }

    @Override
    public long removeAll(int index, long colorSet) {
        check(index);
        final long old = base[offset + index];
        base[offset + index] &= ~colorSet;
        return old;
    }
}
