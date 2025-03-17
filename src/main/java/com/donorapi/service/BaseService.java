package com.donorapi.service;

import com.donorapi.entity.Donor;
import com.donorapi.entity.Hospital;
import com.donorapi.constants.UserRoles;
import com.donorapi.entity.Users;
import com.donorapi.exception.EmailExistsException;
import com.donorapi.exception.HospitalFoundException;
import com.donorapi.jpa.DonorRepository;
import com.donorapi.jpa.HospitalRepository;
import com.donorapi.jpa.UserRepository;
import com.donorapi.jwt.service.JwtService;
import com.donorapi.models.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;


@Service
@Log4j2
@RequiredArgsConstructor
public class BaseService {

    private final UserRepository userRepository;
    private final DonorRepository donorRepository;
    private final HospitalRepository hospitalRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ResponseEntity<DonorResponse> registerDonor(DonorRegistrationRequest donorRequest) {

        donorRepository.findByEmail(donorRequest.getEmail()).ifPresent(donor -> {
            throw new EmailExistsException("EMAIL_ALREADY_EXISTS", "A donor with this email already exists.");
        });
        Users user = new Users();
        user.setUsername(donorRequest.getEmail());
        user.setRoles(UserRoles.DONOR);
        user.setPassword(passwordEncoder.encode(donorRequest.getPassword()));
        userRepository.save(user);

        Donor donor = new Donor();
        donor.setUser(user);
        donor.setFullName(donorRequest.getFullName());
        donor.setEmail(donorRequest.getEmail());
        donor.setPhone(donorRequest.getPhone());
        donorRepository.save(donor);

        return new ResponseEntity<>(new DonorResponse(
                donor.getDonorId(), donor.getEmail(), donor.getPhone()), HttpStatus.CREATED);
    }

    public ResponseEntity<HospitalResponse> registerHospital(HospitalRegistrationRequest hospitalRequest) {

        hospitalRepository.findByHospitalName(hospitalRequest.getHospitalName()).ifPresent(hospital -> {
            throw new HospitalFoundException("Hospital already exists");
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

    public ResponseEntity<AuthResponse> authenticateUser(AuthRequest request){
        return userRepository.findByUsername(request.getUsername())
                .map(user -> {
                    if (!passwordEncoder.matches(request.getPassword(),user.getPassword())) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new AuthResponse("Invalid credentials", null, null, null, null, null,null));
                    }
                    //Donor
                    Donor donor = donorRepository.findByUser(user).orElse(null);
                    //Hospital
                    Hospital hospital = hospitalRepository.findByUser(user).orElse(null);

                    var token = jwtService.generateToken(user);

                    AuthResponse.AuthResponseBuilder response = AuthResponse.builder()
                            .message("Successfully logged in")
                            .token(token)
                            .username(user.getUsername())
                            .roles(user.getRoles().name());

                    if(donor != null){
                        response.email(donor.getEmail())
                                .phone(donor.getPhone());
                    }else if(hospital != null){
                        response.email(hospital.getHospitalName())
                                .hospital(
                                        HospitalResponse.builder()
                                                .hospitalName(hospital.getHospitalName())
                                                .hospitalAddress(hospital.getHospitalAddress())
                                                .hospitalCity(hospital.getHospitalCity())
                                                .build()
                                );
                    }

                    return ResponseEntity.ok(response.build());
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse("User not found", null, null, null, null, null,null))
                );
    }

    public ResponseEntity<ProfileResponse> updateProfile(Integer donorId, ProfileRequest request) throws IOException{
        Donor donor = donorRepository.findByDonorId(donorId)
                .orElseThrow(() -> new EntityNotFoundException("Donor with ID " + donorId + " not found"));

        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            String imageName = storeImages(request.getProfileImage());
            donor.setImage(imageName);
        }

        donor.setFullName(request.getFullName());
        donor.setPhone(request.getPhone());
        donor.setEmail(request.getEmail());
        donor.setBirthDate(request.getBirthdate());
        donor.setGender(request.getGender());
        donor.setHeight(request.getHeight());
        donor.setWeight(request.getWeight());
        donorRepository.save(donor);

        ProfileResponse response = ProfileResponse.builder()
                .fullName(donor.getFullName())
                .phone(donor.getPhone())
                .email(donor.getEmail())
                .weight(donor.getWeight())
                .height(donor.getHeight())
                .birthdate(donor.getBirthDate())
                .gender(donor.getGender())
                .build();

        return ResponseEntity.ok(response);
    }


    private String storeImages(MultipartFile image) throws IOException {
       if (image == null || image.isEmpty()) {
        throw new IllegalArgumentException("Image file is null or empty");
       }

       String uploadDirectory = "src/main/resources/static";
       String imageName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));

       if (imageName.contains("..")) {
         throw new IllegalArgumentException("Invalid file format");
       }

       Path uploadPath = Paths.get(uploadDirectory);
        if (!Files.exists(uploadPath)) {
          Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(imageName);
          Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

          return imageName;
    }

}
