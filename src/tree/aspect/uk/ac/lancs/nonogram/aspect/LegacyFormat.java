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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads puzzles in the legacy format.
 *
 * @author simpsons
 * 
 * @see https://www.lancaster.ac.uk/~simpsons/nonogram/fmt2 puzzle
 * format
 */
public final class LegacyFormat implements Format {
    private LegacyFormat() {}

    /**
     * The sole instance of this class
     */
    public static final LegacyFormat INSTANCE = new LegacyFormat();

    private static final Pattern OLD_CLUE_SEP = Pattern.compile("\\s*,\\s*");

    private static final Pattern OLD_COMMAND_FORMAT =
        Pattern.compile("^\\s*([^\\s]+)");

    private static final Pattern CHAR_ENT_FMT =
        Pattern.compile("&(#x([0-9a-fA-F])+|#([0-9])+|([^;]+));");

    private static final Pattern LOCALE_KEY =
        Pattern.compile("^([^.]+)\\.(.*)$");

    private static final Map<String, Integer> characterEntities;

    static {
        ResourceBundle charEnts =
            ResourceBundle.getBundle(LegacyFormat.class.getPackage().getName()
                + "CharacterEntities");
        Map<String, Integer> mutable = new HashMap<>();
        for (String key : charEnts.keySet())
            mutable.put(key, Integer.parseInt(charEnts.getString(key), 16));
        characterEntities = Map.copyOf(mutable);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws UnsupportedOperationException always
     */
    @Override
    public void write(Puzzle puzzle, Writer out) throws IOException {
        throw new UnsupportedOperationException("unimplemented"); // TODO
    }

    @Override
    public Puzzle read(Reader in, Locale defaultLocale) throws IOException {
        BufferedReader rdr = new BufferedReader(in);
        Puzzle.Builder builder = Puzzle.start().geometry("rect");

        /* Define the monochrome palette. */
        final Hue SOLID = Hue.distinct();
        builder = builder.tile(Hue.BACKGROUND, Set.of(Set.of("complete")),
                               Tile.COMPLETE_MONOCHROME_EMPTY);
        builder = builder.tile(Hue.BACKGROUND, Set.of(Set.of("partial")),
                               Tile.WORKING_MONOCHROME_EMPTY);
        builder =
            builder.tile(SOLID, Set.of(Set.of("partial"), Set.of("complete")),
                         Tile.COMPLETE_MONOCHROME_FILLED);
        builder = builder.tile(Hue.UNKNOWN, Set.of(Set.of("partial")),
                               Tile.WORKING_MONOCHROME_UNKNOWN);

        /* Create space for initial data. */
        List<Clue> rows = null, columns = null;

        /* Parse the lines. */
        int width = -1, height = -1;
        boolean onRows = false, onColumns = false;
        int lineNo = 0;
        String line;
        while ((line = rdr.readLine()) != null) {
            lineNo++;
            Matcher m = OLD_COMMAND_FORMAT.matcher(line);
            if (!m.matches()) continue;
            String cmd = m.group(1);
            String args = line.substring(m.end());
            if (cmd.equals("maxrule")) {
                // Ignore.
            } else if (cmd.equals("width")) {
                if (columns != null)
                    throw new IllegalArgumentException("too many width "
                        + "specifications: (" + lineNo + ")");
                width = Integer.parseInt(args.trim());
                columns = new ArrayList<>(width);
            } else if (cmd.equals("columns")) {
                args = args.trim();
                if (!args.isEmpty()) {
                    if (columns != null)
                        throw new IllegalArgumentException("too many width "
                            + "specifications: (" + lineNo + ")");
                    width = Integer.parseInt(args);
                    columns = new ArrayList<>(width);
                }
                onColumns = true;
                onRows = false;
            } else if (cmd.equals("height")) {
                if (rows != null)
                    throw new IllegalArgumentException("too many height "
                        + "specifications: (" + lineNo + ")");
                height = Integer.parseInt(args.trim());
                rows = new ArrayList<>(width);
            } else if (cmd.equals("rows")) {
                args = args.trim();
                if (!args.isEmpty()) {
                    if (rows != null)
                        throw new IllegalArgumentException("too many height "
                            + "specifications: (" + lineNo + ")");
                    height = Integer.parseInt(args);
                    rows = new ArrayList<>(height);
                }
                onColumns = false;
                onRows = true;
            } else if (Character.isAlphabetic(cmd.charAt(0))) {
                Matcher mt = LOCALE_KEY.matcher(cmd);
                final String key;
                final Locale locale;
                if (mt.matches()) {
                    key = mt.group(1);
                    locale = Locale.forLanguageTag(mt.group(2));
                } else {
                    key = cmd;
                    locale = defaultLocale;
                }
                builder = builder.meta(key, locale, parseMetaValue(args));
            } else if (onRows || onColumns) {
                String[] parts = OLD_CLUE_SEP.split(line.trim());
                List<Bar> blocks = new ArrayList<>(parts.length);
                for (String part : parts) {
                    Bar block = Bar.of(Integer.parseInt(part), SOLID);
                    blocks.add(block);
                }
                if (blocks.size() == 1 && blocks.get(0).length == 0)
                    blocks.clear();
                if (onRows) {
                    if (rows == null)
                        throw new IllegalArgumentException("height "
                            + "unspecified (" + lineNo + ")");
                    if (rows.size() == height)
                        throw new IllegalArgumentException("too much "
                            + "row data (" + lineNo + ")");
                    rows.add(Clue.of(blocks));
                } else {
                    if (columns == null)
                        throw new IllegalArgumentException("width "
                            + "unspecified (" + lineNo + ")");
                    if (columns.size() == width)
                        throw new IllegalArgumentException("too much "
                            + "column data (" + lineNo + ")");
                    columns.add(Clue.of(blocks));
                }
            } else {
                throw new IllegalArgumentException("clue too soon: (" + lineNo
                    + ")");
            }
            int cStart = 0;
            while (cStart < line.length() &&
                Character.isWhitespace(line.charAt(cStart)))
                cStart++;
            int cEnd = cStart;
            while (cEnd < line.length() &&
                Character.isWhitespace(line.charAt(cEnd)))
                cEnd++;
        }
        if (rows == null)
            throw new IllegalArgumentException("height unspecified");
        if (rows.size() < height)
            throw new IllegalArgumentException("insufficient row data (exp="
                + height + "; got=" + rows.size() + ")");
        if (columns == null)
            throw new IllegalArgumentException("width unspecified");
        if (columns.size() < height)
            throw new IllegalArgumentException("insufficient column data (exp="
                + width + "; got=" + columns.size() + ")");

        /* Copy all the parsed data into place. */
        return builder.clues("rows", rows).clues("cols", columns).create();
    }

    private static String parseMetaValue(String rem) {
        rem = rem.trim();
        StringBuilder result = new StringBuilder();
        boolean quoted = false;
        boolean esc = false;
        boolean spaced = false;
        for (int i = 0; i < rem.length(); i++) {
            char c = rem.charAt(i);
            if (esc) {
                result.append(c);
                esc = false;
                spaced = false;
            } else if (c == '\\') {
                esc = true;
                spaced = false;
            } else if (c == '"') {
                quoted = !quoted;
                spaced = false;
            } else if (Character.isWhitespace(c)) {
                if (quoted || !spaced) {
                    result.append(' ');
                    spaced = true;
                }
            }
        }
        String unquoted = result.toString();
        Matcher m = CHAR_ENT_FMT.matcher(unquoted);
        result = new StringBuilder();
        int last = 0;
        while (m.find()) {
            /* Append the part which didn't match. */
            result.append(unquoted.subSequence(last, m.start()));
            last = m.end();

            String hex = m.group(2);
            String dec = m.group(3);
            String name = m.group(4);

            int code = -1;
            if (hex != null) {
                code = Integer.parseInt(hex, 16);
            } else if (dec != null) {
                code = Integer.parseInt(hex);
            } else if (name != null) {
                Integer ref = characterEntities.get(name);
                if (ref != null) code = ref;
            }
            if (code > -1) result.appendCodePoint(code);
        }
        return result.toString();
    }
}
