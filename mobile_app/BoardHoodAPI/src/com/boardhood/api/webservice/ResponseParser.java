package com.boardhood.api.webservice;

import java.util.Date;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.boardhood.api.exception.ValidationError;
import com.boardhood.api.model.Conversation;
import com.boardhood.api.model.Coordinates;
import com.boardhood.api.model.Interest;
import com.boardhood.api.model.User;
import com.boardhood.api.util.BHArrayList;
import com.boardhood.api.util.BHList;

public class ResponseParser {
	private Date parseIsoDate(String strDate) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeNoMillis();
		return parser.parseDateTime(strDate).toDate();
	}
	
	public Interest parseInterest(JSONObject json) throws JSONException {
		return parseInterest(json, false);
	}
	
	public Interest parseInterest(JSONObject json, boolean fromCache) throws JSONException {
		Interest i = new Interest();
		i.setDiskCacheData(fromCache);
		
		if (json.has("id"))
			i.setId(json.getString("id"));
		if (json.has("name"))
			i.setName(json.getString("name"));
		if (json.has("followers_count"))
			i.setFollowersCount(json.getInt("followers_count"));
		if (json.has("conversations_count"))
			i.setConversationsCount(json.getInt("conversations_count"));
		if (json.has("logged_user_follows"))
			i.setLoggedUserFollows(json.getBoolean("logged_user_follows"));
		
		return i;
	}
	
	public User parseUser(JSONObject json) throws JSONException {
		return parseUser(json, false);
	}
	
	public User parseUser(JSONObject json, boolean fromCache) throws JSONException {
		User u = new User();
		u.setDiskCacheData(fromCache);
		
		if (json.has("name"))
			u.setName(json.getString("name"));
		if (json.has("email"))
			u.setEmail(json.getString("email"));
		if (json.has("avatar_url"))
			u.setAvatar(json.getString("avatar_url"));
		if (json.has("interests_count"))
			u.setInterestsCount(json.getInt("interests_count"));
		if (json.has("conversations_count"))
			u.setConversationsCount(json.getInt("conversations_count"));
		
		return u;
	}
	
	public ValidationError parseValidationError(JSONObject json) throws JSONException {
		ValidationError error = new ValidationError();
		
		if (json.has("reason"))
			error.setReason(json.getString("reason"));
		if (json.has("field"))
			error.setField(json.getString("field"));
		if (json.has("code"))
			error.setCode(json.getString("code"));
		if (json.has("tip"))
			error.setTip(json.getString("tip"));
		
		return error;
	}
	
	public Conversation parseConversation(JSONObject json) throws JSONException {
		return parseConversation(json, false);
	}
	
	public Conversation parseConversation(JSONObject json, boolean fromCache) throws JSONException {
		Conversation c = new Conversation();
		c.setDiskCacheData(fromCache);
		
		if (json.has("author")) {
			JSONObject user = json.getJSONObject("author");
			c.setUser(parseUser(user));
		}
		
		if (json.has("interest")) {
			JSONObject interest = json.getJSONObject("interest");
			c.setInterest(parseInterest(interest));
		}
		
		if (json.has("parent")) {
			JSONObject parent = json.getJSONObject("parent");
			c.setParent(parseConversation(parent));
		}
		
		if (json.has("id"))
			c.setId(json.getString("id"));
		if (json.has("message"))
			c.setMessage(json.getString("message"));
		if (json.has("lat") && json.has("lon"))
			c.setLocation(new Coordinates(json.getDouble("lat"), json.getDouble("lon")));
		if (json.has("created_at"))
			c.setCreatedAt(parseIsoDate(json.getString("created_at")));
		if (json.has("replies_count"))
			c.setRepliesCount(json.getInt("replies_count"));
		if (json.has("distance"))
			c.setDistance(json.getDouble("distance"));
		
		return c;
	}
	
	@SuppressWarnings("rawtypes")
	public void parseListMetadata(JSONObject json, BHList list) throws JSONException {
		if (json.has("created_at")) {
			list.setCreatedAt(parseIsoDate(json.getString("created_at")));
		}
		
		if (json.has("total")) {
			list.setTotal(json.getInt("total"));
		}
	}
	
	public BHList<Interest> parseUserInterests(JSONObject json) throws JSONException {
		return parseUserInterests(json, false);
	}
	
	public BHList<Interest> parseUserInterests(JSONObject json, boolean fromCache) throws JSONException {
		if (json == null) return null;
		
		BHList<Interest> interests = parseInterests(json, fromCache);
		
		for (int i=0; i<interests.size(); i++)
			interests.get(i).setLoggedUserFollows(true);
		
		return interests;
	}
	
	public BHList<Interest> parseInterests(JSONObject json) throws JSONException {
		return parseInterests(json, false);
	}
	
	public BHList<Interest> parseInterests(JSONObject json, boolean fromCache) throws JSONException {
		if (json == null) return null;
		
		BHList<Interest> interests = new BHArrayList<Interest>();
		interests.setDiskCacheData(fromCache);
		
		if (json.has("interests")) {
			JSONArray objs = json.getJSONArray("interests");
			for (int i=0; i < objs.length(); i++)
				interests.add(parseInterest(objs.getJSONObject(i), fromCache));
		}
		
		parseListMetadata(json, interests);
		
		return interests;
	}
	
	public BHList<Conversation> parseConversations(String key, JSONObject json) throws JSONException {
		return parseConversations(key, json, false);
	}
	
	public BHList<Conversation> parseConversations(String key, JSONObject json, boolean fromCache) throws JSONException {
		if (json == null) return null;
		
		BHList<Conversation> conversations = new BHArrayList<Conversation>();
		conversations.setDiskCacheData(fromCache);
		
		if (json.has(key)) {
			JSONArray objs = json.getJSONArray(key);
			for (int i=0; i < objs.length(); i++)
				conversations.add(parseConversation(objs.getJSONObject(i), fromCache));
		}
		
		parseListMetadata(json, conversations);
		
		return conversations;
	}
	
	public BHList<User> parseUsers(String key, JSONObject json) throws JSONException {
		return parseUsers(key, json, false);
	}
	
	public BHList<User> parseUsers(String key, JSONObject json, boolean fromCache) throws JSONException {
		if (json == null) return null;
		
		BHList<User> users = new BHArrayList<User>();
		users.setDiskCacheData(fromCache);
		
		if (json.has(key)) {
			JSONArray objs = json.getJSONArray(key);
			for (int i=0; i < objs.length(); i++)
				users.add(parseUser(objs.getJSONObject(i), fromCache));
		}
		
		parseListMetadata(json, users);
		
		return users;
	}
	
	public BHList<ValidationError> parseValidationErrors(String key, JSONObject json) throws JSONException {
		if (json == null) return null;
		
		BHList<ValidationError> errors = new BHArrayList<ValidationError>();
		
		if (json.has(key)) {
			JSONArray objs = json.getJSONArray(key);
			for (int i=0; i < objs.length(); i++)
				errors.add(parseValidationError(objs.getJSONObject(i)));
		}
		
		return errors;
	}
}
