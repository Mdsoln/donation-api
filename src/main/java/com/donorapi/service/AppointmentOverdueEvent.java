package com.donorapi.service;

import com.donorapi.entity.Appointment;
import org.springframework.context.ApplicationEvent;

public class AppointmentOverdueEvent extends ApplicationEvent {
    public AppointmentOverdueEvent(Appointment appointment) {
        super(appointment);
    }

    public Appointment getAppointment() {
        return (Appointment) getSource();
    }
}