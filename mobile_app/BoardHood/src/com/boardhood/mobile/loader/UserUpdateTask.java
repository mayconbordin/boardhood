package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.User;
import com.boardhood.mobile.utils.TaskLoader;

public class UserUpdateTask extends RestTask<User, Void, User> {
	public UserUpdateTask(BoardHood client, TaskLoader.TaskListener<User> listener) {
		super(client, listener);
	}
	
	public UserUpdateTask(BoardHood client, TaskLoader.TaskListener<User> listener, int type) {
		super(client, listener, type);
	}

	public UserUpdateTask(BoardHood client) {
		super(client);
	}

	@Override
	public User run(User... params) throws Exception {
		return client.updateAuthUser(params[0]);
	}

}
