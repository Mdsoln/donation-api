package com.donorapi.report.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for Hospital Reports
 * Contains summary information about donations at a hospital
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalReportDTO {
    private Long hospitalId;
    private String hospitalName;
    private String reportPeriod; // e.g., "Q1 2023", "2023", etc.
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalDonations;
    private double totalVolumeMl;
    private int uniqueDonors;
    private List<QuarterlyData> quarterlyData; // For yearly reports
    private List<BloodTypeData> bloodTypeDistribution;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuarterlyData {
        private String quarter; // e.g., "Q1", "Q2", etc.
        private int donations;
        private double volumeMl;
        private int donors;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BloodTypeData {
        private String bloodType;
        private int count;
        private double percentage;
    }
}