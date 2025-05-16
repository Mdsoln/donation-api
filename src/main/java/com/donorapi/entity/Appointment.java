package com.donorapi.entity;

import com.donorapi.models.AppointmentStatus;
import com.donorapi.service.AppointmentOverdueEvent;
import com.donorapi.config.DomainEventPublisher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;


@Entity
@Table(name = "appointment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime statusChangedAt;
    private boolean overdue = false;
    private boolean notificationSent = false;
    @Column(name = "date", nullable = false)
    private LocalDate appointmentDate;


    @PreUpdate
    public void checkForOverdue() {
        if (this.status == AppointmentStatus.SCHEDULED &&
                LocalDateTime.now().isAfter(this.slot.getEndTime())) {
            this.markAsOverdue();
            DomainEventPublisher.publish(new AppointmentOverdueEvent(this));
        }
    }

    public void markAsOverdue() {
        this.status = AppointmentStatus.OVERDUE;
        this.overdue = true;
        this.statusChangedAt = LocalDateTime.now();
    }


    public boolean hasPendingStatus(){
        return this.status.equals(AppointmentStatus.PENDING);
    }

    public boolean hasScheduledStatus(){
        return this.status.equals(AppointmentStatus.SCHEDULED);
    }

}
