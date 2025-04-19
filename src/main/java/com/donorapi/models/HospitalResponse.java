package com.donorapi.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class HospitalResponse {
    private Integer id;
    private String hospitalName;
    private String hospitalAddress;
}
