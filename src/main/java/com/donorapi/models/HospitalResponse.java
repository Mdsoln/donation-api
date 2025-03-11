package com.donorapi.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class HospitalResponse {
    private Integer id;
    private String hospitalName;
    private String hospitalAddress;
    private String hospitalCity;
}
