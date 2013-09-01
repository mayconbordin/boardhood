package com.boardhood.api.util.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.message.BasicNameValuePair;

public class Request {
	private String resource;
	private Map<String, String> params;
    private Map<String, String> headers;
    private Map<String, ByteArrayBody> byteArrays;
    
    public Request(String resource) {
		this.resource = resource;
		params = new HashMap<String, String>();
        headers = new HashMap<String, String>();
        byteArrays = new HashMap<String, ByteArrayBody>();
	}

	public void addParam(String key, String value) {
        params.put(key, value);
    }
	
	public void addParam(String key, int value) {
		params.put(key, String.valueOf(value));
    }
	
	public void addParam(String key, double value) {
		params.put(key, String.valueOf(value));
    }
	
	public void addByteArray(String key, ByteArrayBody value) {
		byteArrays.put(key, value);
    }

    public void addHeader(String key, String value) {
    	params.put(key, value);
    }

	public String getResource() {
		return resource;
	}

	public Map<String, String> getParams() {
		return params;
	}
	
	public List<NameValuePair> getParamsList() {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for(Map.Entry<String, String> param : params.entrySet())
        	list.add(new BasicNameValuePair(param.getKey(), param.getValue()));
		return list;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
    
	public Map<String, ByteArrayBody> getByteArrays() {
		return byteArrays;
	}
}
