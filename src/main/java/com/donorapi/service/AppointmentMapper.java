package com.donorapi.service;

import com.donorapi.entity.Appointment;
import com.donorapi.models.AppointmentResponse;

public interface AppointmentMapper {
    AppointmentResponse convertToResponse(Appointment appointment);
}
