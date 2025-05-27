package com.donorapi.hospital.models;

import java.util.List;

public record AuthHospitalResponse(
        String hospitalName,
        String token,
        Integer totalAppointment, //per day
        List<MonthlyDonation> monthlyDonations, // donations per month for graphing
        List<FrequentDonor> frequentDonors // frequent donors with profile name
) {
}
