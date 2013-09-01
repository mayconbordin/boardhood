package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Conversation;
import com.boardhood.api.util.BHList;
import com.boardhood.mobile.list.ListContext;
import com.boardhood.mobile.utils.TaskLoader;

public class FeedTask extends RestTask<ListContext<Void>, Void, BHList<Conversation>> {
	public FeedTask(BoardHood client, TaskLoader.TaskListener<BHList<Conversation>> listener) {
		super(client, listener);
	}
	
	public FeedTask(BoardHood client, TaskLoader.TaskListener<BHList<Conversation>> listener, int type) {
		super(client, listener, type);
	}

	public FeedTask(BoardHood client) {
		super(client);
	}

	@Override
	public BHList<Conversation> run(ListContext<Void>... params) throws Exception {
		ListContext<Void> ctx = params[0];
		return client.listConversationFeed(ctx.getOrder(), ctx.getPerPage(), 
				ctx.getNextPage(getType()), ctx.getAfter(), ctx.getLocation(), ctx.getRadius());
	}

}
