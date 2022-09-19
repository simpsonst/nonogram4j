// -*- c-basic-offset: 4; indent-tabs-mode: nil -*-

package uk.ac.lancs.nonogram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;
import uk.ac.lancs.nonogram.util.ReversedList;

public final class TestUtils extends TestCase {
    @Test
    public void testReversedList() {
        List<Integer> list1 = Arrays.asList(10, 20, 30);
        List<Integer> rev1 = new ReversedList<>(list1);

        assertEquals(list1.size(), rev1.size());
        assertEquals(10, rev1.get(2).intValue());
        assertEquals(20, rev1.get(1).intValue());
        assertEquals(30, rev1.get(0).intValue());

        List<Integer> result1 = new ArrayList<>();
        for (int i : rev1)
            result1.add(i);
        Collections.reverse(result1);
        assertEquals(list1, result1);
    }
}
