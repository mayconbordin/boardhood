package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.User;
import com.boardhood.mobile.utils.TaskLoader;

public class LoginTask extends RestTask<User, Void, Boolean> {
	public LoginTask(BoardHood client, TaskLoader.TaskListener<Boolean> listener) {
		super(client, listener);
	}
	
	public LoginTask(BoardHood client, TaskLoader.TaskListener<Boolean> listener, int type) {
		super(client, listener, type);
	}

	public LoginTask(BoardHood client) {
		super(client);
	}

	@Override
	public Boolean run(User... params) throws Exception {
		client.setCredentials(params[0].getName(), params[0].getPassword());
		return client.authenticateUserCredentials();
	}

}
