package com.donorapi.service;

import com.donorapi.entity.Appointment;
import com.donorapi.entity.Hospital;
import com.donorapi.entity.Slot;
import com.donorapi.models.AppointmentResponse;
import com.donorapi.models.HospitalResponse;
import com.donorapi.utilities.DateFormatter;


import org.springframework.stereotype.Component;


@Component
public class AppointmentJsonConverterImpl implements AppointmentMapper{

    @Override
    public AppointmentResponse convertToResponse(Appointment appointment, int total, int attended, int expired) {
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

        return new AppointmentResponse(
                HospitalResponse.builder()
                        .id(hospital.getHospitalId())
                        .hospitalName(hospital.getHospitalName())
                        .hospitalAddress(hospital.getHospitalAddress())
                        .build(),
                date,
                timeRange,
                status,
                total,
                attended,
                expired
        );
    }
}
