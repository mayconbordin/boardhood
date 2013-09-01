package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Interest;
import com.boardhood.api.model.User;
import com.boardhood.api.util.BHList;
import com.boardhood.mobile.list.ListContext;
import com.boardhood.mobile.utils.TaskLoader;

public class UserInterestsTask extends RestTask<ListContext<User>, Void, BHList<Interest>> {
	public UserInterestsTask(BoardHood client, TaskLoader.TaskListener<BHList<Interest>> listener) {
		super(client, listener);
	}
	
	public UserInterestsTask(BoardHood client, TaskLoader.TaskListener<BHList<Interest>> listener, int type) {
		super(client, listener, type);
	}

	public UserInterestsTask(BoardHood client) {
		super(client);
	}

	@Override
	public BHList<Interest> run(ListContext<User>... params) throws Exception {
		return client.listUserInterests(params[0].getObject().getName(), params[0].getPerPage(), params[0].getNextPage(getType()));
	}

}