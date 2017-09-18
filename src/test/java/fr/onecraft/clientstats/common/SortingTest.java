package fr.onecraft.clientstats.common;

import fr.onecraft.core.tuple.Pair;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

public class SortingTest {

    @Test
    public void treeMap() {

        Integer[] protocolOrder = new Integer[]{47, 107, 108, 109, 110, 210};

        Map<Integer, Pair<String, Integer>> versions = new TreeMap<>();
        versions.put(108, Pair.of("1.9.1", 14));
        versions.put(47, Pair.of("1.8 - 1.8.9", 210));
        versions.put(210, Pair.of("1.10 - 1.10.2", 210));
        versions.put(107, Pair.of("1.9", 79));
        versions.put(110, Pair.of("1.9.3, 1.9.4", 478));
        versions.put(109, Pair.of("1.9.2", 56));

//        int total = 0;
//        for (Pair<String, Integer> pair : versions.values()) total += pair.getRight();

        int i = 0;
        for (Map.Entry<Integer, Pair<String, Integer>> entry : versions.entrySet()) {
            assertEquals(protocolOrder[i++], entry.getKey());
//            int percent = Math.round(entry.getValue().getRight() * 100F / total);
        }

    }

}
