package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Conversation;
import com.boardhood.api.model.Interest;
import com.boardhood.api.util.BHList;
import com.boardhood.mobile.list.ListContext;
import com.boardhood.mobile.utils.TaskLoader;

public class InterestConversationsTask extends RestTask<ListContext<Interest>, Void, BHList<Conversation>> {
	public InterestConversationsTask(BoardHood client, TaskLoader.TaskListener<BHList<Conversation>> listener) {
		super(client, listener);
	}
	
	public InterestConversationsTask(BoardHood client, TaskLoader.TaskListener<BHList<Conversation>> listener, int type) {
		super(client, listener, type);
	}

	public InterestConversationsTask(BoardHood client) {
		super(client);
	}

	@Override
	public BHList<Conversation> run(ListContext<Interest>... params) throws Exception {
		return client.listConversations(params[0].getObject().getId(), BoardHood.ORDER_RECENT, params[0].getPerPage(), params[0].getNextPage(getType()));
	}

}
