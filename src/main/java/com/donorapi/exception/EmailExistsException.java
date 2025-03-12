package com.donorapi.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class EmailExistsException extends RuntimeException {
    private final String errorCode;
    private final String message;

    public EmailExistsException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }
}
