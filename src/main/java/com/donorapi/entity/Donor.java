package com.donorapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "donors")
public class Donor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer donorId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToMany(mappedBy = "donor", cascade = CascadeType.ALL)
    private List<Appointment> appointments;

    private String fullName;
    private String email;
    private String phone;
    private String bloodType;
    private double height;
    private double weight;
    private LocalDate birthDate;
    private String gender;
    private String image;
    private String address;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now().withNano(0);
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = LocalDateTime.now().withNano(0);
    }

}
