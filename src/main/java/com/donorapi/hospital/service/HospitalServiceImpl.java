package com.donorapi.hospital.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.donorapi.donor.entity.Donation;
import com.donorapi.donor.entity.Users;
import com.donorapi.donor.jpa.DonorRepository;
import com.donorapi.donor.jpa.UserRepository;
import com.donorapi.donor.models.DonationRequest;
import com.donorapi.exception.InvalidAppointmentStatusException;
import com.donorapi.hospital.entity.Hospital;
import com.donorapi.hospital.entity.UrgentRequest;
import com.donorapi.hospital.jpa.HospitalRepository;
import com.donorapi.hospital.jpa.UrgentRequestRepository;
import com.donorapi.hospital.models.*;
import com.donorapi.jwt.service.JwtService;
import com.donorapi.models.AuthRequest;
import com.donorapi.utilities.DateFormatter;
import com.donorapi.utilities.UrgentRequestStatus;
import com.donorapi.utilities.UserRoles;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.donorapi.hospital.entity.Appointment;
import com.donorapi.donor.entity.Donor;
import com.donorapi.hospital.jpa.AppointmentRepository;
import com.donorapi.donor.jpa.DonationRepository;
import com.donorapi.utilities.AppointmentStatus;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class HospitalServiceImpl {
    private final AppointmentRepository appointmentRepository;
    private final DonationRepository donationRepository;
    private final DonorRepository donorRepository;
    private final HospitalRepository hospitalRepository;
    private final AppointmentJsonConverterImpl converter;
    private final UserRepository userRepository;
    private final UrgentRequestRepository urgentRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Transactional
    public void approveAppointment(Long appointmentId){
        Appointment appointment = appointmentRepository.findById(appointmentId)
               .orElseThrow(()-> new EntityNotFoundException("No match appointment with appointment ID provided"));
        log.debug("Approving appointment with ID: {}", appointmentId);
        if (!appointment.hasPendingStatus()) {
            throw new InvalidAppointmentStatusException("Only pending appointments can be approved");
        } 

        final LocalDateTime changed = LocalDateTime.now();
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setStatusChangedAt(changed);
        appointmentRepository.save(appointment);
        log.debug("Appointment status changed to SCHEDULED at {}", changed);
    }

    public List<HospitalDonors> findDonorsByHospital(Long hospitalId) {
        List<Donor> donors = appointmentRepository.findDistinctDonorsByHospitalId(hospitalId);
        if (donors == null || donors.isEmpty()) {
            return Collections.emptyList();
        }

        return donors.stream()
                .map(d -> {
                    int donationCount = donationRepository.countDonationsByDonorAndHospital(d, hospitalId);
                    return new HospitalDonors(
                            d.getDonorId(),
                            d.getFullName(),
                            d.getEmail(),
                            d.getPhone(),
                            d.getBloodType(),
                            donationCount
                    );
                })
                .toList();
    }

    public List<HospitalDonors> findTopDonorsByHospital(Long hospitalId) {
        return donationRepository.findTopDonorsByHospital(hospitalId);
    }

    public List<HospitalAppointment> findAppointmentsByHospital(Long hospitalId){
        List<Appointment> appointments = appointmentRepository.findAppointmentsBySlot_Hospital_HospitalId(hospitalId);
        if (appointments == null || appointments.isEmpty()) {
            return Collections.emptyList();
        }

        return appointments.stream().map(a -> {
            String date = DateFormatter.formatDate(a.getAppointmentDate().atStartOfDay());
            String time = DateFormatter.formatTimeRange(a.getSlot().getStartTime(), a.getSlot().getEndTime());
            return new HospitalAppointment(
                    a.getId().intValue(),
                    a.getDonor().getFullName(),
                    a.getStatus().name(),
                    date,
                    time
            );
        }).toList();
    }

    @Transactional
    public String recordDonation(DonationRequest request) {
        Donor donor = donorRepository.findById(request.donorId())
                .orElseThrow(() -> new EntityNotFoundException("Donor not found with ID: " + request.donorId()));

        Donation donation = new Donation();
        donation.setDonor(donor);
        donation.setVolumeMl(request.volumeMl());
        donation.setBloodType(request.bloodType());
        donation.setNotes(request.notes());
        donation.setDonationDate(LocalDateTime.now());

        // Calculate the next eligibility date (3 months from donation)
        donation.calculateNextEligibility();

        // Handle appointment if provided
        if (request.appointmentId() != null) {
            // Scenario 1: Donation with appointment
            Appointment appointment = appointmentRepository.findById(request.appointmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Appointment not found with ID: " + request.appointmentId()));

            // Update appointment status
            appointment.setBloodDonated(true);
            appointment.setStatus(AppointmentStatus.COMPLETED);
            appointment.setStatusChangedAt(LocalDateTime.now());
            appointmentRepository.save(appointment);

            // Link appointment to donation
            donation.setAppointment(appointment);

            log.info("Recorded donation for appointment ID: {}", request.appointmentId());
        } else {
            // Scenario 2: Direct donation without appointment
            log.info("Recorded direct donation for donor ID: {}", request.donorId());
        }

        // Save the donation
        donationRepository.save(donation);

        return "Donation recorded successfully";
    }

    /**
     * Authenticates a hospital user and returns hospital-specific data.
     *
     * @param request The authentication request containing username and password
     * @return AuthHospitalResponse containing hospital data and authentication token
     * @throws UsernameNotFoundException if the username is not found or password is incorrect
     * @throws EntityNotFoundException if no hospital is associated with the user
     * @throws IllegalArgumentException if the request is invalid
     */
    public AuthHospitalResponse authenticateHospital(AuthRequest request) {
        validateAuthRequest(request);
        log.debug("Attempting to authenticate hospital user: {}", request.getUsername());

        return userRepository.findByUsername(request.getUsername())
                .map(user -> {
                    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        log.warn("Failed authentication attempt for username: {}", request.getUsername());
                        throw new UsernameNotFoundException("Invalid username or password");
                    }

                    if (user.getRoles() != UserRoles.HOSPITAL) {
                        log.warn("User {} attempted to authenticate as hospital but has role: {}", 
                                request.getUsername(), user.getRoles());
                        throw new UsernameNotFoundException("User is not authorized as a hospital");
                    }

                    Hospital hospital = findHospitalByUser(user);
                    int totalAppointmentsToday = countTodayAppointments(hospital.getHospitalId());
                    List<MonthlyDonation> monthlyDonations = getMonthlyDonationsByHospital(hospital.getHospitalId());
                    List<FrequentDonor> frequentDonors = donationRepository.findFrequentDonorsByHospital(hospital.getHospitalId());
                    List<UrgentRequestSummary> urgentRequests = getUrgentRequestsForHospital(hospital);

                    String token = jwtService.generateToken(user);

                    log.info("Hospital authenticated successfully: {}", hospital.getHospitalName());
                    return new AuthHospitalResponse(
                            hospital.getHospitalName(),
                            token,
                            totalAppointmentsToday,
                            monthlyDonations,
                            frequentDonors,
                            urgentRequests
                    );
                })
                .orElseThrow(() -> {
                    log.warn("Authentication failed: username not found: {}", request.getUsername());
                    return new UsernameNotFoundException("Invalid username or password");
                });
    }
    /**
     * Validates the authentication request.
     *
     * @param request The authentication request to validate
     * @throws IllegalArgumentException if the request is invalid
     */
    private void validateAuthRequest(AuthRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Authentication request cannot be null");
        }
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }

    /**
     * Finds a hospital by its associated user.
     *
     * @param user The user associated with the hospital
     * @return The hospital entity
     * @throws EntityNotFoundException if no hospital is found for the user
     */
    private Hospital findHospitalByUser(Users user) {
        return hospitalRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("Hospital not found for user: {}", user.getUsername());
                    return new EntityNotFoundException("Hospital not found for user: " + user.getUsername());
                });
    }

    /**
     * Counts the number of appointments for a hospital on the current day.
     *
     * @param hospitalId The ID of the hospital
     * @return The number of appointments today
     */
    private int countTodayAppointments(Long hospitalId) {
        LocalDate today = LocalDate.now();
        List<Appointment> appointments = appointmentRepository.findAppointmentsBySlot_Hospital_HospitalId(hospitalId);
        return (int) appointments.stream()
                .filter(a -> a.getAppointmentDate().equals(today))
                .count();
    }

    private List<MonthlyDonation> getMonthlyDonationsByHospital(Long hospitalId) {
        List<Object[]> results = donationRepository.findMonthlyDonationsByHospitalNative(hospitalId);

        return results.stream()
                .map(obj -> new MonthlyDonation(
                        (String) obj[0],
                        ((Number) obj[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Gets urgent requests for a hospital and converts them to UrgentRequestSummary objects.
     *
     * @param hospital The hospital entity
     * @return A list of UrgentRequestSummary objects
     */
    private List<UrgentRequestSummary> getUrgentRequestsForHospital(Hospital hospital) {
        List<UrgentRequest> urgentRequests = urgentRequestRepository.findByHospital(hospital);

        return urgentRequests.stream()
                .map(request -> new UrgentRequestSummary(
                        request.getRequestTime(),
                        request.getBloodType(),
                        request.getStatus()
                ))
                .collect(Collectors.toList());
    }
}
