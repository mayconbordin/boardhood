package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.User;
import com.boardhood.mobile.utils.TaskLoader;

public class UserCreateTask extends RestTask<User, Void, User> {
	public UserCreateTask(BoardHood client, TaskLoader.TaskListener<User> listener) {
		super(client, listener);
	}
	
	public UserCreateTask(BoardHood client, TaskLoader.TaskListener<User> listener, int type) {
		super(client, listener, type);
	}

	public UserCreateTask(BoardHood client) {
		super(client);
	}

	@Override
	public User run(User... params) throws Exception {
		return client.createUser(params[0]);
	}

}
