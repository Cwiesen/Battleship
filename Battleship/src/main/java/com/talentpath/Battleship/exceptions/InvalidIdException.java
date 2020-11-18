package com.talentpath.Battleship.exceptions;

public class InvalidIdException extends Exception {
    public InvalidIdException(String message) {
        super(message);
    }

    public InvalidIdException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
