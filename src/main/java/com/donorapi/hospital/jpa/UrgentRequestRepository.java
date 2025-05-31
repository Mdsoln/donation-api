package com.donorapi.hospital.jpa;

import com.donorapi.hospital.entity.UrgentRequest;
import com.donorapi.donor.entity.Donor;
import com.donorapi.hospital.entity.Hospital;
import com.donorapi.utilities.UrgentRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrgentRequestRepository extends JpaRepository<UrgentRequest, Long> {
    List<UrgentRequest> findByDonor(Donor donor);
    List<UrgentRequest> findByHospital(Hospital hospital);
    List<UrgentRequest> findByDonorAndStatus(Donor donor, UrgentRequestStatus status);
    List<UrgentRequest> findByHospitalAndStatus(Hospital hospital, UrgentRequestStatus status);
}
