package com.donorapi.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper=true)
@Data
public class OverBookingException extends RuntimeException{
    private final String message;

}
