package com.donorapi.hospital.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appointments {
    private Long appointmentId;
    private String donorName;
    private String date;
    private String time;
    private String status;
    private String bloodType;
    private Contact contact;
}
