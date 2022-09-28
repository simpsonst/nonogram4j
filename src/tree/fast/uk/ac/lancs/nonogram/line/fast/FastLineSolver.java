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

package uk.ac.lancs.nonogram.line.fast;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicReference;
import uk.ac.lancs.nonogram.line.LineAlgorithm;
import uk.ac.lancs.nonogram.line.LineSolver;
import uk.ac.lancs.nonogram.line.LineChallenge;
import uk.ac.lancs.nonogram.util.ReversedList;

final class FastLineSolver implements LineSolver {
    private final LineChallenge line;

    /**
     * Create a fast line solver.
     * 
     * @param line the line to be solved
     */
    public FastLineSolver(LineChallenge line) {
        this.line = line;
    }

    private volatile boolean aborted;

    private volatile boolean done;

    private final AtomicReference<Thread> user = new AtomicReference<>();

    private static final BitSet BACKGROUND = new BitSet();

    static {
        BACKGROUND.set(0);
    }

    private Result innerProcess() {
        final int clueLength = line.clue.size();
        final int lineLength = line.cells.size();

        /* If there are no blocks, everything is background. */
        if (clueLength == 0) {
            for (BitSet cell : line.cells) {
                if (!cell.get(0)) return Result.INCONSISTENT;
                cell.andNot(BACKGROUND);
            }
            return Result.EXHAUSTED;
        }

        /* Push everything to the left. */
        final int[] minStart = new int[clueLength];
        if (FastLineAlgorithm.push(minStart, line.cells, line.clue))
            return Result.INCONSISTENT;
        if (aborted) return Result.ABORTED;

        /* Push everything to the right. */
        final int[] revMinStart = new int[clueLength];
        final boolean rightState =
            FastLineAlgorithm.push(revMinStart,
                                   new ReversedList<>(line.cells),
                                   new ReversedList<>(line.clue));
        assert !rightState;
        if (aborted) return Result.ABORTED;

        /* Translate the right positions. */
        final int[] minEnd = new int[clueLength];
        final int[] maxStart = new int[clueLength];
        final int[] maxEnd = new int[clueLength];
        for (int i = 0; i < maxStart.length; i++) {
            final int blockLength = line.clue.get(i).length;
            minEnd[i] = minStart[i] + blockLength;
            maxEnd[i] = lineLength - revMinStart[clueLength - 1 - i];
            maxStart[i] = maxEnd[i] - blockLength;
        }

        /* Compare the two arrays of block positions to work out what
         * colours are possible in each position. Where they overlap,
         * the cells must be of the block's color. */
        for (int block = 0; block < clueLength; block++) {
            if (minEnd[block] <= maxStart[block]) continue;
            BitSet notMask = new BitSet();
            final int colour = line.clue.get(block).color;
            notMask.set(colour);
            for (BitSet cell : line.cells.subList(maxStart[block],
                                                  minEnd[block])) {
                /* The block's color must still be possible here, or the
                 * algorithm is faulty. */
                assert cell.get(colour);

                /* Clear all bits except the block's color. */
                cell.andNot(notMask);
            }
        }

        /* Where the end of one block fails to overlap the start of the
         * next of the same color, the cells in-between must be free of
         * that color. */
        for (int colour = 1; colour < line.colors; colour++) {
            int prevEnd = 0;
            for (int block = 0; block < clueLength; block++) {
                if (line.clue.get(block).color != colour) continue;
                final int nextStart = minStart[block];
                if (prevEnd < nextStart)
                    for (BitSet cell : line.cells.subList(prevEnd, nextStart))
                    cell.clear(colour);
                prevEnd = maxEnd[block];
            }
            if (prevEnd < lineLength)
                for (BitSet cell : line.cells.subList(prevEnd, lineLength))
                cell.clear(colour);
        }

        return Result.EXHAUSTED;
    }

    @Override
    public Result process() {
        /* Guard against being run by more than one thread. This is only
         * important because we record the thread that's solving this
         * line, so we can interrupt it if aborted. Attempting to run
         * this method with two threads, consecutively or at the same
         * time, is considered to be a programming error. */
        if (user.compareAndSet(null, Thread.currentThread())) {
            try {
                if (done) throw new IllegalStateException("Already processed");
                return innerProcess();
            } finally {
                done = true;
                user.set(null);
            }
        } else {
            throw new IllegalStateException("Already processing");
        }
    }

    @Override
    public void abort() {
        aborted = true;
        Thread user = this.user.get();
        if (user != null) user.interrupt();
    }

    @Override
    public LineAlgorithm getAlgorithm() {
        return FastLineAlgorithm.INSTANCE;
    }
}
