package com.talentpath.Battleship.exceptions;

public class InvalidPlayerTurnException extends Exception {
    public InvalidPlayerTurnException(String message) {
        super(message);
    }

    public InvalidPlayerTurnException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
