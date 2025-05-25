package com.donorapi.report.service;

import com.donorapi.report.model.DonorReportDTO;
import com.donorapi.report.model.HospitalReportDTO;
import com.donorapi.report.model.ReportRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Service interface for generating reports
 */
public interface ReportService {
    
    /**
     * Generate a report for a specific hospital
     * @param hospitalId The ID of the hospital
     * @param request The report request parameters
     * @return The hospital report data
     */
    HospitalReportDTO generateHospitalReport(Long hospitalId, ReportRequest request);
    
    /**
     * Generate a report for a specific donor
     * @param donorId The ID of the donor
     * @param request The report request parameters
     * @return The donor report data
     */
    DonorReportDTO generateDonorReport(Integer donorId, ReportRequest request);
    
    /**
     * Generate a comparison report for all hospitals
     * @param request The report request parameters
     * @return List of hospital reports for comparison
     */
    List<HospitalReportDTO> generateHospitalComparisonReport(ReportRequest request);
    
    /**
     * Export a hospital report as PDF
     * @param hospitalId The ID of the hospital
     * @param request The report request parameters
     * @return The PDF file as a Resource
     */
    ResponseEntity<Resource> exportHospitalReportAsPdf(Long hospitalId, ReportRequest request);
    
    /**
     * Export a hospital report as Excel
     * @param hospitalId The ID of the hospital
     * @param request The report request parameters
     * @return The Excel file as a Resource
     */
    ResponseEntity<Resource> exportHospitalReportAsExcel(Long hospitalId, ReportRequest request);
    
    /**
     * Export a donor report as PDF
     * @param donorId The ID of the donor
     * @param request The report request parameters
     * @return The PDF file as a Resource
     */
    ResponseEntity<Resource> exportDonorReportAsPdf(Integer donorId, ReportRequest request);
    
    /**
     * Export a donor report as Excel
     * @param donorId The ID of the donor
     * @param request The report request parameters
     * @return The Excel file as a Resource
     */
    ResponseEntity<Resource> exportDonorReportAsExcel(Integer donorId, ReportRequest request);
}