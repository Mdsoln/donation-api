package com.donorapi.jpa;

import com.donorapi.entity.Donor;
import com.donorapi.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Integer> {
    Optional<Donor> findByEmail(String email);

    Optional<Donor> findByUser(Users user);

   Optional<Donor> findByDonorId(Integer donorId);
}
