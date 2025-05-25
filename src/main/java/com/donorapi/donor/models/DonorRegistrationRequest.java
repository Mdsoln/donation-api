package com.donorapi.donor.models;

import com.donorapi.models.UserRegistrationRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DonorRegistrationRequest extends UserRegistrationRequest {
    private String fullName;
    private String email;
    private String phone;
    private String age;
    private String gender;
}
