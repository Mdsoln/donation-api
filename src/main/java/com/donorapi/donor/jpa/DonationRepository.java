package com.donorapi.donor.jpa;

import com.donorapi.hospital.models.HospitalDonors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.donorapi.donor.entity.Donation;
import com.donorapi.donor.entity.Donor;

import java.time.LocalDateTime;
import java.util.List;

public interface DonationRepository extends JpaRepository<Donation,Integer> {

    @Query("SELECT COUNT(d) FROM Donation d WHERE d.appointment.donor = :donor AND d.appointment.slot.hospital.hospitalId = :hospitalId")
    int countDonationsByDonorAndHospital(@Param("donor") Donor donor, @Param("hospitalId") Long hospitalId);

    @Query("""
    SELECT new com.donorapi.models.HospitalDonors(
        d.donorId,
        d.fullName,
        d.email,
        d.phone,
        d.bloodType,
        COUNT(do)
    )
    FROM Donation do
    JOIN do.appointment a
    JOIN a.donor d
    JOIN a.slot s
    WHERE s.hospital.hospitalId = :hospitalId
    GROUP BY d.donorId, d.fullName, d.email, d.phone, d.bloodType
    ORDER BY COUNT(do) DESC
    """)
    List<HospitalDonors> findTopDonorsByHospital(@Param("hospitalId") Long hospitalId);

    @Query("SELECT d.donationDate FROM Donation d " +
            "WHERE d.appointment.donor.donorId = :donorId " +
            "ORDER BY d.donationDate DESC")
    LocalDateTime findTopByDonorIdOrderByDonationDateDesc(@Param("donorId") Integer donorId);

}
