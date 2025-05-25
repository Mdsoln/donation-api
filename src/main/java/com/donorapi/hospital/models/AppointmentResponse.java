package com.donorapi.hospital.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AppointmentResponse {
    private int total;
    private int attended;
    private int expired;
    private List<AppointmentDetails> appointments;
}
