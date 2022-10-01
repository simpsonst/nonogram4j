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

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.WeakHashMap;
import uk.ac.lancs.nonogram.clue.ArrayCellSequence;
import uk.ac.lancs.nonogram.clue.Colors;
import uk.ac.lancs.nonogram.display.Display;
import uk.ac.lancs.nonogram.display.DisplayFactory;
import uk.ac.lancs.nonogram.layout.Cell;
import uk.ac.lancs.nonogram.layout.Layout;
import uk.ac.lancs.nonogram.layout.Line;
import uk.ac.lancs.nonogram.line.Cache;
import uk.ac.lancs.nonogram.line.LineChallenge;
import uk.ac.lancs.nonogram.line.LineSolver;
import uk.ac.lancs.nonogram.line.LineSolver.Result;
import uk.ac.lancs.nonogram.line.heuristic.LineHeuristic;

/**
 * Retains information about a puzzle and its current solving state.
 * 
 * @author simpsons
 */
public final class Grid {
    /**
     * The number of colours in the puzzle
     */
    private final int colors;

    private final int lineCount;

    private final int cellCount;

    /**
     * The number of line-solving algorithms that will be applied to
     * lines from this grid
     */
    private final int algos;

    /**
     * This grid adds itself, and clones of itself, to this sink when
     * such grids have jobs available.
     */
    private final GridSink<Grid> sink;

    /**
     * The factory for creating displays when cloning
     */
    private final DisplayFactory displays;

    private final Layout layout;

    /**
     * We keep a 1-dimensional array for cell states. This is sufficient
     * to give each cell an identity, and the layout only refers to
     * cells by such identities.
     */
    private final long[] cells;

    /**
     * We keep a 1-dimensional array for line descriptions. Again, the
     * layout only identifies lines by a simple integer.
     */
    private final Line[] lines;

    private final Cache[] caches;

    private int cellsRemaining;

    /**
     * These are the selection weights for each line, initially computed
     * from a line-selection heuristic (and multiplied by the number of
     * colours). As the results from a line solver are processed, new
     * information causes the line's weight to be decremented, while
     * intersecting lines are incremented.
     */
    private final int[] weights;

    /**
     * To support simultaneous solution of intersecting lines, we keep a
     * lock count for each line. A non-zero value means the line is not
     * available for solving, because the given number of lines are
     * intersecting at incomplete cells.
     */
    private final int[] locks;

    private final BitSet lineActivity = new BitSet();

    /**
     * Each line has a counter to indicate how many algorithms have not
     * yet been tried on it since new information was added to it.
     */
    private final int[] levels;

    /**
     * This is the number of guesses made to produce this grid. It is
     * used by {@link #getScore()}.
     */
    private int guessed;

    /**
     * This is the number of deductions made. It is used by
     * {@link #getScore()}. Every colour eliminated from every cell
     * counts as a deduction.
     */
    private int deduced = 0;

    /**
     * Get the score for this grid used to prioritize it above others
     * when threads are seeking jobs. Lower scores have higher priority.
     * 
     * @return the score, which is {@link #guessed} divided by (
     * {@link #deduced} + 1)
     */
    private double getScore() {
        return (double) guessed / (deduced + 1);
    }

    /**
     * Make this grid submit itself as one with available jobs.
     */
    public synchronized void start() {
        determineNextLine();
    }

    private final Display display;

    /**
     * Get the display associated with this grid.
     * 
     * @return the grid's associated display
     */
    public Display getDisplay() {
        return display;
    }

    /**
     * Create a grid from a puzzle layout.
     * 
     * @param algos the number of line-solving algorithms that will be
     * applied to lines from this grid
     * 
     * @param sink the destination for submitting this grid when it has
     * jobs available
     * 
     * @param layout the source layout
     * 
     * @param heur the heuristic for weighting lines for selection
     */
    public Grid(int algos, GridSink<Grid> sink, Layout layout,
                LineHeuristic heur, DisplayFactory displays) {
        this.guessed = 0;
        this.sink = sink;
        this.displays = displays;
        this.display = this.displays.newDisplay();
        this.algos = algos;
        this.layout = layout;
        this.colors = layout.colors();
        this.cellCount = layout.cells().size();
        this.lineCount = layout.lines().size();
        this.caches = new Cache[lineCount];
        this.cellsRemaining = cellCount;
        this.lines = new Line[lineCount];
        this.weights = new int[lineCount];
        this.locks = new int[lineCount];

        /* Set all cells to 'completely unknown'. We don't need to
         * update the display, as 'unknown' is the default state. */
        cells = new long[cellCount];
        for (int i = 0; i < cells.length; i++)
            cells[i] = Colors.newCellLong(colors);

        /* Set weights and algorithm levels. Update the display to show
         * the levels. */
        try (Display.Transaction xact = display.open()) {
            levels = new int[lineCount];
            for (int i = 0; i < lineCount; i++) {
                Line line = lines[i] = layout.lines().get(i);
                weights[i] =
                    heur.compute(line.cells().size(), line.clue()) * colors;
                levels[i] = this.algos;
                xact.setLineLevel(i, levels[i]);
            }
        }
    }

