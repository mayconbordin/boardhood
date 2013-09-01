package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Interest;
import com.boardhood.mobile.utils.TaskLoader;

public class InterestFollowTask extends RestTask<Interest, Void, Boolean> {
	public InterestFollowTask(BoardHood client, TaskLoader.TaskListener<Boolean> listener) {
		super(client, listener);
	}
	
	public InterestFollowTask(BoardHood client, TaskLoader.TaskListener<Boolean> listener, int type) {
		super(client, listener, type);
	}

	public InterestFollowTask(BoardHood client) {
		super(client);
	}

	@Override
	public Boolean run(Interest... params) throws Exception {
		boolean result;
		if (params[0].isLoggedUserFollows()) {
			result = client.unfollowInterest(params[0].getId());
			if (result) params[0].setLoggedUserFollows(false);
		} else {
			result = client.followInterest(params[0].getId());
			if (result) params[0].setLoggedUserFollows(true);
		}
		
		if (!result)
			throw new Exception("Unable to follow/unfollow interest");
		
		return params[0].isLoggedUserFollows();
	}

}
