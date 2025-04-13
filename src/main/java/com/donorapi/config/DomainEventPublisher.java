package com.donorapi.config;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DomainEventPublisher {
    private static ApplicationContext applicationContext;

    public DomainEventPublisher(ApplicationContext applicationContext) {
        DomainEventPublisher.applicationContext = applicationContext;
    }

    public static void publish(Object event) {
        if (applicationContext != null) {
            applicationContext.publishEvent(event);
        }
    }
}
