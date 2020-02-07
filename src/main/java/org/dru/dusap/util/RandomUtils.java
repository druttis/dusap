package org.dru.dusap.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public final class RandomUtils {
    public static int nextWeightedIndex(final Random random, final Collection<Tuple<?, Number>> available) {
        final double total = available.stream().mapToDouble(t -> t.getRight().doubleValue()).sum();
        double accumelator = random.nextDouble() * total;
        int index = 0;
        final Iterator<Tuple<?, Number>> it = available.iterator();
        while (it.hasNext()) {
            accumelator -= it.next().getRight().doubleValue();
            if (accumelator <= 0.0) {
                break;
            }
            index++;
        }
        return index;
    }

    private RandomUtils() {
    }
}
