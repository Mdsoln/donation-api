package com.donorapi.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AppointmentRequest {
    @NotNull(message = "Hospital ID is required")
    @Min(value = 1, message = "Invalid Hospital ID")
    private int hospitalId;

    @NotNull(message = "Slot ID is required")
    @Min(value = 1, message = "Invalid Slot ID")
    private int slotId;

    @NotNull(message = "Donor ID is required")
    @Min(value = 1, message = "Invalid Donor ID")
    private int donorId;
}
