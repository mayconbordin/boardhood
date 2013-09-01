package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.User;
import com.boardhood.mobile.utils.TaskLoader;

public class AuthUserTask extends RestTask<Void, Void, User> {
	public AuthUserTask(BoardHood client, TaskLoader.TaskListener<User> listener) {
		super(client, listener);
	}
	
	public AuthUserTask(BoardHood client, TaskLoader.TaskListener<User> listener, int type) {
		super(client, listener, type);
	}

	public AuthUserTask(BoardHood client) {
		super(client);
	}

	@Override
	public User run(Void... params) throws Exception {
		return client.getAuthUser();
	}

}
