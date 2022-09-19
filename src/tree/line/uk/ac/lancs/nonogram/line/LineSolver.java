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

package uk.ac.lancs.nonogram.line;

/**
 * Attempts to deduce the state of unknown cells in a line, based on a
 * clue indicating order, length and colour of consecutive blocks.
 * 
 * @author simpsons
 */
public interface LineSolver {
    /**
     * Indicates the consequence of attempting to solve a line.
     * 
     * @author simpsons
     */
    enum Result {
        /**
         * The solver was aborted before completion.
         */
        ABORTED,

        /**
         * An inconsistency was detected.
         */
        INCONSISTENT,

        /**
         * The algorithm has learned as much as it can from this line.
         */
        EXHAUSTED;
    }

    /**
     * Derive as much information from this line as can be derived by
     * the responsible algorithm.
     * 
     * @return an indicator of how the line solver terminated
     * 
     * @throws IllegalStateException if this method has already been
     * called
     */
    Result process();

    /**
     * Tell the solver to abort. This should normally cause
     * {@link #process()} to return {@link Result#ABORTED}. It should
     * normally be called when another line solver operating on the same
     * grid has returned {@link Result#INCONSISTENT}, as this renders
     * all other processing on the grid irrelevant.
     * 
     * <p>
     * If the solver has already completed successfully, this method
     * should have no effect.
     */
    void abort();

    /**
     * Get the algorithm responsible for this solver.
     * 
     * @return the algorithm responsible for this solver
     */
    LineAlgorithm getAlgorithm();
}
