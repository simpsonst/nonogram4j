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
import uk.ac.lancs.nonogram.Clue;
import uk.ac.lancs.nonogram.Hue;
import uk.ac.lancs.nonogram.geom.Geometry;
import uk.ac.lancs.nonogram.geom.GeometryLoader;
import uk.ac.lancs.nonogram.geom.Layout;
import uk.ac.lancs.nonogram.line.LineAlgorithm;
import uk.ac.lancs.nonogram.line.LineAlgorithmLoader;
import uk.ac.lancs.nonogram.line.heuristic.LineHeuristic;
import uk.ac.lancs.nonogram.line.heuristic.LineHeuristicLoader;
import uk.ac.lancs.nonogram.plugin.PluginConfigurationException;
import uk.ac.lancs.nonogram.plugin.PluginException;
import uk.ac.lancs.nonogram.plugin.PluginLoader;

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

    private static <T, L extends PluginLoader<T>> T
        findPlugin(Class<L> loaderType, String label, String config)
            throws PluginException {
        for (L cand : ServiceLoader.load(loaderType)) {
            T plugin = cand.load(config);
            if (plugin != null) return plugin;
        }

        throw new UnknownPluginException(label + ": " + config);
    }

    private static <T, L extends PluginLoader<T>> T
        findPlugin(Class<L> loaderType, String label, String config,
                   ClassLoader classLoader)
            throws PluginException {
        for (L cand : ServiceLoader.load(loaderType, classLoader)) {
            T plugin = cand.load(config);
            if (plugin != null) return plugin;
        }

        throw new UnknownPluginException(label + ": " + config);
    }

    /**
     * Find a line-solving algorithm, using a plug-in matching the
     * supplied configuration.
     * 
     * @param config a string identifying the plug-in and specifying its
     * configuration
     * 
     * @return the instance supplied by the first matching loader
     * 
     * @throws UnknownPluginException if the configuration string
     * matches no known plug-in
     * 
     * @throws PluginConfigurationException if the configuration string
     * was recognized, but is invalid
     * 
     * @throws PluginException if some other exception occurred
     * 
     * @see LineAlgorithmLoader The service type sought by this method
     */
    public static LineAlgorithm findLineAlgorithm(String config)
        throws PluginException {
        return findPlugin(LineAlgorithmLoader.class, "algo", config);
    }

    /**
     * Find a line-solving algorithm, using a plug-in from a class
     * loader, matching the supplied configuration.
     * 
     * @param config a string identifying the plug-in and specifying its
     * configuration
     * 
     * @return the instance supplied by the first matching loader
     * 
     * @throws UnknownPluginException if the configuration string
     * matches no known plug-in
     * 
     * @throws PluginConfigurationException if the configuration string
     * was recognized, but is invalid
     * 
     * @throws PluginException if some other exception occurred
     * 
     * @param classLoader the class loader used to find line-solving
     * algorithm loaders
     * 
     * @see LineAlgorithmLoader The service type sought by this method
     */
    public static LineAlgorithm findLineAlgorithm(String config,
                                                  ClassLoader classLoader)
        throws PluginException {
        return findPlugin(LineAlgorithmLoader.class, "algo", config,
                          classLoader);
    }

    /**
     * Find a line-selection heuristic, using a plug-in matching the
     * supplied configuration.
     * 
     * @param config a string identifying the plug-in and specifying its
     * configuration
     * 
     * @return the instance supplied by the first matching loader
     * 
     * @throws UnknownPluginException if the configuration string
     * matches no known plug-in
     * 
     * @throws PluginConfigurationException if the configuration string
     * was recognized, but is invalid
     * 
     * @throws PluginException if some other exception occurred
     * 
     * @see LineHeuristicLoader The service type sought by this method
     */
    public static LineHeuristic findLineHeuristic(String config)
        throws PluginException {
        return findPlugin(LineHeuristicLoader.class, "heuristic", config);
    }

    /**
     * Find a line-selection heuristic, using a plug-in from a class
     * loader, matching the supplied configuration.
     * 
     * @param config a string identifying the plug-in and specifying its
     * configuration
     * 
     * @return the instance supplied by the first matching loader
     * 
     * @throws UnknownPluginException if the configuration string
     * matches no known plug-in
     * 
     * @throws PluginConfigurationException if the configuration string
     * was recognized, but is invalid
     * 
     * @throws PluginException if some other exception occurred
     * 
     * @param classLoader the class loader used to find line-selection
     * heuristics
     * 
     * @see LineHeuristicLoader The service type sought by this method
     */
    public static LineHeuristic findLineHeuristic(String config,
                                                  ClassLoader classLoader)
        throws PluginException {
        return findPlugin(LineHeuristicLoader.class, "heuristic", config,
                          classLoader);
    }

    /**
     * Find a puzzle geometry, using a plug-in matching the supplied
     * configuration.
     * 
     * @param config a string identifying the plug-in and specifying its
     * configuration
     * 
     * @return the instance supplied by the first matching loader
     * 
     * @throws UnknownPluginException if the configuration string
     * matches no known plug-in
     * 
     * @throws PluginConfigurationException if the configuration string
     * was recognized, but is invalid
     * 
     * @throws PluginException if some other exception occurred
     * 
     * @see GeometryLoader The service type sought by this method
     */
    public static Geometry findGeometry(String config) throws PluginException {
        return findPlugin(GeometryLoader.class, "geometry", config);
    }

    /**
     * Find a puzzle geometry, using a plug-in from a class loader,
     * matching the supplied configuration.
     * 
     * @param config a string identifying the plug-in and specifying its
     * configuration
     * 
     * @return the instance supplied by the first matching loader
     * 
     * @throws UnknownPluginException if the configuration string
     * matches no known plug-in
     * 
     * @throws PluginConfigurationException if the configuration string
     * was recognized, but is invalid
     * 
     * @throws PluginException if some other exception occurred
     * 
     * @param classLoader the class loader used to find puzzle-geometry
     * plug-ins
     * 
     * @see GeometryLoader The service type sought by this method
     */
    public static Geometry findGeometry(String config, ClassLoader classLoader)
        throws PluginException {
        return findPlugin(GeometryLoader.class, "geometry", config,
                          classLoader);
    }

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
        Geometry factory = findGeometry(type);
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
        Geometry factory = findGeometry(type, classLoader);
        return factory.createLayout(type, colorMap, banks);
    }
}
