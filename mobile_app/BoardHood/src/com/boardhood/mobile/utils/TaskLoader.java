package com.boardhood.mobile.utils;

import android.os.AsyncTask;

public abstract class TaskLoader<ParamT, ProgressT, ReturnT> extends AsyncTask<ParamT, ProgressT, ReturnT> {
	private TaskListener<ReturnT> listener;
	private Exception error;
	private int type = -1;
	
	public TaskLoader() {}
	public TaskLoader(TaskListener<ReturnT> listener) {
		this.listener = listener;
		init();
	}
	public TaskLoader(TaskListener<ReturnT> listener, int type) {
		this.listener = listener;
		this.type = type;
		init();
	}
	
	private void init() {
		if (listener != null) {
			listener.setType(type);
		}
	}
	
	@Override
	protected final ReturnT doInBackground(ParamT... params) {
		ReturnT result = null;
        try {
            result = run(params);
        } catch (Exception e) {
            this.error = e;
        }
        return result;
	}
	
	@Override
	protected void onPostExecute(ReturnT result) {
		super.onPostExecute(result);
		
        if (listener != null) {
        	if (failed()) {
        		listener.onTaskFailed(error);
        	} else {
        		listener.onTaskSuccess(result);
        	}
        }
    }
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (listener != null)
			listener.onPreExecute();
	}
	
	public boolean failed() {
        return error != null;
    }
	
	public int getType() {
		return type;
	}
	
	public abstract ReturnT run(ParamT... params) throws Exception;

	public static abstract class TaskListener<ReturnT> {
		protected int type;
		
		public void setType(int type) {
			this.type = type;
		}
		
		public abstract void onPreExecute();
		public abstract void onTaskSuccess(ReturnT result);
		public abstract void onTaskFailed(Exception error);
	}
}
