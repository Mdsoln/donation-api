package com.donorapi.hospital.controller;

import com.donorapi.hospital.models.UrgentRequestCreateDTO;
import com.donorapi.hospital.models.UrgentRequestDTO;
import com.donorapi.hospital.service.UrgentRequestServiceImpl;
import com.donorapi.utilities.UrgentRequestStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/urgent-requests")
@RequiredArgsConstructor
@Log4j2
@Validated
public class UrgentRequestController {

    private final UrgentRequestServiceImpl urgentRequestService;

    @PostMapping
    public ResponseEntity<UrgentRequestDTO> createUrgentRequest(@RequestBody @Validated UrgentRequestCreateDTO requestDTO) {
        log.info("Creating urgent request: {}", requestDTO);
        UrgentRequestDTO createdRequest = urgentRequestService.createUrgentRequest(requestDTO);
        return ResponseEntity.ok(createdRequest);
    }

    @PutMapping("/{requestId}/status")
    public ResponseEntity<UrgentRequestDTO> updateUrgentRequestStatus(
            @PathVariable Long requestId,
            @RequestParam String status) {
        log.info("Updating urgent request status: {} to {}", requestId, status);

        try {
            UrgentRequestStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        UrgentRequestDTO updatedRequest = urgentRequestService.updateUrgentRequestStatus(requestId, status);
        return ResponseEntity.ok(updatedRequest);
    }

    @GetMapping("/donor/{donorId}")
    public ResponseEntity<List<UrgentRequestDTO>> getUrgentRequestsByDonor(@PathVariable Long donorId) {
        log.info("Getting urgent requests for donor: {}", donorId);
        List<UrgentRequestDTO> requests = urgentRequestService.getUrgentRequestsByDonor(donorId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<UrgentRequestDTO>> getUrgentRequestsByHospital(@PathVariable Long hospitalId) {
        log.info("Getting urgent requests for hospital: {}", hospitalId);
        List<UrgentRequestDTO> requests = urgentRequestService.getUrgentRequestsByHospital(hospitalId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<UrgentRequestDTO> getUrgentRequestById(@PathVariable Long requestId) {
        log.info("Getting urgent request by ID: {}", requestId);
        UrgentRequestDTO request = urgentRequestService.getUrgentRequestById(requestId);
        return ResponseEntity.ok(request);
    }
}
