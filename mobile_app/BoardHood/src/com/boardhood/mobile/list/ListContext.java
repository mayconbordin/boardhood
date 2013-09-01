package com.boardhood.mobile.list;

import java.util.Date;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Coordinates;

public class ListContext<T> {
	private T object;
	private Date after;
	private String order = BoardHood.ORDER_POPULAR;
	private int page = 1;
	private int perPage = 20;
	private int nextPage = 1;
	private int radius = 0;
	private Coordinates location;
	
	public ListContext() {}
	
	public ListContext(T object) {
		this.object = object;
	}
	
	public T getObject() {
		return object;
	}
	public void setObject(T object) {
		this.object = object;
	}

	public Date getAfter() {
		return after;
	}

	public void setAfter(Date after) {
		this.after = after;
	}

	public int getPage() {
		return page;
	}

	public int getNextPage() {
		return page + nextPage;
	}
	
	public int getNextPage(int type) {
		if (type != ListListener.LOAD_MORE) {
			return 1;
		}
		return page + nextPage;
	}
	
	public void setPage(int page) {
		this.page = page;
	}
	
	public void increasePage(int amount) {
		this.page += amount;
	}

	public int getPerPage() {
		return perPage;
	}

	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Coordinates getLocation() {
		return location;
	}

	public void setLocation(Coordinates location) {
		this.location = location;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}
}
