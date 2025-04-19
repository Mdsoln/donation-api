package com.donorapi.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.donorapi.exception.InvalidAppointmentStatusException;
import com.donorapi.models.HospitalAppointment;
import com.donorapi.utilities.DateFormatter;
import org.springframework.stereotype.Service;

import com.donorapi.entity.Appointment;
import com.donorapi.entity.Donor;
import com.donorapi.jpa.AppointmentRepository;
import com.donorapi.jpa.DonationRepository;
import com.donorapi.models.AppointmentStatus;
import com.donorapi.models.HospitalDonors;

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

    public List<HospitalAppointment> findAppointmentsByHospital(Integer hospitalId){
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

}