package com.donorapi.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.donorapi.entity.Donation;

public interface DonationRepository extends JpaRepository<Donation,Integer> {

}
