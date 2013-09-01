package com.boardhood.api.model;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class User extends BaseModel implements Serializable {
	private String name;
	private String email;
	private String avatar;
	private String password;	
	private List<Interest> interests;
	private int interestsCount;
	private int conversationsCount;
	
	private byte[] newAvatar;
		
	public User(){}
	public User(String name) {
		super();
		this.name = name;
	}
	public User(String name, String avatar) {
		super();
		this.name = name;
		this.avatar = avatar;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public List<Interest> getInterests() {
		return interests;
	}
	public void setInterests(List<Interest> interests) {
		this.interests = interests;
	}
	public int getInterestsCount() {
		return interestsCount;
	}
	public void setInterestsCount(int interestsCount) {
		this.interestsCount = interestsCount;
	}
	public int getConversationsCount() {
		return conversationsCount;
	}
	public void setConversationsCount(int conversationsCount) {
		this.conversationsCount = conversationsCount;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public byte[] getNewAvatar() {
		return newAvatar;
	}
	public void setNewAvatar(byte[] newAvatar) {
		this.newAvatar = newAvatar;
	}
}
