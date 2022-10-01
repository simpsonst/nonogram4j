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

/**
 * A line solver takes the current state of a line (its clue data and
 * cell state), and attempts to deduce the state of undetermined cells.
 * It does not need to determine all of them, but the most sophisticated
 * algorithm applied to line must be able to detect when the current
 * state is inconsistent with the clue data.
 * 
 * <p>
 * Creating a new line-solving algorithm first involves writing an
 * implementation of {@link uk.ac.lancs.nonogram.line.LineSolver} that
 * takes a {@link uk.ac.lancs.nonogram.line.LineChallenge} as
 * configuration, which provides it with details of a line to be solved.
 * The algorithm itself should be inside the
 * 
 * {@link uk.ac.lancs.nonogram.line.LineSolver#process()}, returning
 * only when it is exhausted, or it has detected an inconsistency (the
 * line cannot be solved with the given clue data and current state), or
 * it has been externally aborted.
 * 
 * <p>
 * The {@code LineWorkUnit} is passed to a new {@code LineSolver} by
 * implementing {@link uk.ac.lancs.nonogram.line.LineAlgorithm}, which
 * abstracts the construction of the {@code LineSolver}.
 * 
 * <p>
 * To make the algorithm available to a solver, a
 * {@link uk.ac.lancs.nonogram.line.LineAlgorithmLoader} must be
 * implemented, and be declared as a service by including a file in the
 * jar that delivers the algorithm with the name:
 * 
 * <pre>
 * META-INF/<wbr>services/<wbr>uk.<wbr>ac.<wbr>lancs.<wbr>nonogram.<wbr>line.<wbr>Line<wbr>Algorithm<wbr>Loader
 * </pre>
 * 
 * <p>
 * &hellip;and including a line giving the class's internal name, in
 * accordance with {@link java.util.ServiceLoader}. The implementation
 * must recognize a string of an agreed format that identifies the
 * algorithm, and optionally configures it. Such a string normally
 * begins with a well-known prefix identifying the algorithm, e.g.,
 * <samp>fast</samp>, followed by a colon and configuration parameters
 * if required.
 * 
 * @resume API for writing line-solving algorithms
 */
package uk.ac.lancs.nonogram.line;
