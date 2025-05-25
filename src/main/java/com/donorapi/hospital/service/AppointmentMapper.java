package com.donorapi.hospital.service;

import com.donorapi.hospital.entity.Appointment;
import com.donorapi.hospital.models.AppointmentResponse;

import java.util.List;

public interface AppointmentMapper {
    AppointmentResponse convertToResponse(List<Appointment> appointment, int total, int attended, int expired);

}
