package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Conversation;
import com.boardhood.api.model.User;
import com.boardhood.api.util.BHList;
import com.boardhood.mobile.list.ListContext;
import com.boardhood.mobile.utils.TaskLoader;

public class UserConversationsTask extends RestTask<ListContext<User>, Void, BHList<Conversation>> {
	public UserConversationsTask(BoardHood client, TaskLoader.TaskListener<BHList<Conversation>> listener) {
		super(client, listener);
	}
	
	public UserConversationsTask(BoardHood client, TaskLoader.TaskListener<BHList<Conversation>> listener, int type) {
		super(client, listener, type);
	}

	public UserConversationsTask(BoardHood client) {
		super(client);
	}

	@Override
	public BHList<Conversation> run(ListContext<User>... params) throws Exception {
		return client.listUserConversations(params[0].getObject().getName(), params[0].getPerPage(), params[0].getNextPage(getType()));
	}
}