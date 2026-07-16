package com.commercial.activities.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CashCollectionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static CashCollection getCashCollectionSample1() {
        return new CashCollection().id(1L);
    }

    public static CashCollection getCashCollectionSample2() {
        return new CashCollection().id(2L);
    }

    public static CashCollection getCashCollectionRandomSampleGenerator() {
        return new CashCollection().id(longCount.incrementAndGet());
    }
}
