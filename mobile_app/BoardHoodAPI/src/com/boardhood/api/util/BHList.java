package com.boardhood.api.util;

import java.util.Date;
import java.util.List;


public interface BHList<T> extends List<T> {
	public Date getCreatedAt();
	public void setCreatedAt(Date createdAt);
	public int getTotal();
	public void setTotal(int total);
	public boolean isDiskCacheData();
	public void setDiskCacheData(boolean isDiskCacheData);
}
