package com.talentpath.Battleship.exceptions;

public class InvalidPlayerException extends Exception {
    public InvalidPlayerException(String message) {
        super(message);
    }

    public InvalidPlayerException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
