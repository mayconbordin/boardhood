package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Interest;
import com.boardhood.mobile.utils.TaskLoader;

public class InterestTask extends RestTask<Interest, Void, Interest> {
	public InterestTask(BoardHood client, TaskLoader.TaskListener<Interest> listener) {
		super(client, listener);
	}
	
	public InterestTask(BoardHood client, TaskLoader.TaskListener<Interest> listener, int type) {
		super(client, listener, type);
	}

	public InterestTask(BoardHood client) {
		super(client);
	}

	@Override
	public Interest run(Interest... params) throws Exception {
		return client.findInterest(params[0].getId());
	}

}
