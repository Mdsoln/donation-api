package com.donorapi.donor.jpa;

import com.donorapi.hospital.models.FrequentDonor;
import com.donorapi.hospital.models.HospitalDonors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.donorapi.donor.entity.Donation;
import com.donorapi.donor.entity.Donor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DonationRepository extends JpaRepository<Donation,Integer> {

    @Query("SELECT COUNT(d) FROM Donation d WHERE d.appointment.donor = :donor AND d.appointment.slot.hospital.hospitalId = :hospitalId")
    int countDonationsByDonorAndHospital(@Param("donor") Donor donor, @Param("hospitalId") Long hospitalId);

    @Query("""
    SELECT new com.donorapi.hospital.models.HospitalDonors(
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

    @Query(value = """
    SELECT TO_CHAR(d.donation_date, 'YYYY-MM') AS month, COUNT(*) AS count
    FROM donations d
    JOIN appointment a ON d.appointment_id = a.id
    JOIN slots s ON a.slot_id = s.id
    WHERE s.hospital_id = :hospitalId
      AND EXTRACT(YEAR FROM d.donation_date) = EXTRACT(YEAR FROM CURRENT_DATE)
    GROUP BY TO_CHAR(d.donation_date, 'YYYY-MM')
    ORDER BY TO_CHAR(d.donation_date, 'YYYY-MM')
    """, nativeQuery = true)
    List<Object[]> findMonthlyDonationsByHospitalNative(@Param("hospitalId") Long hospitalId);

    @Query("""
    SELECT new com.donorapi.hospital.models.FrequentDonor(
        d.donorId,
        d.fullName,
        d.bloodType,
        COUNT(do),
        d.user.username
    )
    FROM Donation do
    JOIN do.appointment a
    JOIN a.donor d
    JOIN a.slot s
    WHERE s.hospital.hospitalId = :hospitalId
    GROUP BY d.donorId, d.fullName, d.bloodType, d.user.username
    ORDER BY COUNT(do) DESC
    LIMIT 10
    """)
    List<FrequentDonor> findFrequentDonorsByHospital(@Param("hospitalId") Long hospitalId);

    @Query("SELECT d FROM Donation d JOIN d.appointment a WHERE a.donor.donorId = :donorId AND a.appointmentDate BETWEEN :start AND :end")
    List<Donation> findDonationsByDonorAndDateRange(@Param("donorId") Integer donorId, @Param("start") LocalDate startDate, @Param("end") LocalDate endDate);
}
