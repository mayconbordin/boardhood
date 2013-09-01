package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.User;
import com.boardhood.mobile.utils.TaskLoader;

public class UserTask extends RestTask<String, Void, User> {
	public UserTask(BoardHood client, TaskLoader.TaskListener<User> listener) {
		super(client, listener);
	}
	
	public UserTask(BoardHood client, TaskLoader.TaskListener<User> listener, int type) {
		super(client, listener, type);
	}

	public UserTask(BoardHood client) {
		super(client);
	}

	@Override
	public User run(String... params) throws Exception {
		return client.findUser(params[0]);
	}

}
