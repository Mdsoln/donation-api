package com.donorapi.config.events;

import com.donorapi.hospital.service.AppointmentOverdueEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public void publish(AppointmentOverdueEvent event) {
        eventPublisher.publishEvent(event);
    }
}
