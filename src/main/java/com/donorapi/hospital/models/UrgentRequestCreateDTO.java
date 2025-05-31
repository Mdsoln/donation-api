package com.donorapi.hospital.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UrgentRequestCreateDTO {
    @NotNull(message = "Hospital ID is required")
    private Long hospitalId;
    
    @NotNull(message = "Donor ID is required")
    private Long donorId;
    
    @NotBlank(message = "Patient name is required")
    private String patientName;
    
    @NotBlank(message = "Blood type is required")
    private String bloodType;
    
    private String notes;
    
    // The date and time can be set to current if not provided
    private String requestDate; // Format: yyyy-MM-dd
    private String requestTime; // Format: HH:mm
}