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

import java.util.List;
import uk.ac.lancs.nonogram.clue.Block;
import uk.ac.lancs.nonogram.clue.CellIterator;
import uk.ac.lancs.nonogram.clue.CellSequence;
import uk.ac.lancs.nonogram.clue.Colors;
import uk.ac.lancs.nonogram.line.LineAlgorithm;
import uk.ac.lancs.nonogram.line.LineChallenge;
import uk.ac.lancs.nonogram.line.LineSolver;

/**
 * Implements the ‘fast’ line-solving algorithm. This uses the method
 * {@link #push(int[], List, List)} to find one set of extreme positions
 * of all blocks, then it applies it again to a reversed view of the
 * line. By looking for overlaps implied by these two extremes, a large
 * proportion of the cells can be determined very quickly.
 * 
 * @author simpsons
 */
public final class FastLineAlgorithm implements LineAlgorithm {
    private FastLineAlgorithm() {}

    /**
     * The prefix for configuration strings for this algorithm, namely *
     * * {@value}
     */
    public static final String ALGORITHM_TYPE = "fast";

    @Override
    public LineSolver prepare(LineChallenge line) {
        return new FastLineSolver(line);
    }

    /**
     * Find the most extreme positions of several blocks in a partially
     * complete line.
     * 
     * @param min the array in which the lower ends of each block will
     * be stored
     * 
     * @param cells the current state of all cells
     * 
     * @param clue the clue describing the blocks to be fitted
     * 
     * @return true if an inconsistency was found
     */
    public static boolean push(int[] min, CellSequence cells,
                               List<? extends Block> clue) {
        final int clueLength = clue.size();
        final int lineLength = cells.size();
        final Block[] blocks = clue.toArray(new Block[clueLength]);
        min[0] = 0;

        int colors = 2;
        System.err.println();
        for (int color = 0; color < colors; color++) {
            for (CellIterator iter = cells.iterator(); iter.more();
                 iter.next()) {
                final int clen = iter.width();
                if (clen > colors) colors = clen;
                if (iter.has(color))
                    System.err.print(color);
                else
                    System.err.print('-');
            }
            System.err.println();
        }

        /* This indicates the current block (with its color), and a
         * solution is found when it reaches the clue length. */
        int block = 0;
        int color = blocks[block].color;
        int length = blocks[block].length;

        /* To start with, we need to check that the first block is not
         * inconsistent with anything it's covering. */

        /* This is the number of cell positions that the current block
         * must be advanced by. */
        int slide = 0;

        /* This is the number of leading cells under the current block
         * that must be checked. */
        int check = blocks[block].length;

        outer: for (;;) {
            assert block < clueLength;

            System.err.println();
            {
                int done = 0;
                for (int i = 0; i <= block; i++) {
                    for (; done < min[i]; done++)
                        System.err.print('-');
                    for (int j = 0; j < blocks[i].length; j++)
                        System.err.print(blocks[i].color);
                    done += blocks[i].length;
                }
                while (done++ < lineLength)
                    System.err.print('?');
                System.err.println();
            }
            System.err.printf("Block: %d (%d of C%d) at %d%n", block, length,
                              color, min[block]);
            System.err.printf("Slide %d; Check %d%n", slide, check);
            assert color == blocks[block].color;
            assert length == blocks[block].length;
            assert slide > 0 || check > 0;

            /* Fail if the current block protrudes beyond the line, or
             * would do if slid by the requested amount. */
            if (min[block] + length + slide > lineLength) return true;

            if (slide > 0) {
                /* We're required to slide the current block by a
                 * certain amount in order to cover a non-blank later
                 * on. Allow it to slide forward, checking that it does
                 * not expose a cell that cannot be blank. As we move,
                 * keep incrementing the count of cells to be
                 * checked. */
                int done = 0;
                assert min[block] + length + slide <= lineLength;
                for (CellIterator iter =
                    cells.iterator(min[block], min[block] + slide); iter.more();
                     iter.next()) {
                    if (iter.lacks(0)) {
                        /* We have exposed a cell which cannot be blank.
                         * Find a previous block of a compatible color,
                         * and make it slide far enough to cover this
                         * cell. */
                        System.err.printf("  Exposed non-blank at %d%n",
                                          done + min[block]);
                        final int newEnd = min[block] + done;
                        while (--block > 0 &&
                            cells.lacks(newEnd, blocks[block].color))
                            ;
                        /* Fail if we can't find a suitable earlier
                         * block. */
                        if (block < 0) return true;
                        color = blocks[block].color;
                        length = blocks[block].length;
                        slide = newEnd + 1 - length - min[block];
                        check = 0;
                        continue outer;
                    }
                    done++;
                }
                /* We're okay to move the current block by the requested
                 * amount. */
                min[block] += done;
                check = Math.min(check + done, length);
                slide = 0;
                continue outer;
            }

            assert slide == 0;
            assert check <= length;
            /* We must check that some of the leading (higher-numbered)
             * cells covered by the current block are not contradicted
             * by it. If they are, we'll have to slide beyond the first
             * contradictory cell. */
            slide = length - check;
            for (CellIterator iter = cells.iterator(min[block] + length - check,
                                                    min[block] + length);
                 iter.more(); iter.next()) {
                slide++;
                check--;
                if (iter.lacks(color)) {
                    /* The cell covered by this block can't be of the
                     * block's color. We must ask it to skip over enough
                     * cells to avoid it. */
                    System.err.printf("  Not possible at %d%n",
                                      slide + min[block] - 1);
                    continue outer;
                }
            }
            /* This block is okay. All cells under it are compatible
             * with its color. */
            System.err.println("  Okay");

            /* Position the next block. */
            final int nextBlock = block + 1;
            if (nextBlock == clueLength) {
                /* We have succeeded if there are no more blocks, and
                 * the remaining cells can be dots. */
                slide = min[block] + length;
                for (CellIterator iter = cells.iterator(slide, lineLength);
                     iter.more(); iter.next()) {
                    if (iter.lacks(0)) {
                        /* There's a trailing cell that cannot be blank.
                         * Find a block of a compatible color. */
                        while (block > -1 && iter.lacks(blocks[block].color))
                            block--;
                        if (block < 0) {
                            /* We have no blocks at all for this cell.
                             * We've failed. */
                            return true;
                        }
                        /* We've found a compatible block, so request
                         * that it be slid to cover this cell. */
                        color = blocks[block].color;
                        length = blocks[block].length;
                        slide = slide + 1 - length - min[block];
                        check = 0;
                        continue outer;
                    }
                    slide++;
                }
                /* There are no more cells which cannot be dots. */
                return false;
            }
            block = nextBlock;

            final int nextColor = blocks[block].color;
            if (nextColor == color) {
                /* Being of the same color, the next block must have a
                 * gap before it. */
                final int gap = min[block - 1] + length;
                final long gapState = cells.get(gap);
                min[block] = gap + 1;

                /* Check that the skipped cell can be blank. */
                if (Colors.lacks(gapState, 0)) {
                    /* It can't be blank, so we have to find an earlier
                     * block of a compatible color. */
                    while (--block > 0 &&
                        Colors.lacks(gapState, blocks[block].color))
                        ;
                    /* Fail if we can't find a suitable earlier
                     * block. */
                    if (block < 0) return true;

                    /* We found a block. Make it skip enough cells to
                     * cover the gap. */
                    color = blocks[block].color;
                    length = blocks[block].length;
                    slide = gap + 1 - length - min[block];
                    check = 0;
                    continue outer;
                }
            } else {
                /* The next block is a different color, so it can be
                 * adjacent to the current one. */
                min[block] = min[block - 1] + length;
                color = nextColor;
            }

            /* The next block in its new position must be checked in
             * full. */
            length = blocks[block].length;
            assert color == blocks[block].color;
            check = length;
            slide = 0;
        }
    }

    /**
     * @resume The sole instance of this class
     */
    public static final FastLineAlgorithm INSTANCE = new FastLineAlgorithm();
}
