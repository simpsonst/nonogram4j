// -*- c-basic-offset: 4; indent-tabs-mode: nil -*-

package uk.ac.lancs.nonogram.line;

import uk.ac.lancs.nonogram.IndexedBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineCandidate {
    public final List<BitSet> complete;

    public final List<BitSet> broken;

    public final List<IndexedBlock> clue;

    private LineCandidate(List<BitSet> complete, List<BitSet> broken,
                          List<IndexedBlock> clue) {
        this.complete = complete;
        this.broken = broken;
        this.clue = clue;
    }

    public static List<IndexedBlock> createMonochromeClue(int... lengths) {
        List<IndexedBlock> result = new ArrayList<>(lengths.length);
        for (int len : lengths)
            result.add(IndexedBlock.of(len));
        return Collections.unmodifiableList(result);
    }

    public static List<IndexedBlock> createClue(int... data) {
        List<IndexedBlock> result = new ArrayList<>((data.length + 1) / 2);
        for (int i = 0; i < data.length; i += 2)
            result.add(IndexedBlock.of(data[i], data[i + 1]));
        return Collections.unmodifiableList(result);
    }

    private static final Pattern BLOCK_SEPARATOR = Pattern.compile("\\s*,\\s*");

    private static final Pattern BLOCK_DESCRIPTOR =
        Pattern.compile("([0-9]+)(.?)");

    public static List<IndexedBlock> createClue(String source) {
        String[] parts = BLOCK_SEPARATOR.split(source);
        List<IndexedBlock> result = new ArrayList<>(parts.length);
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
            final IndexedBlock block = IndexedBlock.of(length, color);
            result.add(block);
        }
        return Collections.unmodifiableList(result);
    }

    public static List<BitSet> createLine(Random rng, final int colours,
                                          final int length) {
        BitSet[] complete = new BitSet[length];
        for (int i = 0; i < length; i++) {
            BitSet cell = new BitSet(colours);
            cell.set(rng.nextInt(colours));
            complete[i] = cell;
        }
        return Arrays.asList(complete);
    }

    public static List<BitSet> createLine(String source) {
        return createLine(source, 2);
    }

    public static List<BitSet> createLine(String source, final int colours) {
        final int length = source.length();
        BitSet[] complete = new BitSet[length];
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
            BitSet cell = new BitSet(colours);
            if (c == '-') {
                cell.set(0);
            } else if (c == ' ') {
                cell.set(0, colours);
            } else {
                int col = map.get(c);
                cell.set(col);
            }
            complete[i] = cell;
        }
        return Arrays.asList(complete);
    }

    public static List<IndexedBlock> createClue(List<? extends BitSet> cells) {
        List<IndexedBlock> result = new ArrayList<>();
        int color = -1;
        int start = 0;
        int pos = 0;
        for (BitSet cell : cells) {
            if (cell.cardinality() != 1)
                throw new IllegalArgumentException("Undetermined colour at "
                    + pos);
            int newCol = cell.nextSetBit(0);
            if (newCol != color) {
                if (color > 0) result.add(IndexedBlock.of(pos - start, color));
                start = pos;
                color = newCol;
            }
            pos++;
        }
        if (color > 0) result.add(IndexedBlock.of(pos - start, color));
        return result;
    }

    public static List<BitSet> copyLine(List<? extends BitSet> in) {
        List<BitSet> out = new ArrayList<>(in.size());
        for (BitSet inc : in) {
            BitSet outc = (BitSet) inc.clone();
            out.add(outc);
        }
        return out;
    }

    public static List<BitSet> breakLine(Random rng, List<BitSet> in) {
        final int length = in.size();
        boolean strip = rng.nextBoolean();
        int pos = 0;
        while (pos < length) {
            final int rem = length - pos;
            int skip = rng.nextInt(rng.nextInt(rem)) + 1;
            if (strip) {
                for (BitSet cell : in.subList(pos, pos + skip))
                    cell.clear();
            }
            pos += skip;
            assert pos <= length;
        }
        return in;
    }

    public static List<BitSet> breakLine(String pattern, List<BitSet> in) {
        int pos = 0;
        for (BitSet cell : in) {
            if (pattern.charAt(pos) == ' ') cell.clear();
            pos++;
        }
        return in;
    }

    public static LineCandidate createCandidate(Random rng, final int colours,
                                                final int length) {
        List<BitSet> complete = createLine(rng, colours, length);
        List<IndexedBlock> clue = createClue(complete);
        List<BitSet> broken = breakLine(rng, copyLine(complete));

        return new LineCandidate(Collections.unmodifiableList(complete),
                                 Collections.unmodifiableList(broken),
                                 Collections.unmodifiableList(clue));
    }

    public static LineCandidate createCandidate(Random rng, String source) {
        List<BitSet> complete = createLine(source);
        List<IndexedBlock> clue = createClue(complete);
        List<BitSet> broken = breakLine(rng, copyLine(complete));

        return new LineCandidate(Collections.unmodifiableList(complete),
                                 Collections.unmodifiableList(broken),
                                 Collections.unmodifiableList(clue));
    }

    public static LineCandidate createCandidate(String source, String pattern) {
        List<BitSet> complete = createLine(source);
        List<IndexedBlock> clue = createClue(complete);
        List<BitSet> broken = breakLine(pattern, copyLine(complete));

        return new LineCandidate(Collections.unmodifiableList(complete),
                                 Collections.unmodifiableList(broken),
                                 Collections.unmodifiableList(clue));
    }

    @SuppressWarnings("unused")
    private static final String COLOURS =
        "-#123456789ABCDEDFGHIJKLMNOPQRSTUVWXYZ";

    public static String clueToString(List<? extends IndexedBlock> clue) {
        StringBuilder out = new StringBuilder();
        String sep = "";
        for (IndexedBlock block : clue) {
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

    public static String cellsToString(List<? extends BitSet> in) {
        StringBuilder out = new StringBuilder(in.size());
        for (BitSet cell : in) {
            switch (cell.cardinality()) {
            case 0:
                out.append('!');
                break;

            case 1:
                out.append(cell.nextSetBit(0));
                break;

            default:
                out.append(' ');
                break;
            }
        }
        return out.toString();
    }
}
