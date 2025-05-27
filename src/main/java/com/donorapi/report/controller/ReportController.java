package com.donorapi.report.controller;

import com.donorapi.report.model.DonorReportDTO;
import com.donorapi.report.model.HospitalReportDTO;
import com.donorapi.report.model.ReportRequest;
import com.donorapi.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for report generation and export
 */
@RestController
@RequestMapping(path = "/api/v1/reports")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Reports", description = "Endpoints for generating and exporting reports")
public class ReportController {

    private final ReportService reportService;

    /**
     * Generate a report for a specific hospital
     * @param hospitalId The ID of the hospital
     * @param request The report request parameters
     * @return The hospital report data
     */
    @PostMapping("/hospital/{hospitalId}")
    @PreAuthorize("hasRole('HOSPITAL')")
    @Operation(summary = "Generate a hospital report", description = "Generate a report for a specific hospital with various metrics")
    public ResponseEntity<HospitalReportDTO> generateHospitalReport(
            @PathVariable Long hospitalId,
            @RequestBody ReportRequest request) {
        
        log.info("Generating hospital report for hospital ID: {}", hospitalId);
        
        if (!request.isValidRequest()) {
            return ResponseEntity.badRequest().build();
        }
        
        HospitalReportDTO report = reportService.generateHospitalReport(hospitalId, request);
        return ResponseEntity.ok(report);
    }

    /**
     * Generate a report for a specific donor
     * @param donorId The ID of the donor
     * @param request The report request parameters
     * @return The donor report data
     */
    @PostMapping("/donor/{donorId}")
    @PreAuthorize("hasRole('DONOR')")
    @Operation(summary = "Generate a donor report", description = "Generate a report for a specific donor with various metrics")
    public ResponseEntity<DonorReportDTO> generateDonorReport(
            @PathVariable Integer donorId,
            @RequestBody ReportRequest request) {
        
        log.info("Generating donor report for donor ID: {}", donorId);
        
        if (!request.isValidRequest()) {
            return ResponseEntity.badRequest().build();
        }
        
        DonorReportDTO report = reportService.generateDonorReport(donorId, request);
        return ResponseEntity.ok(report);
    }

    /**
     * Generate a comparison report for all hospitals
     * @param request The report request parameters
     * @return List of hospital reports for comparison
     */
    @PostMapping("/hospital/comparison")
    @PreAuthorize("hasRole('HOSPITAL')")
    @Operation(summary = "Generate a hospital comparison report", description = "Generate a comparison report for all hospitals")
    public ResponseEntity<List<HospitalReportDTO>> generateHospitalComparisonReport(
            @RequestBody ReportRequest request) {
        
        log.info("Generating hospital comparison report");
        
        if (!request.isValidRequest()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<HospitalReportDTO> reports = reportService.generateHospitalComparisonReport(request);
        return ResponseEntity.ok(reports);
    }

    /**
     * Export a hospital report as PDF
     * @param hospitalId The ID of the hospital
     * @param request The report request parameters
     * @return The PDF file as a Resource
     */
    @PostMapping("/hospital/{hospitalId}/export/pdf")
    @PreAuthorize("hasRole('HOSPITAL')")
    @Operation(summary = "Export a hospital report as PDF", description = "Generate and export a hospital report as PDF")
    public ResponseEntity<Resource> exportHospitalReportAsPdf(
            @PathVariable Long hospitalId,
            @RequestBody ReportRequest request) {
        
        log.info("Exporting hospital report as PDF for hospital ID: {}", hospitalId);
        
        if (!request.isValidRequest()) {
            return ResponseEntity.badRequest().build();
        }
        
        return reportService.exportHospitalReportAsPdf(hospitalId, request);
    }

    /**
     * Export a hospital report as Excel
     * @param hospitalId The ID of the hospital
     * @param request The report request parameters
     * @return The Excel file as a Resource
     */
    @PostMapping("/hospital/{hospitalId}/export/excel")
    @PreAuthorize("hasRole('HOSPITAL')")
    @Operation(summary = "Export a hospital report as Excel", description = "Generate and export a hospital report as Excel")
    public ResponseEntity<Resource> exportHospitalReportAsExcel(
            @PathVariable Long hospitalId,
            @RequestBody ReportRequest request) {
        
        log.info("Exporting hospital report as Excel for hospital ID: {}", hospitalId);
        
        if (!request.isValidRequest()) {
            return ResponseEntity.badRequest().build();
        }
        
        return reportService.exportHospitalReportAsExcel(hospitalId, request);
    }

    /**
     * Export a donor report as PDF
     * @param donorId The ID of the donor
     * @param request The report request parameters
     * @return The PDF file as a Resource
     */
    @PostMapping("/donor/{donorId}/export/pdf")
    @PreAuthorize("hasRole('DONOR')")
    @Operation(summary = "Export a donor report as PDF", description = "Generate and export a donor report as PDF")
    public ResponseEntity<Resource> exportDonorReportAsPdf(
            @PathVariable Integer donorId,
            @RequestBody ReportRequest request) {
        
        log.info("Exporting donor report as PDF for donor ID: {}", donorId);
        
        if (!request.isValidRequest()) {
            return ResponseEntity.badRequest().build();
        }
        
        return reportService.exportDonorReportAsPdf(donorId, request);
    }

    /**
     * Export a donor report as Excel
     * @param donorId The ID of the donor
     * @param request The report request parameters
     * @return The Excel file as a Resource
     */
    @PostMapping("/donor/{donorId}/export/excel")
    @PreAuthorize("hasRole('DONOR')")
    @Operation(summary = "Export a donor report as Excel", description = "Generate and export a donor report as Excel")
    public ResponseEntity<Resource> exportDonorReportAsExcel(
            @PathVariable Integer donorId,
            @RequestBody ReportRequest request) {
        
        log.info("Exporting donor report as Excel for donor ID: {}", donorId);
        
        if (!request.isValidRequest()) {
            return ResponseEntity.badRequest().build();
        }
        
        return reportService.exportDonorReportAsExcel(donorId, request);
    }
}