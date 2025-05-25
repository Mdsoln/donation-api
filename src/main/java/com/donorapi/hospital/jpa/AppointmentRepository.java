package com.donorapi.hospital.jpa;

import com.donorapi.hospital.entity.Appointment;
import com.donorapi.donor.entity.Donor;
import com.donorapi.utilities.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDonorAndStatusNot(Donor donor, AppointmentStatus status);

    Optional<Appointment> findByDonorAndStatusOrderBySlotEndTimeDesc(Donor donor, AppointmentStatus status);

    int countByDonor(Donor donor);

    int countByDonorAndStatus(Donor donor, AppointmentStatus status);

    List<Appointment> findByStatusAndSlotEndTimeBefore(AppointmentStatus status, LocalDateTime slot_endTime);

    @Query("SELECT DISTINCT d FROM Appointment a JOIN a.donor d WHERE a.slot.hospital.hospitalId = :hospitalId")
    List<Donor> findDistinctDonorsByHospitalId(@Param("hospitalId") Long hospitalId);

    List<Appointment> findAppointmentsBySlot_Hospital_HospitalId(Long slotHospitalHospitalId);

    List<Appointment> findByDonorOrderByAppointmentDateDesc(Donor donor);
}
