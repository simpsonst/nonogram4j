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

import uk.ac.lancs.nonogram.line.LineSolver;
import uk.ac.lancs.nonogram.line.LineSolver.Result;
import uk.ac.lancs.nonogram.line.LineWorkUnit;

/**
 * Grids provide jobs as records of their activity. A job includes a
 * line state that a line solver can work on, and an index to indicate
 * which algorithm should be used. When the solver has finished, the
 * result of that work is fed back to the grid via the job reference.
 * This allows the grid to record, for example, which lines are
 * currently being solved, which lines are unavailable for solving due
 * to intersection with perpendicular lines, and so on.
 * 
 * <p>
 * The standard idiom for using a job is as follows:
 * 
 * <pre>
 * for ( ; ; ) {
 *     try (LineJob job = grid.getJob()) {
 *         if (job.isInvalid()) break;
 *         LineAlgorithm algo = algorithms.get(job.getAlgorithmIndex());
 *         LineSolver solver = algo.prepare(job.getLine());
 *         grid.registerSolver(solver);
 *         job.submit(solver.process());
 *     }
 * }
 * </pre>
 * 
 * @resume A unit of work from the solution of a puzzle
 * 
 * @see Grid
 * 
 * @author simpsons
 */
public interface LineJob extends AutoCloseable {
    /**
     * Get the data representing the line by its clues, its mutable
     * current state, the number of colours, and any solver-specific
     * data associated with it.
     * 
     * @return a representation of the line
     */
    LineWorkUnit getLine();

    /**
     * Get the identifier for the algorithm that should work on this
     * line. This is a non-negative integer, whose maximum value plus
     * one is the number of algorithms in use.
     * 
     * @return the algorithm index
     */
    int getAlgorithmIndex();

    /**
     * Set the result type. The default is {@link Result#ABORTED}, if
     * this method is never called. The result is applied when the job
     * is closed.
     * 
     * <p>
     * If the result is {@link Result#ABORTED}, all line solvers
     * registered with the associated grid through
     * {@link Grid#registerSolver(LineSolver)} will be aborted.
     * 
     * @param result the new result type
     */
    void submit(Result result);

    /**
     * Complete the processing of this line.
     */
    @Override
    void close();

    /**
     * Determine whether this is a real job.
     * 
     * @return true if this is the last job to be offered by this grid
     * at this time
     */
    boolean isInvalid();
}
