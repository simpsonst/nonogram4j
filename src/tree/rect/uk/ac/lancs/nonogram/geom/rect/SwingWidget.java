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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import uk.ac.lancs.nonogram.Tile;

/**
 * @resume A Swing component that displays a two-dimensional Nonogram
 * state, automatically adjusting to fit the available space
 * 
 * @author simpsons
 */
final class SwingWidget extends JPanel implements Updatable {
    private static final long serialVersionUID = 1L;

    private final DisplayState state;

    private final Color[] algoColours;

    private int scale = 1;

    private int margin = 0;

    private final Point offset = new Point(0, 0);

    public SwingWidget(final DisplayState state) {
        this.state = state;
        algoColours = createShading(MAX_LEVEL, MIN_LEVEL, state.algos + 1);
        if (false) {
            // TODO: Remove old code.
            algoColours = new Color[state.algos + 1];
            for (int i = 0; i < algoColours.length; i++) {
                final float frac = (float) i / state.algos;
                algoColours[i] = new Color(frac, frac, frac);
            }
        }
        ALGORITHM_SHADING = new Color[state.algos + 1][];
        updateShading();

        final ComponentListener cl = new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                dimensionsChanged();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                dimensionsChanged();
            }
        };

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Dimension preferredSize =
                    new Dimension((state.width + 2) * 5 + 1,
                                  (state.height + 2) * 5 + 1);
                setPreferredSize(preferredSize);
                addComponentListener(cl);
            }
        });
    }

    private void dimensionsChanged() {
        if (!isVisible()) return;

        final int availableWidth = getWidth();
        final int availableHeight = getHeight();
        if (false) {
            System.err
                .printf("Fitting (%d+2,%d+2) cells to (%d,%d) pixels...%n",
                        state.width, state.height, availableWidth,
                        availableHeight);
        }

        /* How many times can we get our puzzle into the available width
         * or height? */
        final int xTimes = (availableWidth + 1) / (state.width + 2);
        final int yTimes = (availableHeight + 1) / (state.height + 2);
        scale = Math.min(xTimes, yTimes);
        if (false) {
            System.err.printf("   x %d times; y %d times%n", xTimes, yTimes);
            System.err.printf("   Chosen %d times%n", scale);
        }
        if (scale > 1) {
            margin = 1;
            scale--;
        } else {
            scale = 1;
            margin = 0;
        }
        updateShading();
        final int step = scale + margin;

        /* Work out the top-left offset so that the puzzle is
         * centred. */
        final int pixelWidth = (state.width + 2) * step - 1;
        final int pixelHeight = (state.height + 2) * step - 1;
        offset.x = (availableWidth - pixelWidth) / 2;
        offset.y = (availableHeight - pixelHeight) / 2;
        if (false) {
            System.err.printf("   Top left at (%d,%d)%n", offset.x, offset.y);
        }

        /* Everything's out of date now. */
        repaint();
    }

    @Override
    public void updateCell(int x, int y) {
        if (!isVisible()) return;
        if (false) {
            System.err.printf("Updating cell (%d,%d)%n...", x, y);
        }

        /* Work out the bounding box for the given cell, and tell the
         * GUI to repaint it. */

        /* Cell (0,0) is at position (1,1). */
        x++;
        y++;

        /* The cells are (scale + margin) pixels apart. */
        final int step = scale + margin;
        x *= step;
        y *= step;

        /* The top-left cell is at an offset. */
        x += offset.x;
        y += offset.y;

        if (false) {
            System.err.printf("  Repainting [%d,%d]-(%d,%d)%n...", x, y,
                              x + scale, y + scale);
        }
        repaint(x, y, scale, scale);
    }

    @Override
    public void updateRowActivity(int index) {
        /* Work out the bounding box for the given activity indicator,
         * and tell the GUI to repaint it. */

        /* The row activity indicators are at the cell positions (-1,
         * index). */
        updateCell(-1, index);
    }

    @Override
    public void updateColumnActivity(int index) {
        /* Work out the bounding box for the given activity indicator,
         * and tell the GUI to repaint it. */

        /* The row activity indicators are at the cell positions (index,
         * -1). */
        updateCell(index, -1);
    }

    @Override
    public void updateRowLevel(int index) {
        /* Work out the bounding box for the given level indicator, and
         * tell the GUI to repaint it. */

        /* The row level indicators are at the cell positions (width,
         * index). */
        updateCell(state.width, index);
    }

    @Override
    public void updateColumnLevel(int index) {
        /* Work out the bounding box for the given level indicator, and
         * tell the GUI to repaint it. */

        /* The row level indicators are at the cell positions (index,
         * height). */
        updateCell(index, state.height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        /* We don't draw anything above or to the left of the offset, so
         * dispense with it. */
        g.translate(offset.x, offset.y);

        Rectangle clip = new Rectangle();
        g.getClipBounds(clip);
        g.clearRect(clip.x, clip.y, clip.width, clip.height);

        final int step = scale + margin;

        /* What are the minimum (inclusive) and maximum (exclusive)
         * co-ordinates covered by the graphic area? */
        final int minx = Math.max((clip.x + 1) / step, 0);
        final int miny = Math.max((clip.y + 1) / step, 0);
        final int maxx =
            Math.min((clip.x + clip.width + step - 1) / step, state.width + 2);
        final int maxy = Math.min((clip.y + clip.height + step - 1) / step,
                                  state.height + 2);
        if (false) {
            System.err.printf("Repaint pixels [%d,%d]-(%d,%d)%n", clip.x,
                              clip.y, clip.x + clip.width,
                              clip.y + clip.height);
            System.err.printf("   Step = %d + %d = %d%n", scale, margin, step);
            System.err.printf("Repainting cells [%d,%d] to (%d,%d)...\n", minx,
                              miny, maxx, maxy);
        }

        /* Repaint the indicated cells according to their current state
         * and the palette. */
        for (int x = minx; x < maxx; x++) {
            for (int y = miny; y < maxy; y++) {
                if (x == 0) {
                    if (y > 0 && y <= state.height) {
                        /* This is a row activity indicator. */
                        plotLight(g, x * step, y * step,
                                  state.activities.get(y - 1) ?
                                      ACTIVITY_SHADING : INACTIVITY_SHADING);
                    }
                } else if (x == state.width + 1) {
                    if (y > 0 && y <= state.height) {
                        /* This is a row level indicator. */
                        int level = state.levels[y - 1];
                        if (level < 0) {
                            level = 0;
                        } else {
                            if (level > state.algos) level = state.algos;
                        }
                        plotLight(g, x * step, y * step,
                                  ALGORITHM_SHADING[level]);
                    }
                } else if (y == 0) {
                    if (x > 0 && x <= state.width) {
                        /* This is a column activity indicator. */
                        plotLight(g, x * step, y * step,
                                  state.activities.get(x - 1 + state.height) ?
                                      ACTIVITY_SHADING : INACTIVITY_SHADING);
                    }
                } else if (y == state.height + 1) {
                    if (x > 0 && x <= state.width) {
                        /* This is a column level indicator. */
                        int level = state.levels[x - 1 + state.height];
                        if (level < 0) {
                            level = 0;
                        } else {
                            if (level > state.algos) level = state.algos;
                        }
                        plotLight(g, x * step, y * step,
                                  ALGORITHM_SHADING[level]);
                    }
                } else {
                    /* This must be a regular cell. */
                    assert x >= 1;
                    assert x <= state.width;
                    assert y >= 1;
                    assert y <= state.height;
                    final int cellx = x - 1;
                    final int celly = y - 1;
                    final int cellIndex = cellx + celly * state.width;
                    Tile tile = getCellTile(state.cellColours[cellIndex]);
                    switch (tile.shape) {
                    case BLANK:
                        plotBox(g, x * step, y * step, scale, tile.background);
                        break;

                    case DOT:
                        plotBox(g, x * step, y * step, scale, tile.background);
                        plotDot(g, x * step, y * step, scale, tile.foreground);
                        break;

                    case SOLID:
                        plotBox(g, x * step, y * step, scale, tile.foreground);
                        break;
                    }
                }
            }
        }
    }

    private static final Color ACTIVITY = new Color(0.9f, 0.6f, 0.2f);

    private static final Color INACTIVITY = new Color(0.4f, 0.2f, 0.0f);

    private static final Color MAX_LEVEL = new Color(0.8f, 0.8f, 0.8f);

    private static final Color MIN_LEVEL = Color.BLACK;

    @SuppressWarnings("unused")
    private Color getActivityColor(boolean activity) {
        return activity ? ACTIVITY : INACTIVITY;
    }

    private Color[] ACTIVITY_SHADING;

    private Color[] INACTIVITY_SHADING;

    private final Color[][] ALGORITHM_SHADING;

    private void updateShading() {
        final int shades = (scale + 1) / 2;
        if (ACTIVITY_SHADING != null && ACTIVITY_SHADING.length == shades)
            return;
        ACTIVITY_SHADING = createShading(Color.WHITE, ACTIVITY, shades);
        INACTIVITY_SHADING = createShading(Color.WHITE, INACTIVITY, shades);
        for (int i = 0; i <= state.algos; i++) {
            ALGORITHM_SHADING[i] =
                createShading(Color.WHITE, getLevelColor(i), shades);
        }
    }

    private Color getLevelColor(int level) {
        if (level < 0 || level > state.algos)
            throw new IllegalArgumentException("algorithm " + level
                + " not in 0.." + state.algos);
        return algoColours[level];
    }

    private Tile getCellTile(int code) {
        if (code < 0 || code >= state.palette.colors())
            return state.palette.getUnknown();
        try {
            return state.palette.getColor(code);
        } catch (IndexOutOfBoundsException ex) {
            return state.palette.getUnknown();
        }
    }

    private static void plotBox(Graphics g, int x, int y, int size, Color col) {
        g.setColor(col);
        g.fillRect(x, y, size, size);
    }

    private static void plotDot(Graphics g, int x, int y, int size, Color col) {
        if (size < 3) return;
        g.setColor(col);
        if (size == 3) {
            g.fillRect(x + 1, y + 1, 1, 1);
        } else {
            g.fillOval(x + size / 4, y + size / 4, size / 2, size / 2);
        }
    }

    private void plotLight(Graphics g, int x, int y, Color[] shades) {
        if (scale < 4) {
            g.setColor(shades[shades.length - 1]);
            g.fillRect(x, y, scale, scale);
            return;
        }

        if (scale < 7) {
            g.setColor(Color.BLACK);
            g.fillRect(x, y, scale, scale);
            g.setColor(shades[shades.length - 1]);
            g.fillRect(x + 1, y + 1, scale - 2, scale - 2);
            return;
        }

        for (int i = 0; i < shades.length; i++) {
            g.setColor(shades[i]);
            g.fillOval(x + i / 2, y + i / 2, scale - i * 2, scale - i * 2);
        }
    }

    private static Color[] createShading(Color to, Color from, final int size) {
        final Color[] out = new Color[size];
        if (size == 1) {
            out[0] = to;
            return out;
        }
        final int baseRed = from.getRed();
        final int baseGreen = from.getGreen();
        final int baseBlue = from.getBlue();
        final int redDiff = to.getRed() - baseRed;
        final int greenDiff = to.getGreen() - baseGreen;
        final int blueDiff = to.getBlue() - baseBlue;
        for (int i = 0; i < size; i++) {
            double frac = (double) i / (size - 1);
            int red = baseRed + (int) (redDiff * frac);
            int green = baseGreen + (int) (greenDiff * frac);
            int blue = baseBlue + (int) (blueDiff * frac);
            out[i] = new Color(red, green, blue);
        }
        return out;
    }
}
