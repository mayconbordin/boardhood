package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Conversation;
import com.boardhood.api.util.BHList;
import com.boardhood.mobile.list.ListContext;
import com.boardhood.mobile.utils.TaskLoader;

public class ConversationRepliesTask extends RestTask<ListContext<Conversation>, Void, BHList<Conversation>> {
	public ConversationRepliesTask(BoardHood client, TaskLoader.TaskListener<BHList<Conversation>> listener) {
		super(client, listener);
	}
	
	public ConversationRepliesTask(BoardHood client, TaskLoader.TaskListener<BHList<Conversation>> listener, int type) {
		super(client, listener, type);
	}

	public ConversationRepliesTask(BoardHood client) {
		super(client);
	}

	@Override
	public BHList<Conversation> run(ListContext<Conversation>... params) throws Exception {
		return client.listConversationReplies(params[0].getObject(), params[0].getAfter(), 
				params[0].getPerPage(), params[0].getNextPage(getType()));
	}

}
