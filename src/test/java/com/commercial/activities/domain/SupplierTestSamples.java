package com.commercial.activities.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SupplierTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Supplier getSupplierSample1() {
        return new Supplier().id(1L).name("name1").contact("contact1");
    }

    public static Supplier getSupplierSample2() {
        return new Supplier().id(2L).name("name2").contact("contact2");
    }

    public static Supplier getSupplierRandomSampleGenerator() {
        return new Supplier().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).contact(UUID.randomUUID().toString());
    }
}
