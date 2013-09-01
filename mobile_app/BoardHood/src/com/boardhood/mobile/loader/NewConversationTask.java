package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Conversation;
import com.boardhood.mobile.utils.TaskLoader;

public class NewConversationTask extends RestTask<Conversation, Void, Conversation> {
	public NewConversationTask(BoardHood client, TaskLoader.TaskListener<Conversation> listener) {
		super(client, listener);
	}
	
	public NewConversationTask(BoardHood client, TaskLoader.TaskListener<Conversation> listener, int type) {
		super(client, listener, type);
	}

	public NewConversationTask(BoardHood client) {
		super(client);
	}

	@Override
	public Conversation run(Conversation... params) throws Exception {
		if (params[0].getParent() == null)
			return client.createConversation(params[0]);
		else
			return client.replyToConversation(params[0]);
	}

}
