package com.donorapi.hospital.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class HospitalAppointment {
    private Integer appointmentId;
    private String donorName;
    private String status;
    private String date;
    private String time;
}
