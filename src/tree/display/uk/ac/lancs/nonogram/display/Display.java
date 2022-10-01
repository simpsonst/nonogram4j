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

package uk.ac.lancs.nonogram.display;

/**
 * A display can provide a transaction, on which modifications are made
 * before closing. These are then applied as an atomic operation.
 * 
 * @resume A visual display of a solver's grid state
 * 
 * @author simpsons
 */
public interface Display {
    /**
     * Gathers changes to a display into an atomic operation.
     * 
     * @author simpsons
     */
    interface Transaction extends AutoCloseable {
        /**
         * Commit pending changes to the display.
         */
        @Override
        void close();

        /**
         * Reset all pending changes. This could be used to abort
         * changes when a surrounding operation is aborted.
         */
        void reset();

        /**
         * Set a cell to a given colour.
         * 
         * @param index the cell index, as defined by an associated
         * {@link uk.ac.lancs.nonogram.layout.Layout}
         * 
         * @param color the new cell colour, or -1 for unknown
         */
        void setCell(int index, int color);

        /**
         * Set the line-activity indicator.
         * 
         * @param index the line index, as defined by an associated
         * {@link uk.ac.lancs.nonogram.layout.Layout}
         * 
         * @param active the new indicator state
         */
        void setLineActivity(int index, boolean active);

        /**
         * Set the line-level indicator. This is a measure of new
         * information available on the line. Zero means that there is
         * no new information for any line-solving algorithm. Higher
         * values indicate that all algorithms up to that number have
         * not attempted the line since they were last applied to it.
         * 
         * @param index the line index, as defined by an associated
         * {@link uk.ac.lancs.nonogram.layout.Layout}
         * 
         * @param state the new indicator state
         */
        void setLineLevel(int index, int state);
    }

    /**
     * Create a new transaction.
     * 
     * @return a new transaction
     */
    Transaction open();
}
