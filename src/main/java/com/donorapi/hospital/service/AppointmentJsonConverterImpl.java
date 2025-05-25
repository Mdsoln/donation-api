package com.donorapi.hospital.service;

import com.donorapi.hospital.entity.Appointment;
import com.donorapi.hospital.entity.Hospital;
import com.donorapi.hospital.entity.Slot;
import com.donorapi.hospital.models.AppointmentDetails;
import com.donorapi.hospital.models.AppointmentResponse;
import com.donorapi.utilities.DateFormatter;


import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class AppointmentJsonConverterImpl implements AppointmentMapper {

    @Override
    public AppointmentResponse convertToResponse(List<Appointment> appointments, int total, int attended, int expired) {
        List<AppointmentDetails> detailsList = appointments.stream().map(appointment -> {
            Slot slot = appointment.getSlot();
            Hospital hospital = slot.getHospital();

            String date = DateFormatter.formatDate(slot.getStartTime());
            String timeRange = DateFormatter.formatTimeRange(slot.getStartTime(), slot.getEndTime());
            String status = switch (appointment.getStatus()) {
                case COMPLETED -> "Attended";
                case OVERDUE -> "Expired";
                case PENDING -> "Pending";
                case CANCELLED -> "Cancelled";
                case SCHEDULED -> "Upcoming";
            };

            return new AppointmentDetails(
                    hospital.getHospitalName(),
                    hospital.getHospitalAddress(),
                    date,
                    timeRange,
                    status
            );
        }).toList();

        return new AppointmentResponse(
                total,
                attended,
                expired,
                detailsList
        );
    }
}
