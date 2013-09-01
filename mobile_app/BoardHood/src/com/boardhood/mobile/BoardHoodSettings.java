package com.boardhood.mobile;

import android.content.Context;
import android.content.SharedPreferences;

public class BoardHoodSettings {
	public static final String SETTINGS = "BoardHoodSettings";
	
	// settings
	public static final String FILTER_ORDER_BY = "filterOrderBy";
	public static final String FILTER_RADIUS = "filterRadius";
	
	private static SharedPreferences settings;
	
	public static SharedPreferences getInstance() {
		return settings;
	}
	
	public static SharedPreferences.Editor getEditor() {
		if (settings == null) {
			return null;
		}
		return settings.edit();
	}
	
	public static void setup(Context ctx) {
		settings = ctx.getApplicationContext().getSharedPreferences(SETTINGS, 0);
	}
	
	public static void clear() {
		if (settings == null) {
			return;
		}
		
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}
}
