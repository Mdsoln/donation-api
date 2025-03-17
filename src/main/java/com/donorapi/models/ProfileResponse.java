package com.donorapi.models;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ProfileResponse {
    private String fullName;
    private String email;
    private String phone;
    private LocalDate birthdate;
    private String gender;
    private double height;
    private double weight;
    private String imageName;
}
