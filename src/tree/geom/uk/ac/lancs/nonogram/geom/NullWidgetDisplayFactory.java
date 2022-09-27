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

import uk.ac.lancs.nonogram.aspect.Palette;
import uk.ac.lancs.nonogram.geom.WidgetDisplay.Transaction;

/**
 * Implements no display. All updates are ignored. This type is provided
 * as a convenience for testing algorithms without maintaining a
 * display.
 * 
 * @author simpsons
 */
public final class NullWidgetDisplayFactory
    implements WidgetDisplayFactory<Void> {
    private NullWidgetDisplayFactory() {}

    /**
     * @resume The sole instance of this class
     */
    public static final NullWidgetDisplayFactory INSTANCE =
        new NullWidgetDisplayFactory();

    /**
     * This implementation always returns a singleton, whose methods do
     * nothing.
     * 
     * @resume {@inheritDoc}
     */
    @Override
    public WidgetDisplay<Void> newDisplay() {
        return NullDisplay.INSTANCE;
    }

    /**
     * This implementation does nothing.
     * 
     * @resume {@inheritDoc}
     */
    @Override
    public void setAlgorithmCount(int algos) {
        /* Ignored. */
    }

    private static final class NullDisplay implements WidgetDisplay<Void> {
        private NullDisplay() {}

        public static final NullDisplay INSTANCE = new NullDisplay();

        @Override
        public Transaction open() {
            return NullTransaction.INSTANCE;
        }

        @Override
        public Void getWidget() {
            return null;
        }
    }

    private static final class NullTransaction implements Transaction {
        private NullTransaction() {}

        public static final NullTransaction INSTANCE = new NullTransaction();

        @Override
        public void close() {
            /* Ignored. */
        }

        @Override
        public void reset() {
            /* Ignored. */
        }

        @Override
        public void setCell(int index, int colour) {
            /* Ignored. */
        }

        @Override
        public void setLineActivity(int index, boolean active) {
            /* Ignored. */
        }

        @Override
        public void setLineLevel(int index, int state) {
            /* Ignored. */
        }

        @Override
        public void setPalette(Palette palette) {
            /* Ignored. */
        }
    }
}
