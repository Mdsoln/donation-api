package com.donorapi.hospital.service;

import com.donorapi.donor.entity.Donor;
import com.donorapi.donor.jpa.DonorRepository;
import com.donorapi.hospital.entity.Hospital;
import com.donorapi.hospital.entity.UrgentRequest;
import com.donorapi.hospital.jpa.HospitalRepository;
import com.donorapi.hospital.jpa.UrgentRequestRepository;
import com.donorapi.hospital.models.UrgentRequestCreateDTO;
import com.donorapi.hospital.models.UrgentRequestDTO;
import com.donorapi.utilities.UrgentRequestStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class UrgentRequestServiceImpl {

    private final UrgentRequestRepository urgentRequestRepository;
    private final HospitalRepository hospitalRepository;
    private final DonorRepository donorRepository;

    @Transactional
    public UrgentRequestDTO createUrgentRequest(UrgentRequestCreateDTO requestDTO) {
        log.info("Creating urgent request: {}", requestDTO);

        // Find hospital
        Hospital hospital = hospitalRepository.findById(requestDTO.getHospitalId())
                .orElseThrow(() -> new EntityNotFoundException("Hospital not found with ID: " + requestDTO.getHospitalId()));

        // Find donor
        Donor donor = donorRepository.findById(requestDTO.getDonorId().intValue())
                .orElseThrow(() -> new EntityNotFoundException("Donor not found with ID: " + requestDTO.getDonorId()));

        UrgentRequest urgentRequest = new UrgentRequest();
        urgentRequest.setHospital(hospital);
        urgentRequest.setDonor(donor);
        urgentRequest.setPatientName(requestDTO.getPatientName());
        urgentRequest.setBloodType(requestDTO.getBloodType());
        urgentRequest.setNotes(requestDTO.getNotes());

        if (requestDTO.getRequestDate() != null && !requestDTO.getRequestDate().isEmpty()) {
            urgentRequest.setRequestDate(LocalDate.parse(requestDTO.getRequestDate()));
        } else {
            urgentRequest.setRequestDate(LocalDate.now());
        }

        if (requestDTO.getRequestTime() != null && !requestDTO.getRequestTime().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalDateTime requestTime = LocalDate.now().atTime(
                    Integer.parseInt(requestDTO.getRequestTime().split(":")[0]),
                    Integer.parseInt(requestDTO.getRequestTime().split(":")[1])
            );
            urgentRequest.setRequestTime(requestTime);
        } else {
            urgentRequest.setRequestTime(LocalDateTime.now());
        }

        UrgentRequest savedRequest = urgentRequestRepository.save(urgentRequest);
        return convertToDTO(savedRequest);
    }

    public UrgentRequestDTO updateUrgentRequestStatus(Long requestId, String status) {
        log.info("Updating urgent request status: {} to {}", requestId, status);

        UrgentRequest urgentRequest = urgentRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Urgent request not found with ID: " + requestId));

        urgentRequest.setStatus(UrgentRequestStatus.valueOf(status));
        UrgentRequest updatedRequest = urgentRequestRepository.save(urgentRequest);

        return convertToDTO(updatedRequest);
    }

    public List<UrgentRequestDTO> getUrgentRequestsByDonor(Long donorId) {
        Donor donor = donorRepository.findById(donorId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Donor not found with ID: " + donorId));

        List<UrgentRequest> requests = urgentRequestRepository.findByDonor(donor);

        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UrgentRequestDTO> getUrgentRequestsByHospital(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new EntityNotFoundException("Hospital not found with ID: " + hospitalId));

        List<UrgentRequest> requests = urgentRequestRepository.findByHospital(hospital);

        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UrgentRequestDTO getUrgentRequestById(Long requestId) {
        UrgentRequest urgentRequest = urgentRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Urgent request not found with ID: " + requestId));

        return convertToDTO(urgentRequest);
    }

    private UrgentRequestDTO convertToDTO(UrgentRequest urgentRequest) {
        UrgentRequestDTO dto = new UrgentRequestDTO();
        dto.setUrgentId(urgentRequest.getUrgentId());
        dto.setHospitalId(urgentRequest.getHospital().getHospitalId());
        dto.setDonorId(urgentRequest.getDonor().getDonorId().longValue());
        dto.setPatientName(urgentRequest.getPatientName());
        dto.setBloodType(urgentRequest.getBloodType());
        dto.setNotes(urgentRequest.getNotes());
        dto.setStatus(urgentRequest.getStatus());
        dto.setRequestDate(urgentRequest.getRequestDate());
        dto.setRequestTime(urgentRequest.getRequestTime());
        dto.setCreatedAt(urgentRequest.getCreatedAt());
        dto.setUpdatedAt(urgentRequest.getUpdatedAt());
        return dto;
    }
}
