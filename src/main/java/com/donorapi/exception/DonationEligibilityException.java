package com.donorapi.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class DonationEligibilityException extends RuntimeException {
    public DonationEligibilityException(String message) {
        super(message);
    }
}
