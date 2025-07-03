package com.donorapi.hospital.controller;

import com.donorapi.donor.models.DonationRequest;
import com.donorapi.hospital.models.*;
import com.donorapi.models.AuthRequest;
import com.donorapi.service.BaseService;
import com.donorapi.hospital.service.HospitalServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/hospital")
@RequiredArgsConstructor
@Log4j2
@Validated
public class HospitalController {

    private final BaseService baseService;
    private final HospitalServiceImpl hospitalService;


    @PostMapping("/register-hospital")
    public ResponseEntity<String> registerHospital(@RequestBody HospitalRegistrationRequest hospitalRequest) {
        return baseService.registerHospital(hospitalRequest);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthHospitalResponse> authenticateHospital(@RequestBody AuthRequest request) {
        if (request == null){
            throw new IllegalArgumentException("Username and password are required");
        }
        return ResponseEntity.ok(hospitalService.authenticateHospital(request));
    }

    @GetMapping("/{hospitalId}/appointments")
    public ResponseEntity<List<Appointments>> getAppointmentsByHospital(@PathVariable("hospitalId") Long hospitalId) {
        if (hospitalId == null) {
            throw new IllegalArgumentException("Hospital id cannot be null");
        }
        final List<Appointments> appointments = hospitalService.getAppointmentsByHospital(hospitalId);
        return ResponseEntity.ok(appointments);
    }

    @PostMapping("/appointments/{appointmentId}/approval")
    public ResponseEntity<String> approveAppointment(@PathVariable Long appointmentId) {
        if (appointmentId == null) {
            throw new IllegalArgumentException("Appointment id cannot be null");
        }
        hospitalService.approveAppointment(appointmentId);
        return ResponseEntity.ok("Successfully approved appointment");
    }


    @GetMapping("/hopsital/{hospitalId}/donors")
    public ResponseEntity<List<HospitalDonors>> getDonorsPerHospital(@PathVariable("hospitalId") Long hospitalId) {
        if (hospitalId == null) {
            throw new IllegalArgumentException("Appointment id cannot be null");
        }
        final List<HospitalDonors> hospitalDonors = hospitalService.findDonorsByHospital(hospitalId);
        return ResponseEntity.ok(hospitalDonors);
    }

    @GetMapping("/hospital/{hospitalId}/topdonors")
    public ResponseEntity<List<HospitalDonors>> getTopDonorsPerHospital(@PathVariable("hospitalId") Long hospitalId) {
        if (hospitalId == null) {
            throw new IllegalArgumentException("Appointment id cannot be null");
        }
        final List<HospitalDonors> topDonors = hospitalService.findTopDonorsByHospital(hospitalId);
        return ResponseEntity.ok(topDonors);
    }


    @GetMapping("/hospital/{hospitalId}/appointments")
    public ResponseEntity<List<HospitalAppointment>> getAppointmentsPerHospital(@PathVariable("hospitalId") Long hospitalId) {
        if (hospitalId == null) {
            throw new IllegalArgumentException("Appointment id cannot be null");
        }
        final List<HospitalAppointment> appointments = hospitalService.findAppointmentsByHospital(hospitalId);
        return ResponseEntity.ok(appointments);
    }

    @PostMapping("/donations")
    public ResponseEntity<String> recordDonation(@RequestBody @Validated DonationRequest request) {
        log.info("Recording donation: {}", request);
        String result = hospitalService.recordDonation(request);
        return ResponseEntity.ok(result);
    }

}
