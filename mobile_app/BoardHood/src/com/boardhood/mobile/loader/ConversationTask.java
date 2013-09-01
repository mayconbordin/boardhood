package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Conversation;
import com.boardhood.mobile.utils.TaskLoader;

public class ConversationTask extends RestTask<Conversation, Void, Conversation> {
	public ConversationTask(BoardHood client, TaskLoader.TaskListener<Conversation> listener) {
		super(client, listener);
	}
	
	public ConversationTask(BoardHood client, TaskLoader.TaskListener<Conversation> listener, int type) {
		super(client, listener, type);
	}

	public ConversationTask(BoardHood client) {
		super(client);
	}

	@Override
	public Conversation run(Conversation... params) throws Exception {
		return client.findConversation(
				params[0].getInterest().getId(),
				params[0].getId());
	}

}
