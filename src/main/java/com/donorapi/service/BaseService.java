package com.donorapi.service;

import com.donorapi.entity.*;
import com.donorapi.exception.DonationEligibilityException;
import com.donorapi.exception.OverBookingException;
import com.donorapi.jpa.*;
import com.donorapi.exception.EmailExistsException;
import com.donorapi.exception.HospitalFoundException;
import com.donorapi.jwt.service.JwtService;
import com.donorapi.models.*;
import com.donorapi.utilities.DateFormatter;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Log4j2
@RequiredArgsConstructor
public class BaseService {

    private final UserRepository userRepository;
    private final DonorRepository donorRepository;
    private final HospitalRepository hospitalRepository;
    private final SlotsRepository slotsRepository;
    private final AppointmentRepository appointmentRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AppointmentJsonConverterImpl converter;
    private final DonationRepository donationRepository;


    public ResponseEntity<Map<String, String>> registerDonor(DonorRegistrationRequest donorRequest) {

        donorRepository.findByEmail(donorRequest.getEmail()).ifPresent(donor -> {
            throw new EmailExistsException("EMAIL_ALREADY_EXISTS", "A donor with this email already exists.");
        });
        Users user = new Users();
        user.setUsername(donorRequest.getUsername());
        user.setRoles(UserRoles.DONOR);
        user.setPassword(passwordEncoder.encode(donorRequest.getPassword()));
        userRepository.save(user);

        Donor donor = new Donor();
        donor.setUser(user);
        donor.setFullName(donorRequest.getFullName());
        donor.setEmail(donorRequest.getEmail());
        donor.setPhone(donorRequest.getPhone());
        donorRepository.save(donor);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Donor registered successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public ResponseEntity<String> registerHospital(HospitalRegistrationRequest hospitalRequest) {

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
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public ResponseEntity<AuthResponse> authenticateUser(AuthRequest request){
        return userRepository.findByUsername(request.getUsername())
                .map(user -> {
                    log.debug("start.............................authenticating");
                    if (!passwordEncoder.matches(request.getPassword(),user.getPassword())) {
                        throw new UsernameNotFoundException("Invalid username or password");
                    }

                    final Donor donor = donorRepository.findByUser(user).orElse(null);
                    assert donor != null;
                    log.debug("Donor fetched successfully..................");
                    final String formattedFirstName = extractFirstName(donor.getFullName());
                    final String dob = formateDateOfBirth(donor.getBirthDate());

                    LocalDateTime donationDateTime = donationRepository.findTopByDonorIdOrderByDonationDateDesc(donor.getDonorId());
                    LocalDate lastDonationDate = donationDateTime != null ? donationDateTime.toLocalDate() : null;
                    String lastDonation;
                    if (lastDonationDate != null) {
                        lastDonation = formatLastDonation(lastDonationDate);
                    } else {
                        lastDonation = getMotivationalMessage();
                    }
                    final String picture = donor.getImage();
                    log.debug("image name: {}",picture);
                    final String bloodType = donor.getBloodType();
                    final String mobile = donor.getPhone();
                    final String email = donor.getEmail();
                    final double height = donor.getHeight();
                    final double weight = donor.getWeight();
                    final String gender = donor.getGender();

                    final int donations = donorRepository.countByAppointmentDonor(donor);
                    final Optional<Appointment> upcomingAppointmentOpt = appointmentRepository.findByDonorAndStatusOrderBySlotEndTimeDesc(donor, AppointmentStatus.SCHEDULED);
                    log.debug("upcoming appointment {}", upcomingAppointmentOpt);
                    final AppointmentCard latestAppointment = upcomingAppointmentOpt.map(this::mapToAppointmentCard).orElse(null);
                    log.debug("Appointment {}",latestAppointment);

                    Map<String, Object> extraClaims = new HashMap<>(3);
                    extraClaims.put("userId", donor.getDonorId());
                    extraClaims.put("roles", user.getAuthorities());
                    var token = jwtService.generateToken(extraClaims, user);

                    log.debug("end.......................authentication");
                    AuthResponse response = AuthResponse.builder()
                            .message("Successfully logged in")
                            .token(token)
                            .username(formattedFirstName)
                            .bloodGroup(bloodType)
                            .picture(picture)
                            .donations(donations)
                            .mobile(mobile)
                            .email(email)
                            .dateOfBirth(dob)
                            .height(height)
                            .weight(weight)
                            .gender(gender)
                            .lastDonation(lastDonation)
                            .latestAppointment(latestAppointment)
                            .build();
                    return ResponseEntity.ok(response);
                })
                .orElseThrow(()-> new EntityNotFoundException("User not found"));
    }

    private String formateDateOfBirth(LocalDate birthDate) {
        if (birthDate == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy");
        return birthDate.format(formatter);
    }

    private String formatLastDonation(LocalDate lastDonationDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM, yyyy");
        return lastDonationDate.format(formatter);
    }

    private static final List<String> MOTIVATIONAL_MESSAGES = Arrays.asList(
            "No donations yet. Your first donation can save lives!",
            "Be a hero today. Start your donation journey!",
            "One donation can save up to three lives. Will you be the one?",
            "You are needed more than ever. Give blood. Save lives."
    );

    private String getMotivationalMessage() {
        Random random = new Random();
        return MOTIVATIONAL_MESSAGES.get(random.nextInt(MOTIVATIONAL_MESSAGES.size()));
    }


    public ResponseEntity<ProfileResponse> updateProfile(Integer donorId, ProfileRequest request) throws IOException{
        Donor donor = donorRepository.findByDonorId(donorId)
                .orElseThrow(() -> new EntityNotFoundException("Donor with ID " + donorId + " not found"));

        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            validateImageType(request.getProfileImage());
            String imageName = storeImages(request.getProfileImage());
            donor.setImage(imageName);
        }

        donor.setFullName(request.getFullname());
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
                .imageName(donor.getImage())
                .build();
        return ResponseEntity.ok(response);
    }

    public List<SlotDto> getAvailableSlotsByHospitalId(Long hospitalId) {
        log.debug("start.....................................");
        List<Slot> slots = slotsRepository.findSlotsByHospitalId(hospitalId);
        LocalDateTime now = LocalDateTime.now();
        List<SlotDto> availableSlots = slots.stream()
                .filter(slot -> slot.getStartTime().isAfter(now))
                .map(slot -> {
                    SlotDto dto = convertToDto(slot);
                    if (slot.isFull()) {
                        dto.setAvailableBookings(-1);
                    }
                    return dto;
                }).sorted(Comparator.comparing(SlotDto::getStartTime))
                .collect(Collectors.toList());

        log.debug("end.....................................");
        return availableSlots;
    }

    public String makeAppointment(AppointmentRequest request){
     log.debug("create...................................appointment");
     final Donor donor = donorRepository.findByDonorId(request.getDonorId()).orElseThrow(
             ()-> new EntityNotFoundException("Donor with ID " + request.getDonorId() + " not found")
     );
     validateDonorEligibility(donor);

     final Slot slot = slotsRepository.findById(request.getSlotId()).orElseThrow(
             ()-> new EntityNotFoundException("Slot with ID " + request.getSlotId() + " not found")
     );
     Appointment appointment = getAppointment(request, slot, donor);
     appointmentRepository.save(appointment);
     slot.addBooking();
     slotsRepository.save(slot);

     return "Appointment scheduled successfully";
    }

    public AppointmentResponse getAppointmentHistory(Integer donorId){
        final Donor donor = donorRepository.findByDonorId(donorId).orElseThrow(
                ()-> new EntityNotFoundException("Donor with ID " + donorId + " not found")
        );
        final List<Appointment> appointments = appointmentRepository.findByDonorOrderByAppointmentDateDesc(donor);
        final int total = appointmentRepository.countByDonor(donor);
        final int attended = appointmentRepository.countByDonorAndStatus(donor, AppointmentStatus.COMPLETED);
        final int expired = appointmentRepository.countByDonorAndStatus(donor, AppointmentStatus.OVERDUE);
        return converter.convertToResponse(appointments, total, attended, expired);
    }

    private Appointment getAppointment(AppointmentRequest request, Slot slot, Donor donor) {
        if (slot.getHospital().getHospitalId() != request.getHospitalId()) {
            throw new IllegalStateException("Slot with ID " + request.getSlotId() + " does not belong to hospital ID " + request.getHospitalId());
        }
        if (slot.isFull()) {
            throw new OverBookingException("Slot with ID " + request.getSlotId() + " is already full");
        }

        final LocalDate appointmentDate = slot.getStartTime().toLocalDate();
        Appointment appointment = new Appointment();
        appointment.setDonor(donor);
        appointment.setSlot(slot);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setDescription(AppointmentDescription.APPOINTMENT_DESCRIPTION);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        return appointment;
    }

    private void validateDonorEligibility(Donor donor) {
        log.debug("validate.............donor.............eligibility");
        final List<Appointment> activeAppointments = appointmentRepository.findByDonorAndStatusNot(donor, AppointmentStatus.COMPLETED);
        if (!activeAppointments.isEmpty()) {
            throw new DonationEligibilityException("You already have an active blood donation appointment.");
        }

        log.debug("last..............donation...............check.........");
        final LocalDate today = LocalDate.now();
        final Optional<Appointment> lastDonation = appointmentRepository.findByDonorAndStatusOrderBySlotEndTimeDesc(
                donor, AppointmentStatus.COMPLETED);

        if (lastDonation.isPresent()) {
            log.debug("last donation found: {}", lastDonation.get().getSlot().getEndTime());
            final LocalDate lastDonationDate = lastDonation.get().getSlot().getEndTime().toLocalDate();

            if (lastDonationDate.isAfter(today)) {
                throw new DonationEligibilityException(
                        "You have a scheduled donation on " + lastDonationDate +
                                ". Please complete or cancel it before booking a new appointment.");
            }
            log.debug("three.............Months............donation.............check.........");
            LocalDate threeMonthsAfterDonation = lastDonationDate.plusMonths(3);
            if (today.isBefore(threeMonthsAfterDonation)) {
                Period remainingWait = Period.between(today, threeMonthsAfterDonation);
                throw new DonationEligibilityException(
                        "You must wait 3 months between donations. " +
                                "You can donate again in " + remainingWait.getMonths() + " months and " +
                                remainingWait.getDays() + " days.");
            }
        }

        log.debug("same..........day...........donation...............check.........");
        boolean hasSameDayAppointment = activeAppointments.stream()
                .anyMatch(a -> a.getAppointmentDate().equals(today));
        if (hasSameDayAppointment) {
            throw new DonationEligibilityException("You already have an appointment today");
        }
    }

    private SlotDto convertToDto(Slot slot) {
        return new SlotDto(
                slot.getId(),
                slot.getHospital().getHospitalId(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getMaxCapacity(),
                slot.getMaxCapacity() - slot.getCurrentBookings()
        );
    }

    private void validateImageType(MultipartFile image) {
        String contentType = image.getContentType();
        System.out.println("Uploaded image type: " + contentType);
        if (contentType == null || (!contentType.equals("image/png") &&
                !contentType.equals("image/jpeg"))) {
            throw new IllegalArgumentException("Invalid image format. Only PNG, JPG, and JPEG are allowed.");
        }
    }

    private String storeImages(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();
        assert FilenameUtils.getExtension(originalFilename) != null;
        String fileExtension = FilenameUtils.getExtension(originalFilename).toLowerCase();

        if (!List.of("jpg", "jpeg", "png").contains(fileExtension)) {
            throw new IllegalArgumentException("Invalid file type");
        }

        String imageName = UUID.randomUUID() + "." + fileExtension;
        String UPLOAD_DIR = "uploads";
        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(imageName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return imageName;
    }

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "User";
        String firstName = fullName.split(" ")[0];
        return firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
    }

    private AppointmentCard mapToAppointmentCard(Appointment appointment) {
        LocalDate appointmentDate = appointment.getAppointmentDate();
        int daysToGo = Period.between(LocalDate.now(), appointmentDate).getDays();

        Slot slot = appointment.getSlot();
        Hospital hospital = slot.getHospital();

        return AppointmentCard.builder()
                .hospital(mapToHospitalResponse(hospital))
                .date(DateFormatter.formatDate(slot.getStartTime()))
                .timeRange(DateFormatter.formatTimeRange(slot.getStartTime(), slot.getEndTime()))
                .dayToGo(daysToGo)
                .build();
    }

    private HospitalResponse mapToHospitalResponse(Hospital hospital) {
        return HospitalResponse.builder()
                .id(hospital.getHospitalId())
                .hospitalName(hospital.getHospitalName())
                .hospitalAddress(hospital.getHospitalAddress())
                .build();
    }

}
