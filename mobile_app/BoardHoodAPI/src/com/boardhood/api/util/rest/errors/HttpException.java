package com.boardhood.api.util.rest.errors;

@SuppressWarnings("serial")
public class HttpException extends Exception {
	public HttpException() {
		super();
	}

	public HttpException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpException(String message) {
		super(message);
	}

	public HttpException(Throwable cause) {
		super(cause);
	}
}
