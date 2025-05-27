package com.donorapi.hospital.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the number of donations for a specific month.
 */
@NoArgsConstructor
@Getter
@Setter
public class MonthlyDonation {
    private String month;
    private Long count;

    public MonthlyDonation(String month, Long count) {
        this.month = month;
        this.count = count;
    }

}
