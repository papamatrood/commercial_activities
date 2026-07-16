package com.commercial.activities.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SaleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Sale getSaleSample1() {
        return new Sale().id(1L).customerName("customerName1").customerCompany("customerCompany1").customerContact("customerContact1");
    }

    public static Sale getSaleSample2() {
        return new Sale().id(2L).customerName("customerName2").customerCompany("customerCompany2").customerContact("customerContact2");
    }

    public static Sale getSaleRandomSampleGenerator() {
        return new Sale()
            .id(longCount.incrementAndGet())
            .customerName(UUID.randomUUID().toString())
            .customerCompany(UUID.randomUUID().toString())
            .customerContact(UUID.randomUUID().toString());
    }
}
