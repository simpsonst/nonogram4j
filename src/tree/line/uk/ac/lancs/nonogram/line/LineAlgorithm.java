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

import uk.ac.lancs.nonogram.plugin.PluginConfigurationException;
import uk.ac.lancs.nonogram.plugin.PluginException;
import uk.ac.lancs.nonogram.plugin.PluginLoader;
import uk.ac.lancs.nonogram.plugin.UnknownPluginException;

/**
 * Creates line solvers for specific lines.
 * 
 * @author simpsons
 */
public interface LineAlgorithm {
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
        return PluginLoader.findPlugin(LineAlgorithmLoader.class, "algo",
                                       config);
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
        return PluginLoader.findPlugin(LineAlgorithmLoader.class, "algo",
                                       config, classLoader);
    }

    /**
     * Prepare to work on a given line of cells.
     * 
     * @param line the working line state, including the number of
     * colours, the clue, the mutable cell state, and a cache of
     * algorithm-specific states pertaining to this line
     * 
     * @return a new solver with fresh state
     */
    LineSolver prepare(LineChallenge line);
}
