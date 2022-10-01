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

package uk.ac.lancs.nonogram.solver.swing;

import java.awt.Component;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.WeakHashMap;
import uk.ac.lancs.nonogram.display.Display;
import uk.ac.lancs.nonogram.geom.DisplayType;
import uk.ac.lancs.nonogram.geom.Displayable;
import uk.ac.lancs.nonogram.geom.WidgetDisplay;
import uk.ac.lancs.nonogram.geom.WidgetDisplayFactory;
import uk.ac.lancs.nonogram.solver.GridSink;
import uk.ac.lancs.nonogram.solver.GridSource;
import uk.ac.lancs.nonogram.solver.KeyedDisplayFactory;

/**
 * Manages grids according to most recently reported scores and
 * activity.
 * 
 * @author simpsons
 *
 * @param <K> the grid type
 */
public class SwingGridManager<K>
    implements GridSink<K>, GridSource<K>, KeyedDisplayFactory<K> {
    private final WidgetDisplayFactory<Component> displayFactory;

    /**
     * Create a manager.
     * 
     * @param displayable used to create a factory for Swing displays
     * 
     * @throws IllegalArgumentException if Swing displays cannot be
     * created from the argument
     */
    public SwingGridManager(Displayable displayable) {
        displayFactory = displayable.getDisplayFactory(DisplayType.SWING);
        if (displayFactory == null)
            throw new IllegalArgumentException("Swing displays not supported");
    }

    private class Monitor implements Display, Comparable<Monitor> {
        private final K grid;

        private final WidgetDisplay<Component> display =
            displayFactory.newDisplay();

        private final BitSet activeLines = new BitSet();

        public Monitor(K grid) {
            this.grid = grid;
        }

        private double score;

        private int activity;

        @Override
        public int compareTo(Monitor other) {
            int activeDiff = other.activity - activity;
            if (activeDiff != 0) return activeDiff;
            return Double.compare(score, other.score);
        }

        /**
         * Record the activity of this display.
         * 
         * @param index the index of the line whose activity is changing
         * 
         * @param active the new state for the activity
         * 
         * @return 0 if there is no overall change in the display's
         * activity, +1 if the display previously was inactive and is
         * now active, and -1 if the display was inactive and is now
         * active
         */
        private synchronized void setLineActivity(int index, boolean active) {
            activeLines.set(index, active);
        }

        @Override
        public Transaction open() {
            final WidgetDisplay.Transaction xact = display.open();
            return new Transaction() {
                @Override
                public void setLineLevel(int index, int state) {
                    xact.setLineLevel(index, state);
                }

                @Override
                public void setLineActivity(int index, boolean active) {
                    xact.setLineActivity(index, active);
                    Monitor.this.setLineActivity(index, active);
                }

                @Override
                public void setCell(int index, int color) {
                    xact.setCell(index, color);
                }

                @Override
                public void reset() {
                    xact.reset();
                }

                @Override
                public void close() {
                    update(Monitor.this);
                    xact.close();
                }
            };
        }
    }

    private synchronized void update(Monitor display) {
        int newActivity = display.activeLines.cardinality();
        if (newActivity != display.activity) {
            selectionOrder.remove(display);
            display.activity = newActivity;
            selectionOrder.add(display);
        }
    }

    public synchronized Display newDisplay(K grid) {
        Monitor result = new Monitor(grid);
        map.put(grid, result);
        return result;
    }

    /**
     * Provides a reverse mapping from external grid to internal
     * monitor.
     */
    private final Map<K, Monitor> map = new WeakHashMap<>();

    /**
     * Orders displays by preference for selection.
     */
    private final NavigableSet<Monitor> selectionOrder = new TreeSet<>();

    private final Collection<Monitor> retained = new HashSet<>();

    @Override
    public synchronized K selectGrid() {
        Monitor display = selectionOrder.first();
        if (display == null) return null;
        return display.grid;
    }

    @Override
    public synchronized void submit(K grid, double score) {
        Monitor display = map.get(grid);
        if (display == null) return;
        selectionOrder.remove(display);
        display.score = score;
        selectionOrder.add(display);
    }

    @Override
    public synchronized void retain(K grid) {
        Monitor display = map.get(grid);
        if (display == null) return;
        retained.add(display);
        selectionOrder.remove(display);
    }

    @Override
    public synchronized void discard(K grid) {
        Monitor display = map.remove(grid);
        if (display == null) return;
        retained.remove(display);
    }

    @Override
    public void withdraw(K grid) {
        Monitor display = map.get(grid);
        if (display == null) return;
        selectionOrder.remove(display);
    }
}
