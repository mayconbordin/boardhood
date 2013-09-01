package com.boardhood.api.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.boardhood.api.util.rest.Request;
import com.boardhood.api.util.rest.Response;
import com.boardhood.api.util.rest.RestClient;

public class RestClientTest {
	private String url = "http://localhost:5000/";
	private RestClient client;
	
	@Before
	public void setUp() throws Exception {
		client = new RestClient(url);
	}

	@Test
	public void testGet() {
		Response response = null;
		try {
			response = client.get(new Request("interests/6"));
		} catch (Exception e) {
			fail(e.toString());
		}
		
		assertEquals(response.getCode(), 200);
		System.out.print(response.getContent());
	}

	@Test
	public void testPost() {
		Response response = null;
		try {
			Request r = new Request("interests/");
			r.addParam("name", "Test::" + System.currentTimeMillis());
			response = client.post(r);
		} catch (Exception e) {
			fail(e.toString());
		}
		
		assertEquals(response.getCode(), 201);
		System.out.print(response.getContent());
	}
}
