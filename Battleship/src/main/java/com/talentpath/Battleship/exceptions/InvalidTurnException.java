package com.talentpath.Battleship.exceptions;

public class InvalidTurnException extends Exception {
    public InvalidTurnException(String message) {
        super(message);
    }

    public InvalidTurnException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
