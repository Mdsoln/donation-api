package com.donorapi.hospital.entity;


import com.donorapi.exception.OverBookingException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "slots")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL)
    private List<Appointment> appointments;

    @Future
    private LocalDateTime startTime;
    private int maxCapacity = 10;  // Max donors (e.g., 5-10)
    private int currentBookings = 0;
    private LocalDateTime endTime;
    private boolean isBooked;

    public boolean isAvailable() {
        return currentBookings < maxCapacity;
    }

    public void addBooking() {
        if (isAvailable()) {
            this.currentBookings++;
        } else {
            throw new OverBookingException("Slot is fully booked!");
        }
    }

    public boolean isFull() {
        return currentBookings == maxCapacity;
    }
}
