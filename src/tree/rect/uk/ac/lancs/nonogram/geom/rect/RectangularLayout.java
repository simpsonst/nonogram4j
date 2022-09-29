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

import java.awt.Component;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import uk.ac.lancs.nonogram.Cell;
import uk.ac.lancs.nonogram.Line;
import uk.ac.lancs.nonogram.aspect.Bar;
import uk.ac.lancs.nonogram.aspect.Clue;
import uk.ac.lancs.nonogram.aspect.Hue;
import uk.ac.lancs.nonogram.clue.Block;
import uk.ac.lancs.nonogram.geom.DisplayType;
import uk.ac.lancs.nonogram.geom.DisplayableLayout;
import uk.ac.lancs.nonogram.geom.NullWidgetDisplayFactory;
import uk.ac.lancs.nonogram.geom.WidgetDisplay;
import uk.ac.lancs.nonogram.geom.WidgetDisplayFactory;

/**
 * The number of cells is the width <var>w</var> times the height
 * <var>h</var>, <var>w</var><var>h</var>. The number of lines is the
 * width plus the height, <var>w</var>+<var>h</var>. Cells are indexed
 * row-by-row, so a cell at (<var>x</var>,<var>y</var>) has index
 * <var>x</var>+<var>y</var><var>w</var>. Rows are indexed before
 * columns, so row <var>y</var> has index <var>y</var>, and column
 * <var>x</var> has index <var>h</var>+<var>x</var>.
 * 
 * @resume A rectangular puzzle with square cells
 * 
 * @author simpsons
 */
public class RectangularLayout implements DisplayableLayout {
    private final int width;

    private final int height;

    private final int colourCount;

    private final Cell[] cells;

    private final Line[] lines;

    private interface Slicer {
        /**
         * Get indices to all cells in a row or column.
         * 
         * @param cells the array backing the cells of the grid,
         * arranged row-by-row
         * 
         * @param width the grid width
         * 
         * @param height the grid height
         * 
         * @param clueNumber the clue number within its bank
         * 
         * @return an immutable list of cells that the clue applies to
         */
        List<Cell> slice(Cell[] cells, int width, int height, int clueNumber);
    }

    private static class RowSlice extends AbstractList<Cell> {
        private final Cell[] cells;

        private final int width;

        private final int height;

        private final int base;

        public RowSlice(Cell[] cells, int width, int height, int base) {
            this.cells = cells;
            this.width = width;
            this.height = height;
            this.base = base;
        }

        @Override
        public Cell get(int index) {
            if (index < 0 || index >= height)
                throw new NoSuchElementException("[0, " + height + ") excludes "
                    + index);
            return cells[base + index];
        }

        @Override
        public int size() {
            return width;
        }
    }

    private static class ColSlice extends AbstractList<Cell> {
        private final Cell[] cells;

        private final int width;

        private final int height;

        private final int base;

        public ColSlice(Cell[] cells, int width, int height, int base) {
            this.cells = cells;
            this.width = width;
            this.height = height;
            this.base = base;
        }

        @Override
        public Cell get(int index) {
            if (index < 0 || index >= width)
                throw new NoSuchElementException("[0, " + width + ") excludes "
                    + index);
            return cells[base + index * width];
        }

        @Override
        public int size() {
            return height;
        }
    }

    /**
     * Create an index from a clue in a bank to line number. Blank clues
     * are assigned an invalid (negative) line number.
     * 
     * @param index the array to hold line numbers, indexed by clue
     * number within the bank
     * 
     * @param bank the bank of clues
     * 
     * @param lines the list to append each new line
     * 
     * @param colorMap a mapping from cell states to colour indices
     */
    private static void
        indexBank(int[] index, Clue[] bank, Cell[] cells,
                  List<? super Line> lines, int width, int height,
                  Function<? super Hue, ? extends Number> colorMap,
                  Slicer slicer) {
        for (int i = 0; i < index.length; i++) {
            /* Extract the blocks, or skip over blank clues. */
            List<Bar> bs = bank[i].blocks();
            if (bs == null) {
                index[i] = -1;
                continue;
            }

            /* Convert the blocks with abstract colours into ones with
             * indexed colours. */
            final List<Block> ibs = bs.stream().map(b -> Bar.of(b, colorMap))
                .collect(Collectors.toList());

            /* Get a view of the cells that form this line. */
            final List<Cell> baseCells = slicer.slice(cells, width, height, i);

            /* Create the line abstraction. */
            Line line = new Line() {
                @Override
                public List<Block> clue() {
                    return ibs;
                }

                @Override
                public List<Cell> cells() {
                    return baseCells;
                }
            };

            /* Allocate a position for the line. */
            final int nx = lines.size();
            index[i] = nx;
            lines.add(line);
        }
    }

