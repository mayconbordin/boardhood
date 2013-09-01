package com.boardhood.mobile.activity.base;

import com.boardhood.api.BoardHood;
import com.boardhood.mobile.BoardHoodApplication;
import com.boardhood.mobile.BoardHoodClientManager;
import com.boardhood.mobile.LocationFinder;
import com.boardhood.mobile.cache.SaveCacheTask;
import com.boardhood.mobile.cache.StringCache;
import com.boardhood.mobile.widget.ActionBar;
import com.boardhood.mobile.widget.FooterBar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public abstract class BoardHoodActivity extends Activity {
	protected ActionBar actionBar;
	protected FooterBar footerBar;
	protected BoardHood client;
	protected StringCache cache;
	protected BroadcastReceiver logoutBroadcastReceiver;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        client = BoardHoodClientManager.getClient();
        LocationFinder.getInstance().updateLocation();
        
        cache = ((BoardHoodApplication)getApplication()).cache;
        new SaveCacheTask(cache).execute();
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		
		new SaveCacheTask(cache).execute();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (logoutBroadcastReceiver != null) {
			unregisterReceiver(logoutBroadcastReceiver);
		}
	}

	protected void registerLogoutReceiver() {
		IntentFilter intentFilter = new IntentFilter();
	    intentFilter.addAction(BoardHoodApplication.ACTION_LOGOUT);
	    logoutBroadcastReceiver = new BroadcastReceiver() {
	    	@Override
	        public void onReceive(Context context, Intent intent) {
	            finish();
	        }
	    };
	    
	    registerReceiver(logoutBroadcastReceiver, intentFilter);
	}
}
