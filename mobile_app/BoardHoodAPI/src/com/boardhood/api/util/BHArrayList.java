package com.boardhood.api.util;

import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings("serial")
public class BHArrayList<T> extends ArrayList<T> implements BHList<T> {
	private Date createdAt;
	private int total;
	private boolean isDiskCacheData = false;
	
	@Override
	public Date getCreatedAt() {
		return createdAt;
	}

	@Override
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public int getTotal() {
		return total;
	}

	@Override
	public void setTotal(int total) {
		this.total = total;
	}

	@Override
	public boolean isDiskCacheData() {
		return isDiskCacheData;
	}

	@Override
	public void setDiskCacheData(boolean isDiskCacheData) {
		this.isDiskCacheData = isDiskCacheData;
	}
}