    /**
     * Add a line number to a set of intersecting lines, if the line
     * number is valid.
     * 
     * @param set the set to be enlarged
     * 
     * @param lineNo the line number to add; or a negative number if
     * invalid
     */
    private static void intersect(BitSet set, int lineNo) {
        if (lineNo >= 0) set.set(lineNo);
    }

    /**
     * Create a rectangular geometry of square cells.
     * 
     * @param rows the row clues, implying the height
     * 
     * @param cols the column clues, implying the width
     */
    public RectangularLayout(List<? extends Clue> rows,
                             List<? extends Clue> cols,
                             Function<? super Hue, ? extends Number> colorMap) {
        /* Copy our clues to arrays so we can access them readily by
         * index. */
        Clue[] rowClues = rows.toArray(n -> new Clue[n]);
        Clue[] colClues = cols.toArray(n -> new Clue[n]);

        /* The number of cells is always width times height. */
        height = rowClues.length;
        width = colClues.length;

        /* Create the cell array and an immutable list view of it. */
        this.cells = new Cell[width * height];

        /* Assign each non-blank clue a line number, and record the
         * mapping from each clue's position within its bank to assigned
         * line number. */
        int[] rowIndex = new int[height];
        int[] colIndex = new int[width];
        List<Line> myLines = new ArrayList<>(width + height);
        indexBank(rowIndex, rowClues, this.cells, myLines, width, height,
                  colorMap, RowSlice::new);
        indexBank(colIndex, colClues, this.cells, myLines, width, height,
                  colorMap, ColSlice::new);
        this.lines = myLines.toArray(n -> new Line[n]);

        /* Populate the cell array. */
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                /* Which row and column does this intersect? Add their
                 * line numbers, if they are valid. */
                final BitSet intersects = new BitSet();
                intersect(intersects, rowIndex[y]);
                intersect(intersects, colIndex[x]);

                /* Create navigation from this cell to intersecting
                 * lines. */
                final int index = x + y * width;
                cells[index] = new Cell() {
                    @Override
                    public BitSet intersects() {
                        return intersects;
                    }

                    @Override
                    public int index() {
                        return index;
                    }
                };
            }
        }

        this.colourCount = Stream.of(lines).map(Line::clue)
            .flatMapToInt(bl -> bl.stream().mapToInt(ib -> ib.color)).max()
            .orElse(0) + 1;
    }

    @Override
    public int colors() {
        return colourCount;
    }

    /**
     * Provides an immutable list view of the lines.
     */
    private final List<Line> lineList = new AbstractList<Line>() {
        @Override
        public Line get(int index) {
            return lines[index];
        }

        @Override
        public int size() {
            return lines.length;
        }
    };

    /**
     * Provides an immutable list view of the cells.
     */
    private final List<Cell> cellList = new AbstractList<Cell>() {
        @Override
        public Cell get(int index) {
            return cells[index];
        }

        @Override
        public int size() {
            return cells.length;
        }
    };

    @Override
    public List<Cell> cells() {
        return cellList;
    }

    @Override
    public List<Line> lines() {
        return lineList;
    }

    /**
     * This implementation recognizes {@link DisplayType#SWING}.
     * 
     * @resume {@inheritDoc}
     */
    @Override
    public <W> WidgetDisplayFactory<W> getDisplayFactory(DisplayType<W> type) {
        if (type.equals(DisplayType.VOID)) {
            @SuppressWarnings("unchecked")
            WidgetDisplayFactory<W> result =
                (WidgetDisplayFactory<W>) NullWidgetDisplayFactory.INSTANCE;
            return result;
        }
        if (type.equals(DisplayType.SWING)) {
            @SuppressWarnings("unchecked")
            WidgetDisplayFactory<W> result =
                (WidgetDisplayFactory<W>) new WidgetDisplayFactory<Component>() {
                    private int algos =
                        WidgetDisplayFactory.DEFAULT_ALGORITHM_COUNT;

                    @Override
                    public WidgetDisplay<Component> newDisplay() {
                        DisplayState state =
                            new DisplayState(width, height, algos);
                        SwingWidget widget = new SwingWidget(state);
                        return new RectangularDisplay<Component>(state, widget,
                                                                 widget);
                    }

                    @Override
                    public void setAlgorithmCount(int algos) {
                        this.algos = algos;
                    }
                };
            return result;
        }
        return null;
    }
}
