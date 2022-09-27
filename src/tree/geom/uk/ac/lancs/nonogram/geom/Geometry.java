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

package uk.ac.lancs.nonogram.geom;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import uk.ac.lancs.nonogram.aspect.Clue;
import uk.ac.lancs.nonogram.aspect.Hue;

/**
 * Creates puzzle representations from a named type, with named banks of
 * clues. The bank names usually depend on the geometry type name.
 * 
 * @author simpsons
 */
public interface Geometry {
    /**
     * Create a puzzle geometry from puzzle data. A particular geometry
     * will require several named banks of data. For example, a
     * rectangular puzzle requires <samp>rows</samp> and
     * <samp>columns</samp>.
     * 
     * <p>
     * The number of colours can be inferred from the colour of the
     * supplied blocks.
     * 
     * @param type the geometry type
     * 
     * @param banks the puzzle data
     * 
     * @param colorMap a mapping from cell states to colour indices
     * 
     * @return a puzzle geometry
     */
    DisplayableLayout
        createLayout(String type,
                     Function<? super Hue, ? extends Number> colorMap,
                     Map<? extends String, List<? extends Clue>> banks);
}
