package com.donorapi.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper=true)
@Data
public class OverBookingException extends RuntimeException{
    private final String message;
    private final HttpStatus status;

    public OverBookingException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
