package com.talentpath.Battleship.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.BAD_REQUEST,
        reason = "User entered a null guess.")
public class NullGameException extends Exception {
    public NullGameException(String message) {
        super(message);
    }

    public NullGameException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
