package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Interest;
import com.boardhood.api.util.BHList;
import com.boardhood.mobile.utils.TaskLoader;

public class InterestAutoCompleteTask extends RestTask<String, Void, BHList<Interest>> {
	public InterestAutoCompleteTask(BoardHood client, TaskLoader.TaskListener<BHList<Interest>> listener) {
		super(client, listener);
	}
	
	public InterestAutoCompleteTask(BoardHood client, TaskLoader.TaskListener<BHList<Interest>> listener, int type) {
		super(client, listener, type);
	}

	public InterestAutoCompleteTask(BoardHood client) {
		super(client);
	}

	@Override
	public BHList<Interest> run(String... params) throws Exception {
		if (params == null || params.length == 0 || params[0] == null || params[0].length() == 0)
			return client.listInterests(BoardHood.ORDER_POPULAR, 20, 1);
		else
			return client.searchInterests(params[0]);
	}

}