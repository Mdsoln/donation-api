package com.donorapi.controller;

import com.donorapi.entity.Hospital;
import com.donorapi.jpa.HospitalRepository;
import com.donorapi.models.*;
import com.donorapi.service.BaseService;
import com.donorapi.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/donorapp")
@RequiredArgsConstructor
@Log4j2
public class BaseController {

    private final BaseService baseService;
    private final LocationService locationService;
    private final HospitalRepository hospitalRepository;

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


    @PostMapping("/{donor-id}/update-profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @PathVariable("donor-id") Integer parameter, @RequestParam(name = "fullname") String fullname, @RequestParam(name = "email") String email,
            @RequestParam(name = "phone") String phone, @RequestParam(name = "gender") String gender, @RequestParam(name = "birthdate") LocalDate birthdate,
            @RequestParam(name = "height") double height, @RequestParam(name = "weight") double weight, @RequestParam(name = "profileImage") MultipartFile profileImage
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

        return baseService.updateProfile(parameter, request);

    }

    @GetMapping("/hospitals/nearby")
    public ResponseEntity<List<Hospital>> nearbyHospitals(@RequestParam double userLat, @RequestParam double userLon, @RequestParam(defaultValue = "10") double radiusKm){
        List<Hospital> allHospitals = hospitalRepository.findAllWithCoordinates();

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

        List<Hospital> otherHospitals = allHospitals.stream()
                .filter(h -> !nearbyHospitals.contains(h))
                .toList();

        List<Hospital> result = new ArrayList<>();
        result.addAll(nearbyHospitals);
        result.addAll(otherHospitals);

        return ResponseEntity.ok(result);
    }

}
