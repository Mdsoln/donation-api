package com.donorapi.hospital.entity;

import com.donorapi.donor.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "hospitals")
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hospitalId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private String hospitalName;
    private String hospitalAddress;
    private String hospitalCity;
    private Double latitude;
    private Double longitude;

}
