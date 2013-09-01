package com.boardhood.api.exception;

import com.boardhood.api.util.BHList;

@SuppressWarnings("serial")
public class ValidationException extends BoardHoodException {
	private BHList<ValidationError> errors;
	
	public ValidationException(BHList<ValidationError> errors) {
		this.errors = errors;
	}

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(Throwable cause) {
		super(cause);
	}

	public BHList<ValidationError> getErrors() {
		return errors;
	}
}
