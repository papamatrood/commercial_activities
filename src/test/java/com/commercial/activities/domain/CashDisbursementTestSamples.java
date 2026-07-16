package com.commercial.activities.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CashDisbursementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static CashDisbursement getCashDisbursementSample1() {
        return new CashDisbursement().id(1L).reason("reason1");
    }

    public static CashDisbursement getCashDisbursementSample2() {
        return new CashDisbursement().id(2L).reason("reason2");
    }

    public static CashDisbursement getCashDisbursementRandomSampleGenerator() {
        return new CashDisbursement().id(longCount.incrementAndGet()).reason(UUID.randomUUID().toString());
    }
}
