package com.boardhood.mobile;

import com.boardhood.mobile.cache.StringCache;

import android.app.Application;

public class BoardHoodApplication extends Application {
	public static final String ACTION_LOGOUT = "com.boardhood.mobile.ACTION_LOGOUT";
	public StringCache cache;
	
	@Override
    public void onCreate() {
        super.onCreate();
        
        BoardHoodSettings.setup(this);

		BoardHoodClientManager.setClient(
        		new BoardHoodWebCacheClient(this, BoardHoodClientManager.URL));
		
		cache = (StringCache) BoardHoodClientManager.getClient().getCacheProvider();
		
		LocationFinder.startInstance(this);
    }
}