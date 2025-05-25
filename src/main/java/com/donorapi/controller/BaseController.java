package com.donorapi.controller;

import com.donorapi.donor.models.DonorRegistrationRequest;
import com.donorapi.donor.models.ProfileRequest;
import com.donorapi.donor.models.ProfileResponse;
import com.donorapi.hospital.entity.Hospital;
import com.donorapi.hospital.jpa.HospitalRepository;
import com.donorapi.hospital.models.AppointmentRequest;
import com.donorapi.hospital.models.AppointmentResponse;
import com.donorapi.hospital.models.SlotDto;
import com.donorapi.models.*;
import com.donorapi.service.BaseService;
import com.donorapi.service.LocationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(path = "/api/v1/donor")
@RequiredArgsConstructor
@Log4j2
@Validated
public class BaseController {

    private final BaseService baseService;
    private final LocationService locationService;
    private final HospitalRepository hospitalRepository;


    @PostMapping("/register-donor")
    public ResponseEntity<Map<String, String>> registerDonor(@RequestBody DonorRegistrationRequest donorRequest) {
       return baseService.registerDonor(donorRequest);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        if (authRequest == null || authRequest.getUsername() == null || authRequest.getUsername().trim().isEmpty()
                || authRequest.getPassword() == null || authRequest.getPassword().trim().isEmpty()) {
               throw new NullPointerException("Username and password are required");
        }
        return baseService.authenticateUser(authRequest);
    }


    @PostMapping("/{donor-id}/update-profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @PathVariable("donor-id") Integer parameter, @RequestParam(name = "fullname") String fullname, @RequestParam(name = "email") String email,
            @RequestParam(name = "phone") String phone, @RequestParam(name = "gender") String gender, @RequestParam(name = "birthdate") LocalDate birthdate,
            @RequestParam(name = "height") double height, @RequestParam(name = "weight") double weight, @RequestParam(name = "profileImage", required = false) MultipartFile profileImage
    ) throws IOException {

        if (birthdate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birthdate cannot be a future date");
        }

        ProfileRequest request = ProfileRequest.builder()
                .fullname(fullname)
                .email(email)
                .phone(phone)
                .gender(gender)
                .birthdate(birthdate)
                .height(height)
                .weight(weight)
                .profileImage(profileImage)
                .build();
        log.debug("Received payload: {}",request);
        return baseService.updateProfile(parameter, request);

    }


    @GetMapping("/hospitals/nearby")
    public ResponseEntity<List<Hospital>> nearbyHospitals(@RequestParam double userLat, @RequestParam double userLon, @RequestParam(defaultValue = "10") double radiusKm){
        log.info("Fetching hospitals for lat={}, lon={}, radius={}", userLat, userLon, radiusKm);
        List<Hospital> allHospitals = hospitalRepository.findAllWithCoordinates();
        log.info(allHospitals);
        List<Hospital> nearbyHospitals = allHospitals.stream()
                .filter(h -> {
                    double distance = locationService.calculateDistance(
                            userLat, userLon,
                            h.getLatitude(), h.getLongitude());
                    return distance <= radiusKm;
                })
                .sorted(Comparator.comparingDouble(h ->
                        locationService.calculateDistance(
                                userLat, userLon,
                                h.getLatitude(), h.getLongitude())))
                .toList();
        log.info(nearbyHospitals);
        List<Hospital> otherHospitals = allHospitals.stream()
                .filter(h -> !nearbyHospitals.contains(h))
                .toList();
        log.info(otherHospitals);
        List<Hospital> result = new ArrayList<>();
        result.addAll(nearbyHospitals);
        result.addAll(otherHospitals);

        return ResponseEntity.ok(result);
    }


    @GetMapping("/hospitals/{hospital-id}/slots")
    public ResponseEntity<List<SlotDto>> getHospitalSlots(@PathVariable("hospital-id") @Min(1) Long hospitalId) {
        log.debug("Hospital id is {}", hospitalId);
        if (!hospitalRepository.existsById(hospitalId)) {
            return ResponseEntity.notFound().build();
        }
        final List<SlotDto> dtos = baseService.getAvailableSlotsByHospitalId(hospitalId);
        return dtos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(dtos);
    }


    @PostMapping("/make-appointment")
    public ResponseEntity<String> appointment(@RequestBody @Valid AppointmentRequest appointmentRequest) {
        final String response = baseService.makeAppointment(appointmentRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/appointments/{donor-id}")
    public ResponseEntity<AppointmentResponse> getAppointments(@PathVariable("donor-id") Integer donorId) {
        final AppointmentResponse response = baseService.getAppointmentHistory(donorId);
        return response.getAppointments()
                .isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }
}
