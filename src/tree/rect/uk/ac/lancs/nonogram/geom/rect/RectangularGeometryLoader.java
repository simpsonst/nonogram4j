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

import java.util.Locale;
import uk.ac.lancs.scc.jardeps.Service;
import uk.ac.lancs.nonogram.geom.Geometry;
import uk.ac.lancs.nonogram.geom.GeometryLoader;
import uk.ac.lancs.nonogram.plugin.PluginConfigurationException;

/**
 * Recognizes the plug-in configuration string
 * 
 * <samp>{@value RectangularGeometry#GEOMETRY_TYPE}</samp>,
 * 
 * and yields a {@link RectangularGeometry}.
 * 
 * @author simpsons
 */
@Service(GeometryLoader.class)
class RectangularGeometryLoader implements GeometryLoader {
    @Override
    public Geometry load(String config) throws PluginConfigurationException {
        if (!RectangularGeometry.GEOMETRY_TYPE.equals(config)) return null;
        return RectangularGeometry.INSTANCE;
    }

    @Override
    public String getSyntax(Locale locale) {
        return RectangularGeometry.GEOMETRY_TYPE;
    }
}
