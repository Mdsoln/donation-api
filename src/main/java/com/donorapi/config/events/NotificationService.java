package com.donorapi.config.events;

import com.donorapi.hospital.entity.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final WebSocketService webSocketService;

    public void sendOverdueNotification(Appointment appointment) {
        // In-app notification
        webSocketService.sendNotification(
                appointment.getDonor().getDonorId(),
                "Your appointment at " + appointment.getSlot().getHospital().getHospitalName() +
                        " is now overdue. Please reschedule."
        );
    }
}
