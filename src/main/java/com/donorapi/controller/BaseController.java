package com.donorapi.controller;

import com.donorapi.models.*;
import com.donorapi.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/donorapp")
@RequiredArgsConstructor
@Log4j2
public class BaseController {

    private final BaseService baseService;

    @PostMapping("/register-donor")
    public ResponseEntity<DonorResponse> registerDonor(@RequestBody DonorRegistrationRequest donorRequest) {
       return baseService.registerDonor(donorRequest);
    }

    @PostMapping("/register-hospital")
    public ResponseEntity<HospitalResponse> registerHospital(@RequestBody HospitalRegistrationRequest hospitalRequest) {
        return baseService.registerHospital(hospitalRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        if (authRequest == null || authRequest.getUsername() == null || authRequest.getUsername().trim().isEmpty()
                || authRequest.getPassword() == null || authRequest.getPassword().trim().isEmpty()) {
               throw new NullPointerException("Username and password are required");
        }
        return baseService.authenticateUser(authRequest);
    }

}
