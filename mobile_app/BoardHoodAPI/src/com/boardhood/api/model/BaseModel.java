package com.boardhood.api.model;

public abstract class BaseModel {
	protected boolean isDiskCacheData = false;
	
	public boolean isDiskCacheData() {
		return isDiskCacheData;
	}
	public void setDiskCacheData(boolean isDiskCacheData) {
		this.isDiskCacheData = isDiskCacheData;
	}
}
