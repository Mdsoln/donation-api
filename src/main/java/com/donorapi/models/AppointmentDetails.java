package com.donorapi.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppointmentDetails {
    private String hospitalName;
    private String address;
    private String date;
    private String timeRange;
    private String status;
}
