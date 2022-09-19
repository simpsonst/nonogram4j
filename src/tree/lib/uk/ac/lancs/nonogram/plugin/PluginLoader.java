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

package uk.ac.lancs.nonogram.plugin;

import java.util.Locale;

/**
 * Loads plug-ins recognized by name. Subclasses should be defined to
 * recognize well-known strings, such as <samp>foo</samp> for a plug-in
 * that needs no configuration, or <samp>foo:<var>config</var></samp>
 * when additional configuration is possible. Strings are recognized by
 * implementing the {@link #load(String)} method. If the prefix is
 * matched, but the configuration is invalid, or only the prefix is
 * given when a configuration is required, the implementation may throw
 * a {@link PluginConfigurationException}.
 * 
 * @author simpsons
 */
public interface PluginLoader<T> {
    /**
     * Acquire a plug-in described by a configuration string.
     * 
     * @param config a string identifying the plug-in and specifying its
     * configuration
     * 
     * @return an instance of the described plug-in, or {@code null} if
     * not recognized
     * 
     * @throws PluginConfigurationException if the configuration string
     * was recognized, but is invalid
     */
    T load(String config) throws PluginConfigurationException;

    /**
     * Get simple textual message describing the format of strings
     * accepted by {@link #load(String)}.
     * 
     * @param locale The message should be compatible with this locale.
     * 
     * @return a message showing the syntax of the string accepted by
     * {@link #load(String)}
     */
    String getSyntax(Locale locale);
}
