package com.donorapi.hospital.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.donorapi.donor.entity.Donation;
import com.donorapi.donor.jpa.DonorRepository;
import com.donorapi.donor.models.DonationRequest;
import com.donorapi.exception.InvalidAppointmentStatusException;
import com.donorapi.hospital.models.HospitalAppointment;
import com.donorapi.utilities.DateFormatter;
import org.springframework.stereotype.Service;

import com.donorapi.hospital.entity.Appointment;
import com.donorapi.donor.entity.Donor;
import com.donorapi.hospital.jpa.AppointmentRepository;
import com.donorapi.donor.jpa.DonationRepository;
import com.donorapi.utilities.AppointmentStatus;
import com.donorapi.hospital.models.HospitalDonors;

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
}
