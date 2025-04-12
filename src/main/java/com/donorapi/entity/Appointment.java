package com.donorapi.entity;

import com.donorapi.models.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private String description;
    private boolean bloodDonated;
    private LocalDateTime statusChangedAt;
    private boolean overdue = false;
    private boolean notificationSent = false;
    @Column(name = "date", nullable = false)
    private LocalDate appointmentDate;


    public void markAsOverdue() {
        this.status = AppointmentStatus.OVERDUE;
        this.overdue = true;
        this.statusChangedAt = LocalDateTime.now();
    }
}
