package com.commercial.activities;

import com.commercial.activities.config.AsyncSyncConfiguration;
import com.commercial.activities.config.DatabaseTestcontainer;
import com.commercial.activities.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        CommercialActivitiesApp.class,
        JacksonConfiguration.class,
        AsyncSyncConfiguration.class,
        com.commercial.activities.config.JacksonHibernateConfiguration.class,
    }
)
@ImportTestcontainers(DatabaseTestcontainer.class)
public @interface IntegrationTest {}
