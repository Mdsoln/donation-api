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
    private String roles;
    private String email;
    private String phone;
    private HospitalResponse hospital;
}
