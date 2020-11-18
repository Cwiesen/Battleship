package com.talentpath.Battleship.exceptions;

public class InvalidPlacementException extends Exception {
    public InvalidPlacementException(String message) {
        super(message);
    }

    public InvalidPlacementException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
