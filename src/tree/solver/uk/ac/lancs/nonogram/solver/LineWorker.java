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

package uk.ac.lancs.nonogram.solver;

import java.util.List;
import uk.ac.lancs.nonogram.line.LineAlgorithm;
import uk.ac.lancs.nonogram.line.LineSolver;

/**
 * Processes grids and line jobs until exhausted.
 *
 * @author simpsons
 */
public class LineWorker implements Runnable {
    private final GridSource<? extends Grid> source;

    private final List<? extends LineAlgorithm> algorithms;

    /**
     * Create a worker.
     * 
     * @param source the source for grids, and ultimately line jobs
     * 
     * @param algorithms the index of line-solving algorithms to use
     */
    private LineWorker(GridSource<? extends Grid> source,
                       List<? extends LineAlgorithm> algorithms) {
        this.source = source;
        this.algorithms = algorithms;
    }

    /**
     * Process line jobs until exhausted.
     * 
     * This method repeatedly calls {@link GridSource#selectGrid()}
     * until it returns {@code null}. For each returned grid, it
     * repeatedly calls {@link Grid#getJob()} to attempt to claim a line
     * to work on, and submits that job to an appropriate line solver.
     */
    @Override
    public void run() {
        for (;;) {
            /* Keep going until there are no more grids to work on. */
            Grid grid = source.selectGrid();
            if (grid == null) break;

            /* Work on the current grid until it tells us to try
             * something else. */
            for (;;) {
                try (LineJob job = grid.getJob()) {
                    if (job.isInvalid()) break;
                    LineAlgorithm algo =
                        algorithms.get(job.getAlgorithmIndex());
                    LineSolver solver = algo.prepare(job.getLine());
                    grid.registerSolver(solver);
                    job.submit(solver.process());
                }
            }
        }
    }

}
