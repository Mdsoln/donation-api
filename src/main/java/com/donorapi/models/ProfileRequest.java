package com.donorapi.models;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ProfileRequest {
    private String fullname;
    private String email;
    private String phone;
    private LocalDate birthdate;
    private String gender;
    private double height;
    private double weight;
    private MultipartFile profileImage;
}
