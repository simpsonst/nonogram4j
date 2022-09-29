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
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import uk.ac.lancs.nonogram.Layout;
import uk.ac.lancs.nonogram.aspect.Clue;
import uk.ac.lancs.nonogram.aspect.Hue;
import uk.ac.lancs.nonogram.geom.Geometry;
import uk.ac.lancs.nonogram.geom.GeometryLoader;
import uk.ac.lancs.nonogram.plugin.PluginException;

/**
 * These methods use {@link ServiceLoader#load(Class)} or
 * {@link ServiceLoader#load(Class, ClassLoader)} to find all loader
 * implementations of a given type, and ask each in turn to provide an
 * implementation, returning the first match.
 * 
 * @resume Static methods for loading plug-ins
 * 
 * @author simpsons
 */
public final class Plugins {
    private Plugins() {}

    /**
     * Create a puzzle from a named geometry type and named banks of
     * clues.
     * 
     * @param type the geometry type
     * 
     * @param banks the puzzle data
     * 
     * @param colorMap a mapping from cell states to colour indices
     * 
     * @return a layout for the supplied puzzle
     * 
     * @throws UnknownPluginException if the configuration string
     * matches no known plug-in
     * 
     * @throws PluginException if some other exception occurred
     * 
     * @see GeometryLoader The service type sought by this method
     */
    public static Layout
        makePuzzle(String type,
                   Function<? super Hue, ? extends Number> colorMap,
                   Map<? extends String, List<? extends Clue>> banks)
            throws PluginException {
        Geometry factory = Geometry.findGeometry(type);
        return factory.createLayout(type, colorMap, banks);
    }

    /**
     * Create a puzzle from a named geometry type and named banks of
     * clues, using a given class loader.
     * 
     * @param type the geometry type
     * 
     * @param banks the puzzle data
     * 
     * @param colorMap a mapping from cell states to colour indices
     * 
     * @param classLoader the class loader used to find puzzle-geometry
     * plug-ins
     * 
     * @return a layout for the supplied puzzle
     * 
     * @throws UnknownPluginException if the configuration string
     * matches no known plug-in
     * 
     * @throws PluginException if some other exception occurred
     * 
     * @see GeometryLoader The service type sought by this method
     */
    public static Layout
        makePuzzle(String type,
                   Function<? super Hue, ? extends Number> colorMap,
                   Map<? extends String, List<? extends Clue>> banks,
                   ClassLoader classLoader)
            throws PluginException {
        Geometry factory = Geometry.findGeometry(type, classLoader);
        return factory.createLayout(type, colorMap, banks);
    }
}
