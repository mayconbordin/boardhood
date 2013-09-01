package com.boardhood.api.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Interest extends BaseModel implements Serializable {
	private String id;
	private String name;
	private int followersCount;
	private int conversationsCount;
	private boolean loggedUserFollows;
		
	public Interest(){}
	public Interest(String name) {
		this.name = name;
	}
	public Interest(String id, String name) {
		this.id = id;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getFollowersCount() {
		return followersCount;
	}
	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}
	public int getConversationsCount() {
		return conversationsCount;
	}
	public void setConversationsCount(int conversationsCount) {
		this.conversationsCount = conversationsCount;
	}
	public boolean isLoggedUserFollows() {
		return loggedUserFollows;
	}
	public void setLoggedUserFollows(boolean loggedUserFollows) {
		this.loggedUserFollows = loggedUserFollows;
	}
}
