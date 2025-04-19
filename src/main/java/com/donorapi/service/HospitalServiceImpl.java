package com.donorapi.service;

import org.springframework.stereotype.Service;

import com.donorapi.jpa.AppointmentRepository;
import com.donorapi.jpa.DonationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl {
    private final AppointmentRepository appointmentRepository;
    private final DonationRepository donationRepository;

    //todo: approve appointment || view list of donors on that hospital || list of appointments mdae to that hospital
    // || list of top donors donates on that hospital || add slots details for donation etc
}