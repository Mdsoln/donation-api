package com.donorapi.hospital.service;

import com.donorapi.hospital.entity.Appointment;
import org.springframework.context.ApplicationEvent;

public class AppointmentOverdueEvent extends ApplicationEvent {
    public AppointmentOverdueEvent(Appointment appointment) {
        super(appointment);
    }

    public Appointment getAppointment() {
        return (Appointment) getSource();
    }
}