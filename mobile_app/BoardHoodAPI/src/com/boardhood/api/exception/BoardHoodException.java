package com.boardhood.api.exception;

public class BoardHoodException extends Exception {
	private static final long serialVersionUID = -5424615788806827844L;

	public BoardHoodException() {
		super();
	}

	public BoardHoodException(String message, Throwable cause) {
		super(message, cause);
	}

	public BoardHoodException(String message) {
		super(message);
	}

	public BoardHoodException(Throwable cause) {
		super(cause);
	}

}
