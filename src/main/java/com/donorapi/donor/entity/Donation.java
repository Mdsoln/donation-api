package com.donorapi.donor.entity;


import com.donorapi.hospital.entity.Appointment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "donations")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer donationId;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    private int volumeMl;
    private LocalDateTime donationDate;
    private LocalDateTime nextEligibleDate;

    public void calculateNextEligibility() {
        this.nextEligibleDate = this.donationDate.plusMonths(3); // 3-month gap
    }
}
