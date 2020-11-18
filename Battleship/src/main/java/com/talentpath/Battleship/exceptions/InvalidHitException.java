package com.talentpath.Battleship.exceptions;

public class InvalidHitException extends Exception {
    public InvalidHitException(String message) {
        super(message);
    }

    public InvalidHitException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
