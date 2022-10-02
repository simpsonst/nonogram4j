// -*- c-basic-offset: 4; indent-tabs-mode: nil -*-

package uk.ac.lancs.nonogram.line;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.ac.lancs.nonogram.clue.ArrayCellSequence;
import uk.ac.lancs.nonogram.clue.Block;
import uk.ac.lancs.nonogram.clue.CellIterator;
import uk.ac.lancs.nonogram.clue.CellSequence;
import uk.ac.lancs.nonogram.clue.Colors;

public class LineCandidate {
    public final CellSequence complete;

    public final CellSequence broken;

    public final List<Block> clue;

    private LineCandidate(CellSequence complete, CellSequence broken,
                          List<Block> clue) {
        this.complete = complete;
        this.broken = broken;
        this.clue = clue;
    }

    public static List<Block> createMonochromeClue(int... lengths) {
        List<Block> result = new ArrayList<>(lengths.length);
        for (int len : lengths)
            result.add(Block.of(len));
        return Collections.unmodifiableList(result);
    }

    public static List<Block> createClue(int... data) {
        List<Block> result = new ArrayList<>((data.length + 1) / 2);
        for (int i = 0; i < data.length; i += 2)
            result.add(Block.of(data[i], data[i + 1]));
        return Collections.unmodifiableList(result);
    }

    private static final Pattern BLOCK_SEPARATOR = Pattern.compile("\\s*,\\s*");

    private static final Pattern BLOCK_DESCRIPTOR =
        Pattern.compile("([0-9]+)(.?)");

    public static List<Block> createClue(String source) {
        String[] parts = BLOCK_SEPARATOR.split(source);
        List<Block> result = new ArrayList<>(parts.length);
        Map<String, Integer> colors = new HashMap<>();
        for (String part : parts) {
            Matcher m = BLOCK_DESCRIPTOR.matcher(part);
            if (!m.matches()) throw new IllegalArgumentException(part);
            final int color;
            final String code = m.group(2);
            if (colors.containsKey(code)) {
                color = colors.get(code);
            } else {
                color = colors.size() + 1;
                colors.put(code, color);
            }
            final int length = Integer.parseInt(m.group(1));
            final Block block = Block.of(length, color);
            result.add(block);
        }
        return Collections.unmodifiableList(result);
    }

    public static CellSequence createLine(Random rng, final int colours,
                                          final int length) {
        long[] complete = new long[length];
        for (int i = 0; i < length; i++)
            complete[i] |= Colors.of(rng.nextInt(colours));
        return new ArrayCellSequence(complete);
    }

    public static CellSequence createLine(String source) {
        return createLine(source, 2);
    }

    public static CellSequence createLine(String source, final int colours) {
        final int length = source.length();
        long[] complete = new long[length];
        Collection<Character> usedChars = new LinkedHashSet<>();
        for (int i = 0; i < length; i++) {
            char c = source.charAt(i);
            if (c != '-' && c != ' ') usedChars.add(c);
        }
        Map<Character, Integer> map = new HashMap<>();
        {
            int index = 1;
            for (char c : usedChars)
                map.put(c, index++);
        }
        for (int i = 0; i < length; i++) {
            char c = source.charAt(i);
            long cell = 0;
            switch (c) {
            case '-':
                cell |= Colors.of(0);
                break;

            case ' ':
                cell |= Colors.all(colours);
                break;

            default:
                int col = map.get(c);
                cell |= Colors.of(col);
                break;
            }
            complete[i] = cell;
        }
        return new ArrayCellSequence(complete);
    }

    public static List<Block> createClue(CellSequence cells) {
        List<Block> result = new ArrayList<>();
        int color = -1;
        int start = 0;
        int pos = 0;
        for (CellIterator iter = cells.iterator(); iter.more(); iter.next()) {
            final int newCol = iter.color();
            switch (newCol) {
            case Colors.INCONSISTENT_COLOR:
            case Colors.INDETERMINATE_COLOR:
                throw new IllegalArgumentException("Undetermined colour at "
                    + pos);

            default:
                if (newCol != color) {
                    if (color > 0) result.add(Block.of(pos - start, color));
                    start = pos;
                    color = newCol;
                }
                pos++;
                break;
            }
        }
        if (color > 0) result.add(Block.of(pos - start, color));
        return result;
    }

    public static CellSequence copyLine(CellSequence in) {
        long[] base = new long[in.size()];
        for (int i = 0; i < in.size(); i++)
            base[i] = in.get(i);
        return new ArrayCellSequence(base);
    }

    public static CellSequence breakLine(Random rng, CellSequence in) {
        final int length = in.size();
        boolean strip = rng.nextBoolean();
        int pos = 0;
        while (pos < length) {
            final int rem = length - pos;
            int skip = rng.nextInt(rng.nextInt(rem)) + 1;
            if (strip) {
                while (skip-- > 0)
                    in.put(pos++, 0);
            } else {
                pos += skip;
            }
            strip = !strip;
            assert pos <= length;
        }
        return in;
    }

    public static CellSequence breakLine(String pattern, CellSequence in) {
        for (int pos = 0; pos < in.size(); pos++) {
            if (pattern.charAt(pos) == ' ') in.put(pos, 0);
            pos++;
        }
        return in;
    }

    public static LineCandidate createCandidate(Random rng, final int colours,
                                                final int length) {
        CellSequence complete = createLine(rng, colours, length);
        List<Block> clue = createClue(complete);
        CellSequence broken = breakLine(rng, copyLine(complete));

        return new LineCandidate(complete.readOnly(), broken.readOnly(),
                                 Collections.unmodifiableList(clue));
    }

    public static LineCandidate createCandidate(Random rng, String source) {
        CellSequence complete = createLine(source);
        List<Block> clue = createClue(complete);
        CellSequence broken = breakLine(rng, copyLine(complete));

        return new LineCandidate(complete.readOnly(), broken.readOnly(),
                                 Collections.unmodifiableList(clue));
    }

    public static LineCandidate createCandidate(String source, String pattern) {
        CellSequence complete = createLine(source);
        List<Block> clue = createClue(complete);
        CellSequence broken = breakLine(pattern, copyLine(complete));

        return new LineCandidate(complete.readOnly(), broken.readOnly(),
                                 Collections.unmodifiableList(clue));
    }

    @SuppressWarnings("unused")
    private static final String COLOURS =
        "-#123456789ABCDEDFGHIJKLMNOPQRSTUVWXYZ";

    public static String clueToString(List<? extends Block> clue) {
        StringBuilder out = new StringBuilder();
        String sep = "";
        for (Block block : clue) {
            out.append(sep);
            sep = ",";
            out.append(block.length);
            if (block.color != 1) {
                out.append(':');
                out.append(block.color);
            }
        }
        return out.toString();
    }

    public static String cellsToString(CellSequence in) {
        StringBuilder out = new StringBuilder(in.size());
        for (CellIterator iter = in.iterator(); iter.more(); iter.next()) {
            int color = iter.color();
            switch (color) {
            case Colors.INCONSISTENT_COLOR:
                out.append('!');
                break;

            case Colors.INDETERMINATE_COLOR:
                out.append(' ');
                break;

            default:
                out.append(color);
                break;
            }
        }
        return out.toString();
    }
}
