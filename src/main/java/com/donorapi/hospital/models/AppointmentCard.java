package com.donorapi.hospital.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class AppointmentCard {
    private HospitalResponse hospital;
    private String date;
    private String timeRange;
    private int dayToGo;
}
