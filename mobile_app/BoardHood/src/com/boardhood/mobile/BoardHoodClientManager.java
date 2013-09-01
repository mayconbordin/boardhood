package com.boardhood.mobile;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.User;
import com.boardhood.api.webservice.BoardHoodWebClient;

public class BoardHoodClientManager {
	//public static final String URL = "http://10.0.2.2:5000/";
	public static final String URL = "http://api.boardhood.com/";
	private static final String APP_KEY = "9c7dc77314ca22b8eec94440fa528157f8b8be03";
	private static BoardHood client;
	
	public static BoardHood getClient() {
		if (client == null) {
			client = new BoardHoodWebClient(URL);
		}
		return client;
	}
	
	public static void setClient(BoardHood c) {
		client = c;
		client.setApiKey(APP_KEY);
	}
	
	public static void setCredentials(String username, String password) {
		getClient().setCredentials(username, password);
	}
	
	public static void setCredentials(User user) {
		getClient().setCredentials(user.getName(), user.getPassword());
	}
}
