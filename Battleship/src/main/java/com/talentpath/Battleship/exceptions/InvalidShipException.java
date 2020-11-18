package com.talentpath.Battleship.exceptions;

public class InvalidShipException extends Exception {
    public InvalidShipException(String message) {
        super(message);
    }

    public InvalidShipException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
