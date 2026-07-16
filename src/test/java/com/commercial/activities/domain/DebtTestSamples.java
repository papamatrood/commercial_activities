package com.commercial.activities.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class DebtTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Debt getDebtSample1() {
        return new Debt().id(1L);
    }

    public static Debt getDebtSample2() {
        return new Debt().id(2L);
    }

    public static Debt getDebtRandomSampleGenerator() {
        return new Debt().id(longCount.incrementAndGet());
    }
}
