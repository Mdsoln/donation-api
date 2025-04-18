package com.donorapi.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AppointmentResponse {
    private HospitalResponse hospitalResponse;
    private String date;
    private String timeRange;
    private String status;
    private int totalAppointments;
    private int attendedAppointments;
    private int expiredAppointments;
}
