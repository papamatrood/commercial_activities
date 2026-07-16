package com.commercial.activities.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProductTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Product getProductSample1() {
        return new Product().id(1L).barcode("barcode1").designation("designation1").initialStock(1).currentStock(1);
    }

    public static Product getProductSample2() {
        return new Product().id(2L).barcode("barcode2").designation("designation2").initialStock(2).currentStock(2);
    }

    public static Product getProductRandomSampleGenerator() {
        return new Product()
            .id(longCount.incrementAndGet())
            .barcode(UUID.randomUUID().toString())
            .designation(UUID.randomUUID().toString())
            .initialStock(intCount.incrementAndGet())
            .currentStock(intCount.incrementAndGet());
    }
}
