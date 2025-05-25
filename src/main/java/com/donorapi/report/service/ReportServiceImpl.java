package com.donorapi.report.service;


import com.donorapi.donor.entity.Donation;
import com.donorapi.donor.entity.Donor;
import com.donorapi.donor.jpa.DonationRepository;
import com.donorapi.donor.jpa.DonorRepository;
import com.donorapi.hospital.entity.Appointment;
import com.donorapi.hospital.entity.Hospital;
import com.donorapi.hospital.entity.Slot;
import com.donorapi.hospital.jpa.AppointmentRepository;
import com.donorapi.hospital.jpa.HospitalRepository;
import com.donorapi.hospital.jpa.SlotsRepository;
import com.donorapi.report.model.DonorReportDTO;
import com.donorapi.report.model.HospitalReportDTO;
import com.donorapi.report.model.ReportRequest;
import com.donorapi.report.util.ExcelExporter;
import com.donorapi.report.util.PdfExporter;
import com.donorapi.utilities.AppointmentStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReportServiceImpl implements ReportService {

    private final DonationRepository donationRepository;
    private final AppointmentRepository appointmentRepository;
    private final HospitalRepository hospitalRepository;
    private final DonorRepository donorRepository;
    private final SlotsRepository slotsRepository;
    private final PdfExporter pdfExporter;
    private final ExcelExporter excelExporter;

    @Override
    public HospitalReportDTO generateHospitalReport(Long hospitalId, ReportRequest request) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new EntityNotFoundException("Hospital not found with ID: " + hospitalId));

        // Determine date range based on report type
        LocalDate startDate = getStartDate(request);
        LocalDate endDate = getEndDate(request);

        // Get all slots for this hospital
        List<Slot> slots = slotsRepository.findSlotsByHospitalId(hospitalId);

        // Get all appointments for these slots
        List<Appointment> appointments = new ArrayList<>();
        for (Slot slot : slots) {
            appointments.addAll(slot.getAppointments());
        }

        // Filter appointments by date range
        List<Appointment> filteredAppointments = appointments.stream()
                .filter(a -> {
                    LocalDate appointmentDate = a.getAppointmentDate();
                    return !appointmentDate.isBefore(startDate) && !appointmentDate.isAfter(endDate);
                })
                .toList();

        // Get donations for these appointments
        List<Donation> donations = new ArrayList<>();
        for (Appointment appointment : filteredAppointments) {
            // Find donation for this appointment
            // Note: This is inefficient and should be replaced with a proper query
            List<Donation> appointmentDonations = donationRepository.findAll().stream()
                    .filter(d -> d.getAppointment() != null && d.getAppointment().getId().equals(appointment.getId()))
                    .toList();
            donations.addAll(appointmentDonations);
        }

        // Calculate statistics
        int totalDonations = donations.size();
        double totalVolumeMl = donations.stream().mapToDouble(Donation::getVolumeMl).sum();

        // Count unique donors
        Set<Integer> uniqueDonorIds = donations.stream()
                .map(d -> d.getDonor().getDonorId())
                .collect(Collectors.toSet());
        int uniqueDonors = uniqueDonorIds.size();

        // Create blood type distribution
        Map<String, Integer> bloodTypeCount = new HashMap<>();
        for (Donation donation : donations) {
            String bloodType = donation.getBloodType();
            bloodTypeCount.put(bloodType, bloodTypeCount.getOrDefault(bloodType, 0) + 1);
        }

        List<HospitalReportDTO.BloodTypeData> bloodTypeDistribution = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : bloodTypeCount.entrySet()) {
            double percentage = (double) entry.getValue() / totalDonations * 100;
            bloodTypeDistribution.add(HospitalReportDTO.BloodTypeData.builder()
                    .bloodType(entry.getKey())
                    .count(entry.getValue())
                    .percentage(percentage)
                    .build());
        }

        // Create quarterly data for yearly reports
        List<HospitalReportDTO.QuarterlyData> quarterlyData = new ArrayList<>();
        if (request.getReportType() == ReportRequest.ReportType.YEARLY) {
            // Group donations by quarter
            Map<Integer, List<Donation>> donationsByQuarter = donations.stream()
                    .collect(Collectors.groupingBy(d -> (d.getDonationDate().getMonthValue() - 1) / 3 + 1));

            for (int quarter = 1; quarter <= 4; quarter++) {
                List<Donation> quarterDonations = donationsByQuarter.getOrDefault(quarter, Collections.emptyList());
                double quarterVolume = quarterDonations.stream().mapToDouble(Donation::getVolumeMl).sum();
                Set<Integer> quarterDonorIds = quarterDonations.stream()
                        .map(d -> d.getDonor().getDonorId())
                        .collect(Collectors.toSet());

                quarterlyData.add(HospitalReportDTO.QuarterlyData.builder()
                        .quarter("Q" + quarter)
                        .donations(quarterDonations.size())
                        .volumeMl(quarterVolume)
                        .donors(quarterDonorIds.size())
                        .build());
            }
        }

        // Build the report
        return HospitalReportDTO.builder()
                .hospitalId(hospitalId)
                .hospitalName(hospital.getHospitalName())
                .reportPeriod(getReportPeriodString(request))
                .startDate(startDate)
                .endDate(endDate)
                .totalDonations(totalDonations)
                .totalVolumeMl(totalVolumeMl)
                .uniqueDonors(uniqueDonors)
                .quarterlyData(quarterlyData)
                .bloodTypeDistribution(bloodTypeDistribution)
                .build();
    }

    @Override
    public DonorReportDTO generateDonorReport(Integer donorId, ReportRequest request) {
        Donor donor = donorRepository.findByDonorId(donorId)
                .orElseThrow(() -> new EntityNotFoundException("Donor not found with ID: " + donorId));

        // Determine date range based on report type
        LocalDate startDate = getStartDate(request);
        LocalDate endDate = getEndDate(request);

        // Get all appointments for this donor
        List<Appointment> allAppointments = appointmentRepository.findByDonorOrderByAppointmentDateDesc(donor);

        // Filter appointments by date range
        List<Appointment> filteredAppointments = allAppointments.stream()
                .filter(a -> {
                    LocalDate appointmentDate = a.getAppointmentDate();
                    return !appointmentDate.isBefore(startDate) && !appointmentDate.isAfter(endDate);
                })
                .toList();

        // Get donations for these appointments
        List<Donation> donations = new ArrayList<>();
        for (Appointment appointment : filteredAppointments) {
            // Find donation for this appointment
            // Note: This is inefficient and should be replaced with a proper query
            List<Donation> appointmentDonations = donationRepository.findAll().stream()
                    .filter(d -> d.getAppointment() != null && d.getAppointment().getId().equals(appointment.getId()))
                    .toList();
            donations.addAll(appointmentDonations);
        }

        // Calculate donation statistics
        int totalDonations = donations.size();
        double totalVolumeMl = donations.stream().mapToDouble(Donation::getVolumeMl).sum();

        // Find first and last donation dates
        LocalDate firstDonationDate = donations.stream()
                .map(d -> d.getDonationDate().toLocalDate())
                .min(LocalDate::compareTo)
                .orElse(null);

        LocalDate lastDonationDate = donations.stream()
                .map(d -> d.getDonationDate().toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(null);

        // Calculate appointment statistics
        int totalAppointments = filteredAppointments.size();
        int completedAppointments = (int) filteredAppointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .count();
        int scheduledAppointments = (int) filteredAppointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
                .count();
        int expiredAppointments = (int) filteredAppointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.OVERDUE)
                .count();
        int cancelledAppointments = (int) filteredAppointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CANCELLED)
                .count();

        // Create period data (quarterly or yearly)
        List<DonorReportDTO.PeriodData> periodData = new ArrayList<>();
        if (request.getReportType() == ReportRequest.ReportType.YEARLY) {
            // Group donations by quarter
            Map<String, List<Donation>> donationsByQuarter = donations.stream()
                    .collect(Collectors.groupingBy(d -> {
                        int quarter = (d.getDonationDate().getMonthValue() - 1) / 3 + 1;
                        return "Q" + quarter + " " + d.getDonationDate().getYear();
                    }));

            for (Map.Entry<String, List<Donation>> entry : donationsByQuarter.entrySet()) {
                double quarterVolume = entry.getValue().stream().mapToDouble(Donation::getVolumeMl).sum();

                periodData.add(DonorReportDTO.PeriodData.builder()
                        .period(entry.getKey())
                        .donations(entry.getValue().size())
                        .volumeMl(quarterVolume)
                        .build());
            }
        }

        // Create hospital breakdown
        List<DonorReportDTO.HospitalData> hospitalData = new ArrayList<>();
        if (request.isIncludeHospitalBreakdown()) {
            // Group donations by hospital
            Map<Long, List<Donation>> donationsByHospital = donations.stream()
                    .collect(Collectors.groupingBy(d -> d.getAppointment().getSlot().getHospital().getHospitalId()));

            for (Map.Entry<Long, List<Donation>> entry : donationsByHospital.entrySet()) {
                Hospital hospital = hospitalRepository.findById(entry.getKey())
                        .orElseThrow(() -> new EntityNotFoundException("Hospital not found with ID: " + entry.getKey()));

                double hospitalVolume = entry.getValue().stream().mapToDouble(Donation::getVolumeMl).sum();
                double percentage = (double) entry.getValue().size() / totalDonations * 100;

                hospitalData.add(DonorReportDTO.HospitalData.builder()
                        .hospitalId(hospital.getHospitalId())
                        .hospitalName(hospital.getHospitalName())
                        .donations(entry.getValue().size())
                        .volumeMl(hospitalVolume)
                        .percentage(percentage)
                        .build());
            }
        }

        // Build the report
        return DonorReportDTO.builder()
                .donorId(donorId)
                .donorName(donor.getFullName())
                .bloodType(donor.getBloodType())
                .reportPeriod(getReportPeriodString(request))
                .startDate(startDate)
                .endDate(endDate)
                .totalDonations(totalDonations)
                .totalVolumeMl(totalVolumeMl)
                .firstDonationDate(firstDonationDate)
                .lastDonationDate(lastDonationDate)
                .totalAppointments(totalAppointments)
                .completedAppointments(completedAppointments)
                .scheduledAppointments(scheduledAppointments)
                .expiredAppointments(expiredAppointments)
                .cancelledAppointments(cancelledAppointments)
                .periodData(periodData)
                .hospitalData(hospitalData)
                .build();
    }

    @Override
    public List<HospitalReportDTO> generateHospitalComparisonReport(ReportRequest request) {
        List<Hospital> hospitals = hospitalRepository.findAll();
        List<HospitalReportDTO> reports = new ArrayList<>();

        for (Hospital hospital : hospitals) {
            HospitalReportDTO report = generateHospitalReport(hospital.getHospitalId(), request);
            reports.add(report);
        }

        // Sort by total donations (descending)
        reports.sort(Comparator.comparing(HospitalReportDTO::getTotalDonations).reversed());

        return reports;
    }

    @Override
    public ResponseEntity<Resource> exportHospitalReportAsPdf(Long hospitalId, ReportRequest request) {
        HospitalReportDTO report = generateHospitalReport(hospitalId, request);
        byte[] pdfBytes = pdfExporter.exportHospitalReportToPdf(report);

        return createResourceResponse(pdfBytes, "hospital_report_" + hospitalId + ".pdf", MediaType.APPLICATION_PDF);
    }

    @Override
    public ResponseEntity<Resource> exportHospitalReportAsExcel(Long hospitalId, ReportRequest request) {
        HospitalReportDTO report = generateHospitalReport(hospitalId, request);
        byte[] excelBytes = excelExporter.exportHospitalReportToExcel(report);

        return createResourceResponse(excelBytes, "hospital_report_" + hospitalId + ".xlsx", 
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @Override
    public ResponseEntity<Resource> exportDonorReportAsPdf(Integer donorId, ReportRequest request) {
        DonorReportDTO report = generateDonorReport(donorId, request);
        byte[] pdfBytes = pdfExporter.exportDonorReportToPdf(report);

        return createResourceResponse(pdfBytes, "donor_report_" + donorId + ".pdf", MediaType.APPLICATION_PDF);
    }

    @Override
    public ResponseEntity<Resource> exportDonorReportAsExcel(Integer donorId, ReportRequest request) {
        DonorReportDTO report = generateDonorReport(donorId, request);
        byte[] excelBytes = excelExporter.exportDonorReportToExcel(report);

        return createResourceResponse(excelBytes, "donor_report_" + donorId + ".xlsx", 
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    // Helper methods

    private LocalDate getStartDate(ReportRequest request) {
        switch (request.getReportType()) {
            case QUARTERLY:
                int year = request.getYear();
                int quarter = request.getQuarter();
                int month = (quarter - 1) * 3 + 1;
                return LocalDate.of(year, month, 1);
            case YEARLY:
                return LocalDate.of(request.getYearOnly(), 1, 1);
            case CUSTOM:
                return request.getStartDate();
            case ALL_TIME:
            default:
                return LocalDate.of(2000, 1, 1); // A date far in the past
        }
    }

    private LocalDate getEndDate(ReportRequest request) {
        switch (request.getReportType()) {
            case QUARTERLY:
                int year = request.getYear();
                int quarter = request.getQuarter();
                int month = quarter * 3;
                return LocalDate.of(year, month, Month.of(month).length(LocalDate.of(year, month, 1).isLeapYear()));
            case YEARLY:
                return LocalDate.of(request.getYearOnly(), 12, 31);
            case CUSTOM:
                return request.getEndDate();
            case ALL_TIME:
            default:
                return LocalDate.now();
        }
    }

    private String getReportPeriodString(ReportRequest request) {
        switch (request.getReportType()) {
            case QUARTERLY:
                return "Q" + request.getQuarter() + " " + request.getYear();
            case YEARLY:
                return String.valueOf(request.getYearOnly());
            case CUSTOM:
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
                return request.getStartDate().format(formatter) + " - " + request.getEndDate().format(formatter);
            case ALL_TIME:
            default:
                return "All Time";
        }
    }

    private ResponseEntity<Resource> createResourceResponse(byte[] bytes, String filename, MediaType mediaType) {
        ByteArrayResource resource = new ByteArrayResource(bytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .contentLength(bytes.length)
                .body(resource);
    }
}
