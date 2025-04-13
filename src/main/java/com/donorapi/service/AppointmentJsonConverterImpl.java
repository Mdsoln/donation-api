package com.donorapi.service;

import com.donorapi.entity.Appointment;
import com.donorapi.entity.Hospital;
import com.donorapi.entity.Slot;
import com.donorapi.models.AppointmentResponse;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class AppointmentJsonConverterImpl implements AppointmentMapper{
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, d MMM, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public AppointmentResponse convertToResponse(Appointment appointment, int total, int attended, int expired) {
        Slot slot = appointment.getSlot();
        Hospital hospital = slot.getHospital();

        String date = slot.getStartTime().format(DATE_FORMATTER);
        String timeRange = slot.getStartTime().toLocalTime().format(TIME_FORMATTER)
                + " - " + slot.getEndTime().toLocalTime().format(TIME_FORMATTER);
        String status = switch (appointment.getStatus()) {
            case COMPLETED -> "Attended";
            case OVERDUE -> "Expired";
            case PENDING -> "Pending";
            case CANCELLED -> "Cancelled";
            case SCHEDULED -> "Upcoming";
        };

        return new AppointmentResponse(
                hospital.getHospitalName(),
                hospital.getHospitalAddress(),
                date,
                timeRange,
                status,
                total,
                attended,
                expired
        );
    }
}
