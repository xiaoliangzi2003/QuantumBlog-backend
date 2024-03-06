package org.example.quantumblog.exception;

import lombok.Getter;

/**
 * @author xiaol
 */
@Getter
public class UserLoginException extends RuntimeException {
    private final int statusCode;

    public UserLoginException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

}