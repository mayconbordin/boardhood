package com.boardhood.mobile;

import android.content.Context;

import com.boardhood.api.webservice.BoardHoodWebClient;
import com.boardhood.mobile.cache.AbstractCache;
import com.boardhood.mobile.cache.StringCache;

public class BoardHoodWebCacheClient extends BoardHoodWebClient {
	private static final int CACHE_INIT_SIZE = 1;
	private static final int CACHE_THREADS = 10;
	
	private Context context;

	public BoardHoodWebCacheClient(Context context) {
		super();
		this.context = context;
		init();
	}

	public BoardHoodWebCacheClient(Context context, String baseUrl) {
		super(baseUrl);
		this.context = context.getApplicationContext();
		init();
	}
	
	public void init() {
		StringCache c = new StringCache(CACHE_INIT_SIZE, CACHE_THREADS);
		c.enableDiskCache(context, AbstractCache.DISK_CACHE_SDCARD, false);
		
		setCacheProvider(c);
	}
}
