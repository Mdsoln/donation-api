package com.donorapi.config;

import com.donorapi.hospital.entity.Appointment;
import com.donorapi.hospital.jpa.AppointmentRepository;
import com.donorapi.hospital.service.AppointmentOverdueEvent;
import com.donorapi.config.events.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AppointmentOverdueHandler {
    private final NotificationService notificationService;
    private final AppointmentRepository repository;

    @EventListener
    @Async
    @Transactional
    public void handleOverdue(AppointmentOverdueEvent event) {
        Appointment appointment = event.getAppointment();
        if (!appointment.isNotificationSent()) {
            notificationService.sendOverdueNotification(appointment);
            appointment.setNotificationSent(true);
            repository.save(appointment);
        }
    }

}
