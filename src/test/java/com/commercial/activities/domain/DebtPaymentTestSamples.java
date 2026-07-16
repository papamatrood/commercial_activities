package com.commercial.activities.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class DebtPaymentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static DebtPayment getDebtPaymentSample1() {
        return new DebtPayment().id(1L);
    }

    public static DebtPayment getDebtPaymentSample2() {
        return new DebtPayment().id(2L);
    }

    public static DebtPayment getDebtPaymentRandomSampleGenerator() {
        return new DebtPayment().id(longCount.incrementAndGet());
    }
}
