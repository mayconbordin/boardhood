package com.boardhood.api.util.rest.errors;

@SuppressWarnings("serial")
public class UnauthorizedException extends HttpException {
	public UnauthorizedException() {
		super("401 Unauthorized");
	}

	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException(Throwable cause) {
		super(cause);
	}
}
