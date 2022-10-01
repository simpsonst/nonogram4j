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

import java.util.NoSuchElementException;

/**
 * Represents a fixed-length sequence of colour sets.
 *
 * @author simpsons
 */
public interface CellSequence {
    /**
     * Get the colour set at a given index.
     * 
     * @param index the index
     * 
     * @return the colour set
     * 
     * @throws IndexOutOfBoundsException if the index is negative or not
     * less than the size
     */
    long get(int index);

    /**
     * Get the width of the set at a given index.
     * 
     * @param index the index
     * 
     * @return the width of the colour set
     * 
     * @throws IndexOutOfBoundsException if the index is negative or not
     * less than the size
     * 
     * @default The default behaviour passes the result of
     * {@link #get(int)} to {@link Colors#width(long)}.
     */
    default int width(int index) {
        return Colors.width(get(index));
    }

    default boolean has(int index, int color) {
        return Colors.has(get(index), color);
    }

    default boolean lacks(int index, int color) {
        return Colors.lacks(get(index), color);
    }

    default CellIterator iterator() {
        return iterator(0, size());
    }

    default CellIterator iterator(int fromIndex, final int toIndex) {
        if (fromIndex < 0 || fromIndex > size())
            throw new IndexOutOfBoundsException(fromIndex);
        if (toIndex < 0 || toIndex > size())
            throw new IndexOutOfBoundsException(toIndex);
        return new CellIterator() {
            int pos = fromIndex;

            @Override
            public void next() {
                if (pos == toIndex) throw new NoSuchElementException();
                pos++;
            }

            @Override
            public long get() {
                return CellSequence.this.get(pos);
            }

            @Override
            public long put(long colorSet) {
                return CellSequence.this.put(pos, colorSet);
            }

            @Override
            public int at() {
                return pos;
            }

            @Override
            public long addAll(long colorSet) {
                return CellSequence.this.addAll(pos, colorSet);
            }

            @Override
            public long removeAll(long colorSet) {
                return CellSequence.this.removeAll(pos, colorSet);
            }

            @Override
            public long toggleAll(long colorSet) {
                return CellSequence.this.toggleAll(pos, colorSet);
            }

            @Override
            public long retainAll(long colorSet) {
                return CellSequence.this.retainAll(pos, colorSet);
            }

            @Override
            public boolean more() {
                return pos < toIndex;
            }

            @Override
            public int width() {
                return CellSequence.this.width(pos);
            }

            @Override
            public boolean has(int color) {
                return CellSequence.this.has(pos, color);
            }

            @Override
            public boolean lacks(int color) {
                return CellSequence.this.lacks(pos, color);
            }

            @Override
            public int color() {
                return CellSequence.this.color(pos);
            }
        };
    }

    /**
     * Replace the colour set at a given index.
     * 
     * @param index the index
     * 
     * @param colorSet the new colour set
     * 
     * @return the old colour set
     * 
     * @throws IndexOutOfBoundsException if the index is negative or not
     * less than the size
     */
    long put(int index, long colorSet);

    /**
     * Get the reversed view of this sequence.
     * 
     * @return the reversed view
     */
    CellSequence reverse();

    /**
     * Get a subsequence view of this sequence.
     * 
     * @param fromIndex the index of the first colour set to appear in
     * the result
     * 
     * @param toIndex one plus the index of the last colour set to
     * appear in the result
     * 
     * @return the requested subsequence view
     * 
     * @throws IllegalArgumentException if the to-index is less than the
     * from-index
     * 
     * @throws IndexOutOfBoundsException if either index is negative or
     * greater than the size
     */
    CellSequence subsequence(int fromIndex, int toIndex);

    /**
     * Get a read-only view of this sequence.
     * 
     * @return a new read-only view
     */
    default CellSequence readOnly() {
        return new ReadOnlyCellSequence(this);
    }

    /**
     * Get the size of the sequence.
     * 
     * @return the sequence size
     */
    int size();

    /**
     * Get the colour number of the colour set at a given index.
     * 
     * @param index the index
     * 
     * @return the colour if the set only contains that colour;
     * {@link Colors#INCONSISTENT_COLOR} if the set is empty; or
     * {@link Colors#INDETERMINATE_COLOR} otherwise
     * 
     * @throws IndexOutOfBoundsException if either index is negative or
     * greater than the size
     * 
     * @default By default, this invokes {@link #get(int)}, and passes
     * the result to {@link Colors#color(long)}.
     */
    default int color(int index) {
        return Colors.color(get(index));
    }

    /**
     * Add colours from another set to that at a given index.
     * 
     * @param index the index
     * 
     * @param colorSet the colours to add
     * 
     * @return the old colour set
     * 
     * @throws IndexOutOfBoundsException if either index is negative or
     * greater than the size
     * 
     * @default By default, this uses {@link #get(int)} to get the
     * current value, modifies as specified, and writes it back with
     * {@link #put(int, long)}.
     */
    default long addAll(int index, long colorSet) {
        return put(index, get(index) | colorSet);
    }

    /**
     * Invert the presence of colours from another set in that at a
     * given index.
     * 
     * @param index the index
     * 
     * @param colorSet the colours to invert
     * 
     * @return the old colour set
     * 
     * @throws IndexOutOfBoundsException if either index is negative or
     * greater than the size
     * 
     * @default By default, this uses {@link #get(int)} to get the
     * current value, modifies as specified, and writes it back with
     * {@link #put(int, long)}.
     */
    default long toggleAll(int index, long colorSet) {
        return put(index, get(index) ^ colorSet);
    }

    /**
     * Retain colours of another set in that at a given index. Other
     * colours are removed.
     * 
     * @param index the index
     * 
     * @param colorSet the colours to retain
     * 
     * @return the old colour set
     * 
     * @throws IndexOutOfBoundsException if either index is negative or
     * greater than the size
     * 
     * @default By default, this uses {@link #get(int)} to get the
     * current value, modifies as specified, and writes it back with
     * {@link #put(int, long)}.
     */
    default long retainAll(int index, long colorSet) {
        return put(index, get(index) & colorSet);
    }

    /**
     * Remove colours of another set from that at a given index.
     * 
     * @param index the index
     * 
     * @param colorSet the colours to remove
     * 
     * @return the old colour set
     * 
     * @throws IndexOutOfBoundsException if either index is negative or
     * greater than the size
     * 
     * @default By default, this uses {@link #get(int)} to get the
     * current value, modifies as specified, and writes it back with
     * {@link #put(int, long)}.
     */
    default long removeAll(int index, long colorSet) {
        return put(index, get(index) & ~colorSet);
    }
}
