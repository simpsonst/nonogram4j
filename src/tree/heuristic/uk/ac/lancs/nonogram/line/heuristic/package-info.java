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
 * A line-selection heuristic takes the basic details of a totally
 * unsolved line, and yields a priority which, relative to other lines'
 * priorities, indicates which lines are more worthwhile solving. A
 * solver can then use this information to focus on potentially more
 * rewarding lines first, and thereby produce a solution faster.
 * 
 * <p>
 * Creating a new heuristic first involves implementing
 * 
 * {@link uk.ac.lancs.nonogram.line.heuristic.LineHeuristic},
 * 
 * which has a single method that takes a line's basic details and
 * yields the priority as an integer.
 * 
 * <p>
 * The second step is to provide a factory for the new class by
 * implementing
 * 
 * {@link uk.ac.lancs.nonogram.line.heuristic.LineHeuristicLoader},
 * 
 * and declaring it as a service in the jar that delivers it by
 * including a file with the name:
 * 
 * <pre>
 * META-INF/<wbr>services/<wbr>uk.<wbr>ac.<wbr>lancs.<wbr>nonogram.<wbr>line.<wbr>heuristic.<wbr>Line<wbr>HeuristicLoader
 * </pre>
 * 
 * &hellip;including a line giving the loader class's internal name, in
 * accordance with
 * 
 * {@link java.util.ServiceLoader}.
 * 
 * @resume The API for writing line-selection heuristics
 */
package uk.ac.lancs.nonogram.line.heuristic;
