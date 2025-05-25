package com.donorapi.hospital.models;

import com.donorapi.models.UserRegistrationRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class HospitalRegistrationRequest extends UserRegistrationRequest {
    private String hospitalName;
    private String hospitalAddress;
    private String hospitalCity;
    private Double latitude;
    private Double longitude;
}
