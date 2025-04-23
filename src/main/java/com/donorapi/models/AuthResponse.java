package com.donorapi.models;

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
    private AppointmentCard latestAppointment;

    public AuthResponse(String s) {
        this.token = s;
    }
}
