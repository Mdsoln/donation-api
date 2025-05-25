package com.donorapi.report.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request object for generating reports
 * Contains parameters for filtering and customizing reports
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    
    public enum ReportType {
        QUARTERLY,
        YEARLY,
        CUSTOM,
        ALL_TIME
    }
    
    public enum ReportFormat {
        PDF,
        EXCEL,
        JSON
    }
    
    private ReportType reportType;
    private ReportFormat reportFormat;
    
    // For QUARTERLY reports
    private Integer year;
    private Integer quarter;
    
    // For YEARLY reports
    private Integer yearOnly;
    
    // For CUSTOM reports
    private LocalDate startDate;
    private LocalDate endDate;
    
    // For hospital comparison reports
    private boolean includeHospitalComparison;
    
    // For blood type distribution
    private boolean includeBloodTypeDistribution;
    
    // For donor reports
    private boolean includeAppointmentStats;
    private boolean includeRequestStats;
    private boolean includeHospitalBreakdown;
    
    // Validation methods
    public boolean isValidQuarterlyRequest() {
        return reportType == ReportType.QUARTERLY && year != null && quarter != null && quarter >= 1 && quarter <= 4;
    }
    
    public boolean isValidYearlyRequest() {
        return reportType == ReportType.YEARLY && yearOnly != null;
    }
    
    public boolean isValidCustomRequest() {
        return reportType == ReportType.CUSTOM && startDate != null && endDate != null && !startDate.isAfter(endDate);
    }
    
    public boolean isValidRequest() {
        return reportType != null && reportFormat != null && (
            reportType == ReportType.ALL_TIME ||
            isValidQuarterlyRequest() ||
            isValidYearlyRequest() ||
            isValidCustomRequest()
        );
    }
}