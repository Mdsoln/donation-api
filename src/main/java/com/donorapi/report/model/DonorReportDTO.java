package com.donorapi.report.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for Donor Reports
 * Contains summary information about a donor's donation history and appointments
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonorReportDTO {
    private Integer donorId;
    private String donorName;
    private String bloodType;
    private String location;

    // Donation statistics
    private int totalDonations;
    private LocalDate lastDonation;
    private LocalDate eligibleDate;
    
    // Appointment statistics
    private int completedAppointments;
    private int scheduledAppointments;
    private int expiredAppointments;

    // Hospital breakdown
    private String topDonatingCenter;
    private String activeMonth;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodData {
        private String period; // e.g., "Q1 2023", "2023", etc.
        private int donations;
        private double volumeMl;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HospitalData {
        private Long hospitalId;
        private String hospitalName;
        private int donations;
        private double volumeMl;
        private double percentage; // Percentage of total donations
    }
}