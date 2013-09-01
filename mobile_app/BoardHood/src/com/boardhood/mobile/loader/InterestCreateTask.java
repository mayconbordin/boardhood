package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Interest;
import com.boardhood.mobile.utils.TaskLoader;

public class InterestCreateTask extends RestTask<Interest, Void, Interest> {
	public InterestCreateTask(BoardHood client, TaskLoader.TaskListener<Interest> listener) {
		super(client, listener);
	}
	
	public InterestCreateTask(BoardHood client, TaskLoader.TaskListener<Interest> listener, int type) {
		super(client, listener, type);
	}

	public InterestCreateTask(BoardHood client) {
		super(client);
	}

	@Override
	public Interest run(Interest... params) throws Exception {
		return client.createInterest(params[0]);
	}

}
