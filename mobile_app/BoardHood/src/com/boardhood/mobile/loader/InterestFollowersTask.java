package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Interest;
import com.boardhood.api.model.User;
import com.boardhood.api.util.BHList;
import com.boardhood.mobile.list.ListContext;
import com.boardhood.mobile.utils.TaskLoader;

public class InterestFollowersTask extends RestTask<ListContext<Interest>, Void, BHList<User>> {
	public InterestFollowersTask(BoardHood client, TaskLoader.TaskListener<BHList<User>> listener) {
		super(client, listener);
	}
	
	public InterestFollowersTask(BoardHood client, TaskLoader.TaskListener<BHList<User>> listener, int type) {
		super(client, listener, type);
	}

	public InterestFollowersTask(BoardHood client) {
		super(client);
	}

	@Override
	public BHList<User> run(ListContext<Interest>... params) throws Exception {
		return client.getInterestFollowers(params[0].getObject().getId(), params[0].getPerPage(), params[0].getNextPage(getType()));
	}

}
