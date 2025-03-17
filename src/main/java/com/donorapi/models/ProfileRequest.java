package com.donorapi.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ProfileRequest {
    private String fullName;
    private String email;
    private String phone;
    private LocalDate birthdate;
    private String gender;
    private double height;
    private double weight;
    private MultipartFile profileImage;
}
