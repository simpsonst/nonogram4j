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

import java.io.Console;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import uk.ac.lancs.nonogram.aspect.Palette;
import uk.ac.lancs.nonogram.aspect.Tile;
import uk.ac.lancs.nonogram.display.Display;
import uk.ac.lancs.nonogram.geom.WidgetDisplay;

/**
 * This class combines a display-type-independent puzzle state with a
 * type-dependent widget. When told to change state through its
 * inherited {@link Display} interface, it updates the state, then
 * informs the widget to reflect that change.
 * 
 * @resume A rectangular Nonogram display
 * 
 * @author simpsons
 * 
 * @param <C> The type of the widget required by the display
 */
final class RectangularDisplay<C> implements WidgetDisplay<C> {
    private final DisplayState state;

    private final Updatable updatable;

    private final C widget;

    public RectangularDisplay(DisplayState state, Updatable updatable,
                              C widget) {
        this.state = state;
        this.updatable = updatable;
        this.widget = widget;
    }

    @Override
    public Transaction open() {
        return new Transaction() {
            private Palette palette;

            private Map<Integer, Integer> newLineLevels = new HashMap<>();

            private BitSet newLineActivityStates = new BitSet();

            private BitSet newLineActivity = new BitSet();

            private Map<Integer, Integer> newCellStates = new HashMap<>();

            @Override
            public void setLineLevel(int index, int state) {
                newLineLevels.put(index, state);
            }

            @Override
            public void setLineActivity(int index, boolean active) {
                newLineActivityStates.set(index, active);
                newLineActivity.set(index);
            }

            @Override
            public void setCell(int index, int color) {
                newCellStates.put(index, color);
            }

            @Override
            public void reset() {
                newLineLevels.clear();
                newLineActivity.clear();
                newCellStates.clear();
                palette = null;
            }

            @Override
            public void setPalette(Palette palette) {
                this.palette = palette;
            }

            @Override
            public void close() {
                /* Create a runnable based on the maps to set the new
                 * states, and tell the GUI which parts to redraw. Get
                 * the GUI to run it on the EDT. */
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        commit(palette,
                               newLineLevels,
                               newLineActivityStates,
                               newLineActivity,
                               newCellStates);
                    }
                });
            }
        };
    }

    private void commit(Palette palette, Map<Integer, Integer> newLineLevels,
                        BitSet newLineActivityStates, BitSet newLineActivity,
                        Map<Integer, Integer> newCellStates) {
        /* Update changed level indicators's states, and inform the
         * widget. */
        for (Map.Entry<Integer, Integer> entry : newLineLevels.entrySet()) {
            int index = entry.getKey();
            int level = entry.getValue();
            state.levels[index] = level;
            if (index < state.height)
                updatable.updateRowLevel(index);
            else
                updatable.updateColumnLevel(index - state.height);
        }

        /* Update changed activity indicators' states, and inform the
         * widget. */
        for (int index = newLineActivity.nextSetBit(0); index >= 0;
             index = newLineActivity.nextSetBit(index + 1)) {
            boolean activity = newLineActivityStates.get(index);
            state.activities.set(index, activity);
            if (index < state.height)
                updatable.updateRowActivity(index);
            else
                updatable.updateColumnActivity(index - state.height);
        }

        /* Update changed cells' states, but don't update the widget
         * yet. */
        for (Map.Entry<Integer, Integer> entry : newCellStates.entrySet()) {
            int index = entry.getKey();
            int color = entry.getValue();
            if (index >= 0 && index < state.cellColours.length)
                state.cellColours[index] = color;
        }

        /* Update the palette if changed, and then mark for update cells
         * with colours changed in the palette. */
        if (palette != null) {
            /* Change the palette, keeping track of the old one for a
             * moment. */
            Palette oldPalette = state.palette;
            state.palette = palette;

            /* Find out which tile types changed, and inform the
             * widget. */
            BitSet changed = new BitSet();
            final int min =
                Math.min(state.palette.colors(), oldPalette.colors());
            final int max =
                Math.max(state.palette.colors(), oldPalette.colors());
            for (int i = 0; i < min; i++) {
                Tile oldCol = oldPalette.getColor(i);
                Tile newCol = state.palette.getColor(i);
                if (oldCol.equals(newCol)) continue;
                changed.set(i);
            }
            changed.set(min, max);

            /* Find cells whose colours have changed in the palette. */
            for (int i = 0; i < state.cellColours.length; i++)
                if (changed.get(state.cellColours[i])) {
                    /* We can use any colour value here, as we don't use
                     * it from here on. */
                    newCellStates.put(i, 0);
                }
        }

        /* Now update all changed cells, plus cells whose colours have
         * been changed in the palette, */
        for (int index : newCellStates.keySet()) {
            int x = index % state.width;
            int y = index / state.width;
            updatable.updateCell(x, y);
        }
    }

    @Override
    public C getWidget() {
        return widget;
    }

    /**
     * @undocumented
     */
    public static void main(String[] args) throws Exception {
        final DisplayState state = new DisplayState(20, 10, 4);
        final SwingWidget[] widget = { null };

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Test");
                widget[0] = new SwingWidget(state);
                frame.add(widget[0]);
                frame.revalidate();
                frame.setVisible(true);
            }
        });

        RectangularDisplay<JPanel> display =
            new RectangularDisplay<JPanel>(state, widget[0], widget[0]);

        Console console = System.console();
        String line;
        while ((line = console.readLine("> ")) != null) {
            String[] words = line.split("\\s+");
            console.printf("Command: %s%n", Arrays.asList(words));
            try {
                if (words.length >= 4 && words[0].equals("cell")) {
                    int x = Integer.parseInt(words[1]);
                    int y = Integer.parseInt(words[2]);
                    int col = Integer.parseInt(words[3]);
                    console.printf("(%d,%d) set to %d%n", x, y, col);
                    try (Display.Transaction xact = display.open()) {
                        xact.setCell(x + y * state.width, col);
                    }
                }
            } catch (NumberFormatException ex) {
                console.printf("Error: %s%n", ex.getMessage());
            }
        }
        System.exit(0);
    }
}
