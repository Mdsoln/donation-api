package com.donorapi.hospital.entity;

import com.donorapi.donor.entity.Donor;
import com.donorapi.utilities.UrgentRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "urgent_requests")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UrgentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long urgentId;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToOne
    @JoinColumn(name = "donor_id")
    private Donor donor;

    private String patientName;
    private String bloodType;
    private String notes;

    @Enumerated(EnumType.STRING)
    private UrgentRequestStatus status;
    private LocalDate requestDate;
    private LocalDateTime requestTime;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now().withNano(0);
        this.status = UrgentRequestStatus.PENDING; // Default status
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = LocalDateTime.now().withNano(0);
    }
}
