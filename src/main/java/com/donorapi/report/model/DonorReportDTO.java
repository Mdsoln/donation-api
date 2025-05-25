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
    private String reportPeriod; // e.g., "Q1 2023", "2023", "All Time", etc.
    private LocalDate startDate;
    private LocalDate endDate;
    
    // Donation statistics
    private int totalDonations;
    private double totalVolumeMl;
    private LocalDate firstDonationDate;
    private LocalDate lastDonationDate;
    
    // Appointment statistics
    private int totalAppointments;
    private int completedAppointments;
    private int scheduledAppointments;
    private int expiredAppointments;
    private int cancelledAppointments;
    
    // Request statistics
    private int totalRequests;
    private int acceptedRequests;
    private int rejectedRequests;
    
    // Donation history by period (quarterly, yearly)
    private List<PeriodData> periodData;
    
    // Hospital breakdown
    private List<HospitalData> hospitalData;
    
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