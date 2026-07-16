package com.commercial.activities.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AppUserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AppUser getAppUserSample1() {
        return new AppUser().id(1L).phoneNumber("phoneNumber1").birthPlace("birthPlace1");
    }

    public static AppUser getAppUserSample2() {
        return new AppUser().id(2L).phoneNumber("phoneNumber2").birthPlace("birthPlace2");
    }

    public static AppUser getAppUserRandomSampleGenerator() {
        return new AppUser()
            .id(longCount.incrementAndGet())
            .phoneNumber(UUID.randomUUID().toString())
            .birthPlace(UUID.randomUUID().toString());
    }
}
