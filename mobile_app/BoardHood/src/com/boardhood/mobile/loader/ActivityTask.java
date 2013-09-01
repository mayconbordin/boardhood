package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Conversation;
import com.boardhood.api.util.BHList;
import com.boardhood.mobile.list.ListContext;
import com.boardhood.mobile.utils.TaskLoader;

public class ActivityTask extends RestTask<ListContext<Void>, Void, BHList<Conversation>> {
	public ActivityTask(BoardHood client, TaskLoader.TaskListener<BHList<Conversation>> listener) {
		super(client, listener);
	}
	
	public ActivityTask(BoardHood client, TaskLoader.TaskListener<BHList<Conversation>> listener, int type) {
		super(client, listener, type);
	}

	public ActivityTask(BoardHood client) {
		super(client);
	}

	@Override
	public BHList<Conversation> run(ListContext<Void>... params) throws Exception {
		return client.activity(params[0].getPerPage(), 
				params[0].getNextPage(getType()), params[0].getAfter());
	}

}
