package com.commercial.activities.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StockArrivalTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static StockArrival getStockArrivalSample1() {
        return new StockArrival().id(1L).barcode("barcode1").quantity(1);
    }

    public static StockArrival getStockArrivalSample2() {
        return new StockArrival().id(2L).barcode("barcode2").quantity(2);
    }

    public static StockArrival getStockArrivalRandomSampleGenerator() {
        return new StockArrival()
            .id(longCount.incrementAndGet())
            .barcode(UUID.randomUUID().toString())
            .quantity(intCount.incrementAndGet());
    }
}
