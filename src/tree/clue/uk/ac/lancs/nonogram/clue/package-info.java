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

/**
 * Defines classes for internal representation of clues.
 * 
 * <p>
 * A clue is a sequence of blocks, where each block has a positive
 * length and a positive (non-background) colour number.
 * {@link uk.ac.lancs.nonogram.clue.Block} expresses such a block, and a
 * <code>{@linkplain java.util.List}&lt;Block&gt;</code> therefore
 * expresses a clue. A clue may be empty.
 * 
 * <p>
 * Colour numbers are non-negative integers up to a maximum. Zero is
 * reserved as the background colour, so no block has this colour
 * number. During solving, cells have a <dfn>working state</dfn>
 * representing the set of colours that have not yet been eliminated. A
 * cell state is represented by a {@code long}, with bit 0 set if colour
 * 0 has not yet been eliminated, bit 1 if colour 1, and so on.
 * {@link Colors} includes static methods for manipulating such states,
 * and {@link CellSequence} provides similar operations over an array of
 * {@code long}s.
 * 
 * @author simpsons
 */
package uk.ac.lancs.nonogram.clue;
