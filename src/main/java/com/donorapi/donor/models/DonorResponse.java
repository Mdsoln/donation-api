package com.donorapi.donor.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class DonorResponse {
    private Integer id;
    private String email;
    private String phone;
}
