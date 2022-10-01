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

/**
 * For the purposes of solving, a puzzle can be described as having a
 * certain number of cells (each with a unique index) arranged into
 * intersecting lines (each with a unique index). Such a puzzle is
 * represented by a
 *
 * {@link uk.ac.lancs.nonogram.layout.Layout} object.
 * 
 * <p>
 * A line is described as a sequence of cell indices, with an associated
 * clue, as represented by
 * 
 * {@link uk.ac.lancs.nonogram.layout.Line}.
 * 
 * A line description must also express the indices of intersecting
 * lines at each of its cells.
 * 
 * <p>
 * Clues are obtained from named banks, whose sizes imply the dimensions
 * of the puzzle. The number of colours in the puzzle is also implied by
 * the colours of the blocks in the clues. It is the job of a
 * {@link uk.ac.lancs.nonogram.geom.Geometry} to accept such named banks
 * (from a textual description of a puzzle, for example) to produce a
 *
 * {@link uk.ac.lancs.nonogram.layout.Layout}.
 * 
 * <p>
 * For example, a rectangular puzzle could be expressed as two banks of
 * clues, <samp>rows</samp> and <samp>cols</samp>. The number of rows
 * implies the height, and the number of columns implies the width. The
 * number of cells is the product of these two lengths. Each cell could
 * be assigned an index based on its position in the rectangular grid.
 * If cells are arranged row-by-row, cells in the first row can be
 * described by giving the start position 0, and incrementing by 1 for
 * one less than the number columns. Cells in the first column can be
 * described by giving the start position 0, and then incrementing by
 * the width. Meanwhile, <var>H</var> rows could be identified by the
 * line numbers 0 to <var>H</var>&minus;1, and <var>W</var> columns by
 * <var>H</var> to <var>H</var>+<var>W</var>&minus;1.
 * 
 * <p>
 * Another example is a hexagonal puzzle with equilateral triangles as
 * cells, with two horizontal sides and four diagonal sides. There are
 * three banks of clues, <samp>horiz</samp> (for horizontal rows),
 * <samp>asc</samp> (for diagonal rows from the bottom left to the top
 * right) and <samp>desc</samp> (for the remaining diagonal rows). These
 * have sizes <var>H</var>, <var>A</var> and <var>D</var> respectively.
 * The lengths of the lower-left sides are <var>b</var> for the base,
 * <var>l</var> for the lower left, and <var>u</var> for the upper left,
 * and they can be inferred from <var>H</var>, <var>A</var> and
 * <var>D</var>:
 * 
 * <ul>
 * 
 * <li>2<var>b</var> = <var>A</var> + <var>D</var> &minus; <var>H</var>
 * 
 * <li>2<var>d</var> = <var>D</var> + <var>H</var> &minus; <var>A</var>
 * 
 * <li>2<var>l</var> = <var>A</var> + <var>H</var> &minus; <var>D</var>
 * 
 * </ul>
 * 
 * <p>
 * In such a puzzle, it's possible that any one bank of clues may be
 * missing, so the size of that bank will have to be given explicitly.
 * 
 * <p>
 * A {@link uk.ac.lancs.nonogram.display.Display} provides an interface
 * through which a solver could render its current progress to the user.
 * It provides methods to record a change in a cell's colour, a line's
 * activity, and a line's algorithm level. Multiple displays can be
 * obtained from a single
 * 
 * {@link uk.ac.lancs.nonogram.display.DisplayFactory},
 * 
 * and multiple factories can be created from a geometry. In fact, a
 * geometry creates a
 * {@link uk.ac.lancs.nonogram.geom.WidgetDisplayFactory} (which is an
 * extension of
 * 
 * {@link uk.ac.lancs.nonogram.display.DisplayFactory}),
 * 
 * and generates {@link uk.ac.lancs.nonogram.geom.WidgetDisplay} (an
 * extension of
 * 
 * {@link uk.ac.lancs.nonogram.display.Display}),
 * 
 * and provides an extra component which can be added to a GUI (e.g.,
 * Swing or AWT) to actually display the changes. The component type is
 * implied by a
 * 
 * {@link uk.ac.lancs.nonogram.geom.DisplayType},
 * 
 * which is passed to the
 * {@link uk.ac.lancs.nonogram.geom.DisplayableLayout} to obtain a
 * {@link uk.ac.lancs.nonogram.geom.WidgetDisplayFactory} of the correct
 * GUI-component type.
 * 
 * <p>
 * This level abstraction complexity allows decoupling between the
 * solving logic and the GUI (e.g. Swing or AWT), and between the solver
 * logic and the puzzle geometry (e.g. rectangular or hexagonal).
 * 
 * @resume A geometry-independent and GUI-independent abstraction of
 * grid-based puzzles
 */
package uk.ac.lancs.nonogram.geom;

