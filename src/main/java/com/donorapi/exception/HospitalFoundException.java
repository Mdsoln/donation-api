package com.donorapi.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class HospitalFoundException extends RuntimeException {
    private final String errorMessage;
}
