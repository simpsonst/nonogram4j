// -*- c-basic-offset: 4; indent-tabs-mode: nil -*-

package uk.ac.lancs.nonogram.line.fast;

import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;
import uk.ac.lancs.nonogram.clue.Block;
import uk.ac.lancs.nonogram.clue.CellSequence;
import uk.ac.lancs.nonogram.clue.Colors;
import uk.ac.lancs.nonogram.line.LineCandidate;
import static uk.ac.lancs.nonogram.line.LineCandidate.createClue;
import static uk.ac.lancs.nonogram.line.LineCandidate.createLine;
import static uk.ac.lancs.nonogram.line.fast.FastLineAlgorithm.push;

public final class TestLine extends TestCase {
    private static void parseGenerate(String expected, String source) {
        assertEquals(expected, LineCandidate.cellsToString(createLine(source)));
    }

    private static void testPush(CellSequence cells, List<Block> clue,
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
        {
            final CellSequence cells0 = createLine(" -#");
            assertEquals(3, cells0.size());
            assertTrue(Colors.has(cells0.get(0), 0));
            assertTrue(Colors.has(cells0.get(0), 1));
            assertTrue(Colors.has(cells0.get(1), 0));
            assertFalse(Colors.has(cells0.get(1), 1));
            assertFalse(Colors.has(cells0.get(2), 0));
            assertTrue(Colors.has(cells0.get(2), 1));
        }

        parseGenerate("000123000321000", "---RGB---BGR---");
        parseGenerate("000123   321000", "---RGB   BGR---");

        assertEquals(3, createClue("1,2,3").size());
        {
            final List<Block> clue0 = createClue("1,2R,3");
            assertEquals(3, clue0.size());
            assertEquals(1, clue0.get(0).color);
            assertEquals(2, clue0.get(1).color);
            assertEquals(1, clue0.get(2).color);
        }
        {
            final List<Block> clue0 = createClue("4,1R,3");
            assertEquals(4, clue0.get(0).length);
            assertEquals(1, clue0.get(1).length);
            assertEquals(3, clue0.get(2).length);
        }

        CellSequence cells1 = createLine("----           ----");
        CellSequence cells2 = createLine("----           --#-");
        CellSequence cells3 = createLine("---- #      R  ----", 3);

        List<Block> clue1 = createClue("6");
        List<Block> clue2 = createClue("6,4");
        List<Block> clue3 = createClue("6,5");
        List<Block> clue4 = createClue("6,4,1");
        List<Block> clue5 = createClue("6,2,1");
        List<Block> clue6 = createClue("6,2R,1");
        List<Block> clue7 = createClue("6,2R,1R,1");

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
