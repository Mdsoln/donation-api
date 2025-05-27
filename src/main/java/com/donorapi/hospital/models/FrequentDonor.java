package com.donorapi.hospital.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a frequent donor with their fullname, blood type, total donations, and profile name.
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class FrequentDonor {
    private Integer id;
    private String fullName;
    private String bloodType;
    private long totalDonations;
    private String profileName;
}