    /**
     * Create a new grid which is a clone of this grid.
     * 
     * @param source the grid to clone
     * 
     * @param bestCell the cell to make a guess at
     * 
     * @param eliminatedColour the colour to eliminate at the best cell
     */
    private Grid(Grid source, Cell bestCell, int eliminatedColour) {
        assert Thread.holdsLock(source);

        /* These items can be shared, or do not need a deep copy. */
        this.colors = source.colors;
        this.lineCount = source.lineCount;
        this.cellCount = source.cellCount;
        this.algos = source.algos;
        this.sink = source.sink;
        this.cellsRemaining = source.cellsRemaining;
        this.guessed = source.guessed;
        this.deduced = source.deduced;
        this.lines = source.lines;
        this.layout = source.layout;
        this.displays = source.displays;

        /* No need to copy lock state. The source's locks should all be
         * zero. */
        this.locks = new int[lineCount];

        /* No need to copy algorithm levels. The source's levels should
         * all be zero. */
        this.levels = new int[lineCount];

        /* Caches must be deep-copied. */
        this.caches = new Cache[source.caches.length];
        for (int i = 0; i < caches.length; i++)
            caches[i] = source.caches[i].clone();

        /* We must make a new display for ourselves. */
        this.display = this.displays.newDisplay();

        /* We must copy each of the weights so we can modify them
         * independently of the source grid. */
        this.weights = Arrays.copyOf(source.weights, source.weights.length);

        /* Cells must be copied, and colour eliminated from the best
         * cell as the antithesis of the specified guess. */
        this.cells = Arrays.copyOf(source.cells, source.cells.length);
        cells[bestCell.index()] &= ~eliminatedColour;
        deduced++;
        guessed++;
        assert nextLine == -1;

        /* Update the display and the intersecting lines. */
        try (Display.Transaction xact = display.open()) {
            for (int i = 0; i < cells.length; i++)
                if (Colors.oneLeft(cells[i]))
                    xact.setCell(i, Long.numberOfTrailingZeros(cells[i]));

            /* Affect all lines intersecting this cell. */
            nextLine = bestCell.intersects().stream().mapToObj(line -> {
                /* Make these lines moderately more favourable. */
                weights[line]++;

                /* Indicate that there is potentially work to be done by
                 * all algorithms on these lines. */
                levels[line] = algos;
                xact.setLineLevel(line, levels[line]);

                /* While we're at it, choose one of these lines as the
                 * next one worth working on. Map each line to its line
                 * and weight, and choose the maximum. */
                return new int[] { line, weights[line] };
            }).max((a, b) -> a[1] - b[1]).map(arr -> arr[0]).orElse(-1);
        }

        /* Note, we don't submit ourselves to the sink, as we are not
         * yet constructed. We assume that the caller will do that. */
    }

    /**
     * If non-negative, this is evidence that we have a line to be
     * submitted to a line algorithm. It is normally set by
     * {@link #determineNextLine()}.
     */
    private int nextLine = -1;

    private void submit() {
        assert Thread.holdsLock(this);

        sink.submit(this, getScore());
    }

