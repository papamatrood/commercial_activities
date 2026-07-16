package com.commercial.activities.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CompanySubscriptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static CompanySubscription getCompanySubscriptionSample1() {
        return new CompanySubscription().id(1L);
    }

    public static CompanySubscription getCompanySubscriptionSample2() {
        return new CompanySubscription().id(2L);
    }

    public static CompanySubscription getCompanySubscriptionRandomSampleGenerator() {
        return new CompanySubscription().id(longCount.incrementAndGet());
    }
}
