package com.donorapi.jpa;

import com.donorapi.entity.Appointment;
import com.donorapi.entity.Donor;
import com.donorapi.models.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDonorAndStatusNot(Donor donor, AppointmentStatus status);

    Optional<Appointment> findByDonorAndStatusOrderBySlotEndTimeDesc(Donor donor, AppointmentStatus status);

    int countByDonor(Donor donor);

    int countByDonorAndStatus(Donor donor, AppointmentStatus status);
}
