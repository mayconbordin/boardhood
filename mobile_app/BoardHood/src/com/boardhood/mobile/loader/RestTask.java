package com.boardhood.mobile.loader;

import com.boardhood.api.BoardHood;
import com.boardhood.mobile.utils.TaskLoader;

public abstract class RestTask<ParamT, ProgressT, ReturnT> extends TaskLoader<ParamT, ProgressT, ReturnT> {
	protected BoardHood client;
	
	public RestTask(BoardHood client) {
		this.client = client;
	}
	
	public RestTask(BoardHood client, TaskLoader.TaskListener<ReturnT> listener) {
		super(listener);
		this.client = client;
	}
	
	public RestTask(BoardHood client, TaskLoader.TaskListener<ReturnT> listener, int type) {
		super(listener, type);
		this.client = client;
	}
}
