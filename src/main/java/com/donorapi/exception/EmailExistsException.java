package com.donorapi.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class EmailExistsException extends RuntimeException {
    private final String errorCode;
}
