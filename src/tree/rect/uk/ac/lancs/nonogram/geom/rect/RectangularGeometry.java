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

package uk.ac.lancs.nonogram.geom.rect;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import uk.ac.lancs.nonogram.aspect.Clue;
import uk.ac.lancs.nonogram.aspect.Hue;
import uk.ac.lancs.nonogram.geom.DisplayableLayout;
import uk.ac.lancs.nonogram.geom.Geometry;

/**
 * Creates rectangular puzzles.
 * 
 * <p>
 * The type created is {@link RectangularLayout}.
 * 
 * @author simpsons
 */
public class RectangularGeometry implements Geometry {
    /**
     * @resume The sole geometry type recognized by this factory
     */
    public static final String GEOMETRY_TYPE = "rect";

    /**
     * @resume The bank of clues recognized by this factory as row data
     */
    public static final String ROW_BANK_NAME = "rows";

    /**
     * @resume The bank of clues recognized by this factory as column
     * data
     */
    public static final String COLUMN_BANK_NAME = "cols";

    /**
     * This implementation only recognized the geometry type
     * 
     * <samp>{@value #GEOMETRY_TYPE}</samp>,
     * 
     * with two banks of clues called
     * 
     * <samp>{@value #ROW_BANK_NAME}</samp> and
     * 
     * <samp>{@value #COLUMN_BANK_NAME}</samp>.
     * 
     * @resume {@inheritDoc}
     */

    @Override
    public DisplayableLayout
        createLayout(String type,
                     Function<? super Hue, ? extends Number> colorMap,
                     Map<? extends String, List<? extends Clue>> banks) {
        if (!GEOMETRY_TYPE.equals(type))
            throw new IllegalArgumentException("geometry type: " + type);
        List<? extends Clue> rows = banks.get(ROW_BANK_NAME);
        if (rows == null) throw new IllegalArgumentException("missing bank: "
            + ROW_BANK_NAME);
        List<? extends Clue> cols = banks.get(COLUMN_BANK_NAME);
        if (cols == null) throw new IllegalArgumentException("missing bank: "
            + COLUMN_BANK_NAME);
        if (banks.size() != 2)
            throw new IllegalArgumentException("unrecognized banks: " + banks);
        return new RectangularLayout(rows, cols, colorMap);
    }

    private RectangularGeometry() {}

    /**
     * @resume The sole instance of this class
     */
    public static final RectangularGeometry INSTANCE =
        new RectangularGeometry();
}
