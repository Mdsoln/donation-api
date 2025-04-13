package com.donorapi.service;

import com.donorapi.jpa.AppointmentRepository;
import com.donorapi.models.AppointmentStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AppointmentScheduler {
    private final AppointmentRepository appointmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedRate = 30_000) // 30 seconds fallback
    @Transactional
    public void catchMissedEvents() {
        appointmentRepository
                .findByStatusAndSlotEndTimeBefore(
                        AppointmentStatus.SCHEDULED,
                        LocalDateTime.now()
                )
                .forEach(app -> {
                    app.markAsOverdue();
                    appointmentRepository.save(app);
                    eventPublisher.publishEvent(new AppointmentOverdueEvent(app));
                });
    }
}
