package com.board.execption;

public class ActorNotFoundException extends RuntimeException{
	public ActorNotFoundException(String message) {
        super(message);
    }
}
