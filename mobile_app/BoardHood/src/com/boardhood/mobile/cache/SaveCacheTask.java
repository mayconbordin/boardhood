package com.boardhood.mobile.cache;

import android.util.Log;

import com.boardhood.mobile.utils.TaskLoader;

public class SaveCacheTask extends TaskLoader<Void, Void, Integer> {
	private static final int INTERVAL = 2 * 60 * 1000;
	private static final int RES_SUCCESS = 0;
	private static final int RES_ERROR = 1;
	private static final int RES_TOO_SOON = 2;
	
	private StringCache cache;
	
	public SaveCacheTask(StringCache cache) {
		super(new TaskListener<Integer>() {
			@Override
			public void onTaskSuccess(Integer result) {
				switch (result.intValue()) {
					case RES_SUCCESS:
						Log.i("SaveCacheTask", "String cache saved to DISK");
						break;
					case RES_ERROR:
						Log.i("SaveCacheTask", "Unable to save string cache to DISK");
						break;
					case RES_TOO_SOON:
						Log.i("SaveCacheTask", "Too soon to save string cache to DISK");
						break;
				}
			}
			
			@Override
			public void onTaskFailed(Exception error) {
				Log.e("SaveCacheTask", "Failed at saving string cache to disk");
				error.printStackTrace();
			}
			
			@Override
			public void onPreExecute() {
				Log.i("SaveCacheTask", "Saving string cache to disk");
			}
		});
		
		this.cache = cache;
	}

	@Override
	public Integer run(Void... params) throws Exception {
		if (System.currentTimeMillis() >= (cache.getLastSavedTime() + INTERVAL)) {
			if (cache.save()) {
				return RES_SUCCESS;
			} else {
				return RES_ERROR;
			}
		}
		
		long leftSecs = (cache.getLastSavedTime() + INTERVAL) - System.currentTimeMillis();
		Log.i("SaveCacheTask", "Too soon to save MEM cache to disk. "
				+ (leftSecs/1000) + " seconds left.");
		return RES_TOO_SOON;
	}
}
