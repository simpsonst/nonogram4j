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

/**
 * Works out what areas of a display need to change given a
 * corresponding change in internal state.
 * 
 * @author simpsons
 */
interface Updatable {
    /**
     * Update a cell.
     * 
     * @param x the cell's <var>x</var> co-ordinate
     * 
     * @param y the cell's <var>y</var> co-ordinate
     */
    void updateCell(int x, int y);

    /**
     * Update a row's activity indicator.
     * 
     * @param index the <var>y</var> co-ordinate of the row
     */
    void updateRowActivity(int index);

    /**
     * Update a column's activity indicator.
     * 
     * @param index the <var>x</var> co-ordinate of the column
     */
    void updateColumnActivity(int index);

    /**
     * Update a row's level indicator
     * 
     * @param index the <var>y</var> co-ordinate of the row
     */
    void updateRowLevel(int index);

    /**
     * Update a column's level indicator
     * 
     * @param index the <var>x</var> co-ordinate of the column
     */
    void updateColumnLevel(int index);
}
