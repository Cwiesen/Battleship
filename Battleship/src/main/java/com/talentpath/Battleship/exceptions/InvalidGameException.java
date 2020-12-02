package com.talentpath.Battleship.exceptions;

public class InvalidGameException extends Exception {
    public InvalidGameException(String message) {
        super(message);
    }

    public InvalidGameException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
