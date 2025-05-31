package com.donorapi.hospital.models;

import com.donorapi.utilities.UrgentRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UrgentRequestDTO {
    private Long urgentId;
    private Long hospitalId;
    private Long donorId;
    private String patientName;
    private String bloodType;
    private String notes;
    private UrgentRequestStatus status;
    private LocalDate requestDate;
    private LocalDateTime requestTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
