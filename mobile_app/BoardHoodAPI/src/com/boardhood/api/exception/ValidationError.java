package com.boardhood.api.exception;

public class ValidationError {
	public static final String UNKNOWN_ERROR = "unknown_error";
	public static final String ALREADY_EXISTS = "already_exists";
	public static final String MISSING_FIELD = "missing_field";
	public static final String EMPTY_FIELD = "empty_field";
	public static final String INVALID_VALUE = "invalid_value";
	public static final String INVALID_FILE_TYPE = "invalid_file_type";
	public static final String INVALID_TYPE = "invalid_type";
	public static final String BELOW_MIN_LENGTH = "below_min_length";
	public static final String ABOVE_MAX_LENGTH = "above_max_length";
	public static final String INVALID_FORMAT = "invalid_format";
	public static final String BELOW_MIN_VALUE = "below_min_value";
	public static final String ABOVE_MAX_VALUE = "above_max_value";
	public static final String INVALID_LENGTH = "invalid_length";
	
	private String reason;
	private String code;
	private String field;
	private String tip;
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getTip() {
		return tip;
	}
	public void setTip(String tip) {
		this.tip = tip;
	}
	
}
