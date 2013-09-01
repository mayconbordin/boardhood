package com.boardhood.mobile;

import android.app.Activity;
import android.content.SharedPreferences;

import com.boardhood.api.model.User;

public class CredentialManager {
	private static User authUser;
	
	public static User getAuthUser(Activity ctx) {
		if (authUser == null) {
			SharedPreferences settings = BoardHoodSettings.getInstance();
			
			if (settings.contains("username")) {
				authUser = new User();
				authUser.setName(settings.getString("username", null));
				authUser.setPassword(settings.getString("password", null));
			}
		}
		return authUser;
	}
	
	public static void setAuthUser(Activity ctx, User user) {
		setAuthUser(ctx, user.getName(), user.getPassword());
	}
	
	public static void setAuthUser(Activity ctx, String username, String password) {
		SharedPreferences.Editor editor = BoardHoodSettings.getEditor();
		editor.putString("username", username);
		editor.putString("password", password);
		editor.commit();
		
		authUser = new User();
		authUser.setName(username);
		authUser.setPassword(password);
	}
	
	public static void deleteAuthUser() {
		SharedPreferences.Editor editor = BoardHoodSettings.getEditor();
		editor.remove("username");
		editor.remove("password");
		editor.commit();
		
		authUser = null;
	}
}