    /**
     * Ensure that we have a line ready. If we don't have a line ready,
     * find out if there is one. If there is, submit ourselves for job
     * selection. If not, consider bifurcating.
     * 
     * <p>
     * This method must only be called when synchronized.
     */
    private void determineNextLine() {
        assert Thread.holdsLock(this);

        /* Do nothing if we're already primed. */
        if (nextLine >= 0) return;

        /* Do nothing if we have aborted. */
        if (aborted) {
            sink.discard(this);
            return;
        }

        /* Pick the line which has the highest score of all unlocked
         * lines. */
        nextLine = -1;
        boolean active = false;
        {
            int bestScore = Integer.MIN_VALUE;
            int bestAlgo = 0;
            for (int line = 0; line < lineCount; line++) {
                /* Ignore lines already being processed. */
                if (lineActivity.get(line)) {
                    active = true;
                    continue;
                }

                /* Ignore locked lines. */
                if (locks[line] > 0) continue;

                /* Ignore lines with no new information on them. */
                if (levels[line] == 0) continue;

                /* Ignore lines with more complex algorithms to be
                 * applied next. */
                if (levels[line] < bestAlgo) continue;

                /* Ignore lines with no better score than our nextLine
                 * so far. */
                if (weights[line] <= bestScore) continue;

                bestScore = weights[line];
                bestAlgo = weights[line];
                nextLine = line;
            }
        }

        /* If we've found a line, we're done. */
        if (nextLine >= 0) {
            assert nextLine < lineCount;
            submit();
            return;
        }

        /* We haven't found a line. Are we complete? */
        if (cellsRemaining == 0) {
            /* We are complete. */
            sink.retain(this);
            return;
        }

        if (active) {
            sink.withdraw(this);
            return;
        }

        /* We need to take a guess. Select a cell and a guess to make at
         * it. */
        /* TODO: Abstract this to a plug-in. */
        Cell bestCell = null;
        int bestScore = Integer.MIN_VALUE;
        for (Cell cell : layout.cells()) {
            int options = Long.bitCount(cells[cell.index()]);

            /* We don't make guesses at cells which are known. */
            if (options == 1) continue;

            /* Work out how good this cell is for guessing. */
            int sum =
                cell.intersects().stream().map(line -> weights[line]).sum();
            sum *= 1 + colors - options;

            if (sum > bestScore) {
                bestScore = sum;
                bestCell = cell;
            }
        }

        /* We must have found some cell, otherwise the grid would be
         * complete. */
        assert bestCell != null;

        /* Pick any remaining colour at the best cell to be ourk
         * guess. */
        final long cellState = cells[bestCell.index()];
        final int remainingColours = Long.bitCount(cellState);
        final int selectedColour = Long.numberOfTrailingZeros(cellState);
        assert selectedColour < 64;

        {
            /* Clone this grid, telling it to make the opposite guess,
             * and start it. */
            Grid other = new Grid(this, bestCell, selectedColour);
            other.determineNextLine();
        }

        /* Apply our own guess, updating the display, and selecting one
         * of the lines affected. */
        try (Display.Transaction xact = display.open()) {
            cells[bestCell.index()] = Colors.of(selectedColour);
            deduced += remainingColours - 1;
            guessed += remainingColours - 1;
            xact.setCell(bestCell.index(), selectedColour);

            nextLine = bestCell.intersects().stream().mapToObj(line -> {
                weights[line] += remainingColours - 1;
                levels[line] = algos;
                xact.setLineLevel(line, levels[line]);

                return new int[] { line, weights[line] };
            }).max((a, b) -> a[1] - b[1]).map(arr -> arr[0])
                .orElse(Integer.MIN_VALUE);
        }

        /* We are ready for activity. */
        submit();
    }

    private static final LineJob INVALID_JOB = new LineJob() {
        @Override
        public LineChallenge getLine() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getAlgorithmIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void submit(Result result) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() {}

        @Override
        public boolean isInvalid() {
            return true;
        }
    };

    /**
     * Claim a job within this grid. The returned object should be used
     * in a try-with-resources block, to ensure that resources locked to
     * create it can be released. It specifies the clue for a line, the
     * line's current state (which should be modified by a
     * {@link LineSolver} to express what it has discovered), the number
     * of colours, and the algorithm to be applied. The result of the
     * line solver should be specified by
     * {@link LineJob#submit(LineSolver.Result)} before closing the
     * object.
     * 
     * @return details of the next line to be solved, which will be
     * invalid if there are no more lines to solve
     */
    public synchronized LineJob getJob() {
        if (nextLine < 0) {
            /* All cells and lines are complete. Tell the caller that
             * there is no more work to do. */
            if (cellsRemaining == 0) return INVALID_JOB;

            /* Have we aborted (due to inconsistency, for example)?
             * Again, there is nothing for the caller to do. */
            if (aborted) return INVALID_JOB;

            /* Something odd has happened. */
            throw new IllegalStateException();
        }

        /* Claim this line. */
        final int lineNumber = nextLine;
        nextLine = -1;

        final BitSet lockedLines = new BitSet();
        final int algo = levels[lineNumber] - 1;
        final Line lineGeom = lines[lineNumber];
        final long[] workingState = new long[lineGeom.cells().size()];
        int wsi = 0;
        for (Cell cell : lineGeom.cells()) {
            final long state = cells[cell.index()];
            if ((state & (state - 1)) != 0) {
                /* This cell is in an indeterminate state, i.e., it has
                 * at least two colour possibilities. Identify as
                 * lock-worthy lines that intersect this cell, apart
                 * from the line we're working on. */
                lockedLines.or(cell.intersects());
            }
            workingState[wsi++] = state;
        }
        assert wsi == workingState.length;

        /* Deselect the current line as lock-worthy, then lock the
         * rest. */
        lockedLines.clear(lineNumber);
        lockedLines.stream().forEach(i -> locks[i]++);

        final LineChallenge line =
            new LineChallenge(colors, lineGeom.clue(),
                              new ArrayCellSequence(workingState),
                              caches[lineNumber]);

        lineActivity.set(lineNumber);
        try (Display.Transaction xact = display.open()) {
            xact.setLineActivity(lineNumber, true);
        }

        determineNextLine();

        return new LineJob() {
            private LineSolver.Result result = LineSolver.Result.ABORTED;

            @Override
            public void close() {
                completeJob(lineNumber, result, workingState, lockedLines);
            }

            @Override
            public void submit(Result result) {
                this.result = result;
            }

            @Override
            public LineChallenge getLine() {
                return line;
            }

            @Override
            public int getAlgorithmIndex() {
                return algo;
            }

            @Override
            public boolean isInvalid() {
                return false;
            }
        };
    }

