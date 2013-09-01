package com.boardhood.api.util.rest;

import org.json.JSONException;
import org.json.JSONObject;

public class Response {
	private String url;
	private int code;
	private String message;
	private String content;
    
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int responseCode) {
		this.code = responseCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String response) {
		this.content = response;
	}
	public JSONObject getJSONContent() throws JSONException {
		return new JSONObject(content);
	}
	
	public String getAuthToken() throws JSONException {
		return getJSONContent().getString("token");
	}
}