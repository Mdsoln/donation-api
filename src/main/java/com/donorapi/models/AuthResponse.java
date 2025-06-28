package com.donorapi.models;

import com.donorapi.hospital.models.AppointmentCard;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class AuthResponse {
    private String message;
    private String token;
    private String username;
    private String bloodGroup;
    private int donations;
    private String picture;
    private String gender;
    private String dateOfBirth;
    private String mobile;
    private String email;
    private double height;
    private double weight;
    private String ageGroup;
    private String lastDonation;
    private AppointmentCard latestAppointment;

    public AuthResponse(String s) {
        this.token = s;
    }
}
