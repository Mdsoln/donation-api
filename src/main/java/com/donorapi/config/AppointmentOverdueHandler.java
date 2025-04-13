package com.donorapi.service;

import com.donorapi.entity.Appointment;
import com.donorapi.jpa.AppointmentRepository;
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
