package com.talentpath.Battleship.exceptions;

public class InvalidBoardException extends Exception {
    public InvalidBoardException(String message) {
        super(message);
    }

    public InvalidBoardException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
