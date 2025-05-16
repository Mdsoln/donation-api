package com.donorapi.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "donations")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long donationId;

    @ManyToOne
    @JoinColumn(name = "donor_id")
    private Donor donor;

    private double volumeMl;
    private String bloodType;
    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime donationDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime nextEligibleDate;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = true)
    private Appointment appointment;

    public void calculateNextEligibility() {
        this.nextEligibleDate = this.donationDate.plusMonths(3); // 3-month gap
    }
}
