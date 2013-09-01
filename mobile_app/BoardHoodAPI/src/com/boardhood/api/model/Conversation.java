package com.boardhood.api.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Conversation extends BaseModel implements Serializable {
	private User user;
	private Interest interest;
	private String id;
	private String message;
	private Date createdAt;
	private Coordinates location;
	private Conversation parent;

	private Double distance;
	private int repliesCount;
		
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Interest getInterest() {
		return interest;
	}
	public void setInterest(Interest interest) {
		this.interest = interest;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public int getRepliesCount() {
		return repliesCount;
	}
	public void setRepliesCount(int replies) {
		this.repliesCount = replies;
	}
	public Coordinates getLocation() {
		return location;
	}
	public void setLocation(Coordinates location) {
		this.location = location;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Conversation getParent() {
		return parent;
	}
	public void setParent(Conversation parent) {
		this.parent = parent;
	}
}
