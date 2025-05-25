package com.donorapi.donor.models;

import jakarta.validation.constraints.NotNull;

public record DonationRequest(
        Long appointmentId,  // Optional for donations with appointments

        @NotNull(message = "Donor ID cannot be null")
        Integer donorId,     // Required
        @NotNull(message = "Required amount of blood donated")
        double volumeMl,     // Required
        @NotNull(message = "Required blood type donated")
        String bloodType,    // Required
        String notes      // Optional
) {
}
