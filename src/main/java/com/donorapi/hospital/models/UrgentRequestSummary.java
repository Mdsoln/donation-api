package com.donorapi.hospital.models;

import com.donorapi.utilities.UrgentRequestStatus;

import java.time.LocalDateTime;

/**
 * A simplified version of UrgentRequestDTO for use in the AuthHospitalResponse.
 * Contains only the fields mentioned in the issue description (time, blood type, status).
 */
public record UrgentRequestSummary(
        LocalDateTime requestTime,
        String bloodType,
        UrgentRequestStatus status
) {
}