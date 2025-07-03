package com.donorapi.hospital.models;


public record AuthHospitalResponse(
        String hospitalName,
        String token,
        Long hospitalId
) {
}