    private synchronized void
        completeJob(final int lineNumber, final LineSolver.Result result,
                    final long[] workingState, final BitSet lockedLines) {
        final Line lineGeom = lines[lineNumber];

        /* Clear locks and records of activity. */
        lockedLines.stream().forEach(i -> locks[i]--);

        try (Display.Transaction xact = display.open()) {
            lineActivity.clear(lineNumber);
            xact.setLineActivity(lineNumber, false);

            /* Prevent ourselves from trying out this algorithm again,
             * until new information is obtained. */
            levels[lineNumber]--;
            xact.setLineLevel(lineNumber, levels[lineNumber]);

            switch (result) {
            case ABORTED:
                break;

            case INCONSISTENT:
                abort();
                break;

            case EXHAUSTED:
                /* Compare the current cell states with new ones. */
                int wsi = 0;
                for (Cell cell : lineGeom.cells()) {
                    final int cwsi = wsi++;
                    for (int color = 0; color < colors; color++) {
                        /* Detect whether the colour has been
                         * eliminated. */
                        if (Colors.has(workingState[cwsi], color)) {
                            if (Colors.lacks(cells[cell.index()], color)) {
                                /* The line solver has added a colour
                                 * that had already been eliminated.
                                 * TODO: Build an informative
                                 * message. */
                                throw new IllegalStateException();
                            }
                            continue;
                        }
                        if (Colors.lacks(cells[cell.index()], color)) {
                            /* The colour has not been eliminated. */
                            continue;
                        }

                        /* We've detected the elimination of a colour.
                         * Record it as cleared. */
                        deduced++;
                        cells[cell.index()] &= Colors.of(color);
                        if (cells[cell.index()] == 0) {
                            /* All colours have been eliminated from
                             * this cell. There can be no solution.
                             * TODO: Build an informative message. */
                            throw new IllegalStateException();
                        }

                        if (Colors.oneLeft(cells[cell.index()])) {
                            /* Indicate that a cell has been fully
                             * determined. */
                            cellsRemaining--;
                            xact.setCell(cell.index(), Long
                                .numberOfTrailingZeros(cells[cell.index()]));
                        }

                        /* Make this line less favourable for
                         * solving. */
                        weights[lineNumber]--;

                        /* Make intersecting lines more favourable, and
                         * suitable for submission to all algorithms. */
                        cell.intersects().stream()
                            .filter(otherLine -> otherLine != lineNumber)
                            .forEach(otherLine -> {
                                weights[otherLine]++;
                                levels[otherLine] = algos;
                                xact.setLineLevel(otherLine, levels[otherLine]);
                            });
                    }
                }
            }
        }

        determineNextLine();
    }

    /**
     * Determine whether the grid has been completed. This should only
     * be tested after {@link #getJob()} has returned {@code null},
     * indicating that all lines have been checked for consistency since
     * completion.
     * 
     * @return {@code true} if the grid has no more unknown cells
     */
    public synchronized boolean isComplete() {
        return nextLine < 0 && cellsRemaining == 0 && !aborted;
    }

    /**
     * Register a line solver with this grid. If any line solver submits
     * {@link Result#INCONSISTENT} through
     * {@link LineJob#submit(LineSolver.Result)}, all registered solvers
     * will be aborted.
     * 
     * <p>
     * Solvers are retained with a weak reference.
     * 
     * @param solver the solver to be registered
     */
    public synchronized void registerSolver(LineSolver solver) {
        solvers.add(solver);
    }

    private boolean aborted;

    /**
     * Abort all registered line solvers.
     * 
     * @see LineSolver#abort()
     */
    public synchronized void abort() {
        aborted = true;
        for (LineSolver solver : solvers)
            solver.abort();
        notifyAll();
    }

    private final Collection<LineSolver> solvers =
        Collections.newSetFromMap(new WeakHashMap<>());
}
