// -*- c-basic-offset: 4; indent-tabs-mode: nil -*-

package uk.ac.lancs.nonogram.line.fast;

import java.util.BitSet;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;
import uk.ac.lancs.nonogram.IndexedBlock;
import uk.ac.lancs.nonogram.line.LineCandidate;
import static uk.ac.lancs.nonogram.line.LineCandidate.createClue;
import static uk.ac.lancs.nonogram.line.LineCandidate.createLine;
import static uk.ac.lancs.nonogram.line.fast.FastLineAlgorithm.push;

public final class TestLine extends TestCase {
    private static void parseGenerate(String expected, String source) {
        assertEquals(expected, LineCandidate.cellsToString(createLine(source)));
    }

    private static void testPush(List<BitSet> cells, List<IndexedBlock> clue,
                                 int... expected) {
        String prefix = LineCandidate.cellsToString(cells) + ' '
            + LineCandidate.clueToString(clue);
        int[] pos = new int[clue.size()];
        boolean state = push(pos, cells, clue);
        if (expected.length != pos.length) {
            assertTrue(prefix + " NOFIT", state);
        } else {
            assertFalse(prefix + " FIT", state);
            for (int i = 0; i < pos.length; i++)
                assertEquals(prefix + '[' + i + ']', expected[i], pos[i]);
        }
    }

    @Test
    public void testSomething() {
        assertEquals(3, createLine(" -#").size());
        assertTrue(createLine(" -#").get(0).get(0));
        assertTrue(createLine(" -#").get(0).get(1));
        assertTrue(createLine(" -#").get(1).get(0));
        assertFalse(createLine(" -#").get(1).get(1));
        assertFalse(createLine(" -#").get(2).get(0));
        assertTrue(createLine(" -#").get(2).get(1));

        parseGenerate("000123000321000", "---RGB---BGR---");
        parseGenerate("000123   321000", "---RGB   BGR---");

        assertEquals(3, createClue("1,2,3").size());
        assertEquals(3, createClue("1,2R,3").size());
        assertEquals(1, createClue("1,2R,3").get(0).color);
        assertEquals(2, createClue("1,2R,3").get(1).color);
        assertEquals(1, createClue("1,2R,3").get(2).color);
        assertEquals(4, createClue("4,1R,3").get(0).length);
        assertEquals(1, createClue("4,1R,3").get(1).length);
        assertEquals(3, createClue("4,1R,3").get(2).length);

        List<BitSet> cells1 = createLine("----           ----");
        List<BitSet> cells2 = createLine("----           --#-");
        List<BitSet> cells3 = createLine("---- #      R  ----", 3);

        List<IndexedBlock> clue1 = createClue("6");
        List<IndexedBlock> clue2 = createClue("6,4");
        List<IndexedBlock> clue3 = createClue("6,5");
        List<IndexedBlock> clue4 = createClue("6,4,1");
        List<IndexedBlock> clue5 = createClue("6,2,1");
        List<IndexedBlock> clue6 = createClue("6,2R,1");
        List<IndexedBlock> clue7 = createClue("6,2R,1R,1");

        testPush(cells1, clue1, 4);
        testPush(cells1, clue2, 4, 11);
        testPush(cells1, clue3);
        testPush(cells1, clue4);
        testPush(cells1, clue5, 4, 11, 14);
        testPush(cells2, clue1);
        testPush(cells2, clue2);
        testPush(cells2, clue3);
        testPush(cells2, clue4, 4, 11, 17);
        testPush(cells2, clue5, 4, 11, 17);
        testPush(cells3, clue5);
        testPush(cells3, clue6, 4, 11, 13);
        testPush(cells3, clue7);
    }
}
