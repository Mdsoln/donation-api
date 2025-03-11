package com.donorapi.service;

import com.donorapi.entity.Donor;
import com.donorapi.entity.Hospital;
import com.donorapi.entity.UserRoles;
import com.donorapi.entity.Users;
import com.donorapi.exception.EmailExistsException;
import com.donorapi.jpa.DonorRepository;
import com.donorapi.jpa.HospitalRepository;
import com.donorapi.jpa.UserRepository;
import com.donorapi.models.DonorRegistrationRequest;
import com.donorapi.models.DonorResponse;
import com.donorapi.models.HospitalRegistrationRequest;
import com.donorapi.models.HospitalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Log4j2
@RequiredArgsConstructor
public class BaseService {

    private final UserRepository userRepository;
    private final DonorRepository donorRepository;
    private final HospitalRepository hospitalRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<DonorResponse> registerDonor(DonorRegistrationRequest donorRequest) {

        donorRepository.findByEmail(donorRequest.getEmail()).ifPresent(donor -> {
            throw new EmailExistsException("Donor already exists");
        });
        Users user = new Users();
        user.setUsername(donorRequest.getEmail());
        user.setRoles(UserRoles.DONOR);
        user.setPassword(passwordEncoder.encode(donorRequest.getPassword()));
        userRepository.save(user);

        Donor donor = new Donor();
        donor.setUser(user);
        donor.setEmail(donorRequest.getEmail());
        donor.setPhone(donorRequest.getPhone());
        donorRepository.save(donor);

        return new ResponseEntity<>(new DonorResponse(
                donor.getDonorId(), donor.getEmail(), donor.getPhone()), HttpStatus.CREATED);
    }

    public ResponseEntity<HospitalResponse> registerHospital(HospitalRegistrationRequest hospitalRequest) {

        hospitalRepository.findByHospitalName(hospitalRequest.getHospitalName()).ifPresent(hospital -> {
            throw new EmailExistsException("Hospital already exists");
        });
        Users user = new Users();
        user.setUsername(hospitalRequest.getHospitalName());
        user.setRoles(UserRoles.HOSPITAL);
        user.setPassword(passwordEncoder.encode(hospitalRequest.getPassword()));
        userRepository.save(user);

        Hospital hospital = new Hospital();
        hospital.setUser(user);
        hospital.setHospitalName(hospitalRequest.getHospitalName());
        hospital.setHospitalAddress(hospitalRequest.getHospitalAddress());
        hospital.setHospitalCity(hospitalRequest.getHospitalCity());
        hospitalRepository.save(hospital);
        return new ResponseEntity<>(
                new HospitalResponse(hospital.getHospitalId(), hospital.getHospitalName(), hospital.getHospitalAddress(),
                        hospital.getHospitalCity()), HttpStatus.CREATED
        );
    }
}
