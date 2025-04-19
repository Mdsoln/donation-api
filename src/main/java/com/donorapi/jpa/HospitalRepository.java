package com.donorapi.jpa;

import com.donorapi.entity.Hospital;
import com.donorapi.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    Optional<Hospital> findByHospitalName(String hospitalName);

    Optional<Hospital> findByUser(Users user);

    @Query("SELECT h FROM Hospital h WHERE h.latitude IS NOT NULL AND h.longitude IS NOT NULL")
    List<Hospital> findAllWithCoordinates();
}
