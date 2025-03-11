package com.donorapi.controller;

import com.donorapi.entity.Donor;
import com.donorapi.models.DonorRegistrationRequest;
import com.donorapi.models.DonorResponse;
import com.donorapi.models.HospitalRegistrationRequest;
import com.donorapi.models.HospitalResponse;
import com.donorapi.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/donorapp")
@RequiredArgsConstructor
@Log4j2
//@CrossOrigin(originPatterns = )
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
}
