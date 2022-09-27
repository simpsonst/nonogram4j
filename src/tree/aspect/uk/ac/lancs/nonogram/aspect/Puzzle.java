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

package uk.ac.lancs.nonogram.aspect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds puzzle data, including geometry type and named banks of clues.
 * Meta-data such as title and ownership are also included.
 * 
 * <p>
 * The geometry of a puzzle is identified by a string, such as
 * <samp>rect</samp> for a rectangular grid. The geometry defines a
 * number of named banks of lines, such as <samp>rows</samp> and
 * <samp>cols</samp>. The lines of some banks have clues, but other
 * banks can exist without clues.
 * 
 * @author simpsons
 */
public final class Puzzle {
    /**
     * @resume The puzzle geometry type
     */
    public final String geometryType;

    /**
     * @resume The banks' clue data
     */
    public final Map<String, List<Clue>> clues;

    /**
     * @resume Tiles and index for each colour
     */
    public final Map<Hue, HueInfo> hueInfo;

    /**
     * Maps colour numbers to hues.
     */
    public final List<Hue> hueIndex;

    /**
     * Get a palette given a set of display attributes.
     * 
     * @param attrs the display attributes
     * 
     * @return a palette matching those attributes
     */
    public Palette palette(Collection<? extends String> attrs) {
        Map<Hue,
            Tile> mt = hueInfo.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          e -> e.getValue().selectTile(attrs)))
                .entrySet().stream().filter(e -> e.getValue().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          e -> e.getValue().get()));
        Palette.Builder b = Palette.start();
        for (Hue h : hueIndex)
            b = b.add(mt.get(h));
        b = b.setUnknown(mt.get(Hue.UNKNOWN));
        return b.create();
    }

    /**
     * Get the colour for a given index.
     * 
     * @param n the colour index
     * 
     * @return the corresponding colour
     * 
     * @throws IndexOutOfBoundsException if the index is negative or not
     * less than the number of colours
     */
    public Hue hue(int n) {
        return hueIndex.get(n);
    }

    /**
     * Get the numeric index for a colour.
     * 
     * @param color the colour whose index is sought
     * 
     * @return the numeric index; or <code>-1</code> if not defined
     */
    public int index(Hue color) {
        HueInfo info = hueInfo.get(color);
        if (info == null) return -1;
        assert info.index >= 0;
        assert info.index < hueIndex.size();
        return info.index;
    }

    /**
     * Describes a colour as used by a puzzle.
     */
    public static class HueInfo {
        /**
         * Numbers the colour. All colours in a puzzle will have an
         * integer identifier in the range [0, <var>n</var>), where
         * <var>n</var> is the number of distinct colours, including the
         * background (0).
         */
        public final int index;

        /**
         * Maps context specifications to tile descriptions. The key of
         * the map is a logical OR of logical ANDs.
         */
        public final Map<Set<Set<String>>, Tile> tiles;

        HueInfo(int index, Map<Set<Set<String>>, Tile> tiles) {
            this.index = index;
            this.tiles = tiles;
        }

        Optional<Tile> selectTile(Collection<? extends String> attrs) {
            return tiles.entrySet().stream()
                .filter(e -> Puzzle.testContext(attrs, e.getKey()))
                .map(Map.Entry::getValue).findFirst();
        }
    }

    /**
     * @resume Puzzle meta-data, such as title, ownership, etc
     */
    public final Map<String, Metadatum> meta;

    private Puzzle(String geometryType, Map<String, List<Clue>> clues,
                   Map<Hue, Map<Set<Set<String>>, Tile>> tiles,
                   Map<String, Metadatum> meta) {
        this.geometryType = geometryType;
        this.clues = clues;
        this.meta = meta;

        /* Get all the colours without fixed positions. */
        List<Hue> unfixed = tiles.keySet().stream()
            .filter(h -> h != Hue.UNKNOWN && h != Hue.BACKGROUND)
            .collect(Collectors.toList());

        /* Create an index which places the background colour first,
         * followed by all the unfixed colours. The 'unknown' colour is
         * excluded. */
        List<Hue> index = new ArrayList<>(unfixed.size() + 1);
        index.add(Hue.BACKGROUND);
        index.addAll(unfixed);
        this.hueIndex = List.copyOf(index);

        /* Combine the reverse mapping from colour to index with the
         * mapping from colour to tile options. */
        Map<Hue, HueInfo> hueInfo = new HashMap<>();
        hueInfo.put(Hue.UNKNOWN, new HueInfo(-1, tiles.get(Hue.UNKNOWN)));
        for (int i = 0; i < this.hueIndex.size(); i++) {
            Hue key = this.hueIndex.get(i);
            var frozenMap = Collections.unmodifiableMap(tiles.get(key));
            hueInfo.put(key, new HueInfo(i, frozenMap));
        }
        this.hueInfo = Map.copyOf(hueInfo);
    }

    /**
     * Builds puzzles.
     */
    public static class Builder {
        private Builder() {}

        private String geometryType;

        private final Map<String, List<Clue>> clues = new HashMap<>();

        private final Map<Hue, Map<Set<Set<String>>, Tile>> tiles =
            new HashMap<>();

        private final Map<String, Map<Locale, String>> lmeta = new HashMap<>();

        /**
         * Create a puzzle from the current state.
         * 
         * @return the new puzzle
         */
        public Puzzle create() {
            return new Puzzle(geometryType, Map.copyOf(clues), tiles,
                              Metadatum.map(lmeta));
        }

        /**
         * Set the geometry of the puzzle.
         * 
         * @param type the puzzle geometry
         * 
         * @return this object
         */
        public Builder geometry(String type) {
            this.geometryType = type;
            return this;
        }

        /**
         * Add a tile.
         * 
         * @param color the colour number represented by the tile; 0 for
         * the background
         * 
         * @param context the contexts in which the tile applies
         * 
         * @param tile the tile specification
         * 
         * @return this object
         */
        public Builder
            tile(Hue color,
                 Collection<? extends Collection<? extends String>> context,
                 Tile tile) {
            /* Freeze the nested data. */
            Set<Set<String>> nctxt =
                context.stream()
                    .map(e -> e.stream().map(Object::toString)
                        .collect(Collectors.toSet()))
                    .collect(Collectors.toSet());

            /* Store the frozen data, but keep the insertion order. */
            tiles.computeIfAbsent(color, k -> new LinkedHashMap<>()).put(nctxt,
                                                                         tile);
            return this;
        }

        /**
         * Set a bank of clues for the puzzle.
         * 
         * @param name the bank name; one defined by the geometry
         * 
         * @param clues the clues for the bank (to be copied)
         * 
         * @return this object
         */
        public Builder clues(String name, List<? extends Clue> clues) {
            this.clues.put(name, List.copyOf(clues));
            return this;
        }

        /**
         * Set a locale-specific meta-datum.
         * 
         * @param name the name of the datum
         * 
         * @param locale the locale the value is suited for
         * 
         * @param value the new value
         * 
         * @return this object
         * 
         * @throws IllegalArgumentException if the name is reserved
         */
        public Builder meta(String name, Locale locale, String value) {
            checkMetaName(name);
            this.lmeta.computeIfAbsent(name, k -> new HashMap<>()).put(locale,
                                                                       value);
            return this;
        }
    }

    /**
     * Check that a meta-data key doesn't match a reserved name.
     * Reserved names are the keywords from the legacy format.
     * 
     * @param name the name to test
     * 
     * @throws IllegalArgumentException if the name is reserved
     */
    private static void checkMetaName(String name) {
        switch (name) {
        case "width":
        case "height":
        case "maxrule":
        case "rows":
        case "columns":
            throw new IllegalArgumentException("bad meta name: " + name);
        }
    }

    /**
     * Start building a puzzle.
     * 
     * @return a fresh builder
     */
    public static Builder start() {
        return new Builder();
    }

    private static final Pattern SPACE_SEP = Pattern.compile("\\s+");

    private static final Pattern COMMA_SEP = Pattern.compile("\\s*,+\\s*");

    /**
     * Convert a display context specification to textual form. Outer
     * elements are joined by a comma and a space. Inner elements are
     * joined by a space. The argument can be regenerated by passing the
     * result to {@link #parseContext(CharSequence)}.
     * 
     * @param pred the specification
     * 
     * @return a string representation
     */
    public static String
        generateContext(Collection<? extends Collection<? extends String>> pred) {
        return pred.stream()
            .map(e -> e.stream().collect(Collectors.joining(" ")))
            .collect(Collectors.joining(", "));
    }

    /**
     * Defines the default display context. This is <samp>partial</samp>
     * OR <samp>complete</samp>.
     */
    public static final Set<Set<String>> DEFAULT_CONTEXT =
        parseContext("partial, complete");

    /**
     * Parse a display context specification. The input is first split
     * on commas. Each element's surrounding white space is then
     * removed, and then split on spaces, and converted to a set of
     * strings. Each of these sets then becomes an element of the
     * result.
     * 
     * <p>
     * For example, <samp>partial, complete</samp> is converted into
     * <code>Set.of(Set.of("partial"), Set.of("complete"))</code>,
     * indicating that a tile (say) attributed with this string is
     * suitable in <samp>partial</samp> or <samp>complete</samp> display
     * contexts. The comma acts as a logical OR; space-separated words
     * act as a logical AND.
     * 
     * <p>
     * {@link #generateContext(Collection)} will convert the result back
     * to a string.
     * 
     * @param text the text to parse.
     * 
     * @return an immutable set of alternative immutable context
     * attribute sets
     */
    public static Set<Set<String>> parseContext(CharSequence text) {
        return Stream.of(COMMA_SEP.split(text)).map(String::trim)
            .map(s -> Stream.of(SPACE_SEP.split(s)).collect(Collectors.toSet()))
            .collect(Collectors.toSet());
    }

    /**
     * Test whether a display description matches a display context
     * specification. A display is described by a set of attributes. The
     * following are defined:
     * 
     * <dl>
     * 
     * <dt><samp>partial</samp></dt>
     * 
     * <dd>A puzzle is being displayed in the process of being
     * solved.</dd>
     * 
     * <dt><samp>complete</samp></dt>
     * 
     * <dd>A puzzle is being displayed in its completed state.</dd>
     * 
     * </dl>
     * 
     * @see #parseContext(CharSequence) the format of a display context
     * specification
     * 
     * @param attrs the display description
     * 
     * @param pred the context specification
     * 
     * @return {@code true} if the description matches the content;
     * {@code false} otherwise
     */
    public static boolean
        testContext(Collection<? extends String> attrs,
                    Collection<? extends Collection<? extends String>> pred) {
        return pred.stream().anyMatch(e -> attrs.containsAll(e));
    }
}
