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
 *
 * @author simpsons
 */
class ReverseCellSequence implements CellSequence {
    private final CellSequence base;

    public ReverseCellSequence(CellSequence base) {
        this.base = base;
    }

    private int invert(int index) {
        return base.size() - 1 - index;
    }

    @Override
    public long get(int index) {
        return base.get(invert(index));
    }

    @Override
    public long put(int index, long colorSet) {
        return base.put(invert(index), colorSet);
    }

    @Override
    public CellSequence reverse() {
        return base;
    }

    @Override
    public CellSequence subsequence(int fromIndex, int toIndex) {
        return new ReverseCellSequence(base
            .subsequence(base.size() - toIndex, base.size() - fromIndex));
    }

    @Override
    public int size() {
        return base.size();
    }

    @Override
    public int color(int index) {
        return base.color(invert(index));
    }

    @Override
    public long addAll(int index, long colorSet) {
        return base.addAll(invert(index), colorSet);
    }

    @Override
    public long toggleAll(int index, long colorSet) {
        return base.toggleAll(invert(index), colorSet);
    }

    @Override
    public long retainAll(int index, long colorSet) {
        return base.retainAll(invert(index), colorSet);
    }

    @Override
    public long removeAll(int index, long colorSet) {
        return base.removeAll(invert(index), colorSet);
    }
}
