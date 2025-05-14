package com.donorapi.service;

import com.donorapi.entity.Appointment;
import com.donorapi.models.AppointmentResponse;

import java.util.List;

public interface AppointmentMapper {
    AppointmentResponse convertToResponse(List<Appointment> appointment, int total, int attended, int expired);

}
