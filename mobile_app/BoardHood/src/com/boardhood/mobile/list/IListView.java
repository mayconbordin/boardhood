package com.boardhood.mobile.list;

public interface IListView {
	public void startLoadingBottom();
	public void finishLoadingBottom();
	public void startLoadingTop();
	public void finishLoadingTop();
	public void startLoadingMore();
	public void finishLoadingMore();
	public void showNoItemsView();
	public void hideNoItemsView();
}
