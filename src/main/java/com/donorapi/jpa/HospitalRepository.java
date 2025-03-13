package com.donorapi.jpa;

import com.donorapi.entity.Hospital;
import com.donorapi.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Integer> {

    Optional<Hospital> findByHospitalName(String hospitalName);

    Optional<Hospital> findByUser(Users user);
}
