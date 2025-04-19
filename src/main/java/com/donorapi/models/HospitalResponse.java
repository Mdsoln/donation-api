package com.donorapi.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class HospitalResponse {
    private Long id;
    private String hospitalName;
    private String hospitalAddress;
}
