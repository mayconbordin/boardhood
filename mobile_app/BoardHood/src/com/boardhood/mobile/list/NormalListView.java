package com.boardhood.mobile.list;

import com.boardhood.mobile.R;
import com.boardhood.mobile.widget.LoadingBar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class NormalListView extends ListView implements IListView {
	protected LayoutInflater inflater;
	protected View headerView;
	protected View footerView;
	protected View roundedTopView;
	protected View roundedBottomView;
	protected LinearLayout noItemsView;
	protected TextView noItemsTextView;
	protected Button loadMoreButton;
	protected LoadingBar topLoadingBar;
	protected LoadingBar bottomLoadingBar;
	protected View.OnClickListener listener;
	
	protected int headerId = R.layout.listview_normal_header;
	protected int footerId = R.layout.listview_normal_footer;
	
	protected boolean noItems = false;
	protected boolean loadMore = false;
		
	public NormalListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public NormalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NormalListView(Context context) {
		super(context);
		init();
	}

	public void init() {
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerView = inflater.inflate(headerId, null, false);
        footerView = inflater.inflate(footerId, null, false);
        
        addHeaderView(headerView, null, false);
        addFooterView(footerView, null, false);
                
        noItemsView = (LinearLayout) footerView.findViewById(R.id.listview_noitems);
        noItemsTextView = (TextView) noItemsView.findViewById(R.id.listview_noitems_text);                   
        loadMoreButton = (Button) footerView.findViewById(R.id.listview_loadmore);
        topLoadingBar = (LoadingBar) headerView.findViewById(R.id.listview_loading_bar_top);
        bottomLoadingBar = (LoadingBar) footerView.findViewById(R.id.listview_loading_bar_bottom);
        
        loadProgressBarBackground();
        
        loadMoreButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadMoreButton.setEnabled(false);
				loadMoreButton.setText(R.string.loaderlistview_updating);
				
				if (listener != null)
					listener.onClick(v);
			}
        });
	}
	
	public void loadProgressBarBackground() {
		topLoadingBar.setBackgroundResource(R.drawable.conversation_item_background);
		bottomLoadingBar.setBackgroundResource(R.drawable.conversation_item_background);
	}
	
	public void setOnLoadMoreListener(View.OnClickListener listener) {
		this.listener = listener;
		loadMore = true;
		//loadMoreButton.setVisibility(View.VISIBLE);
	}
	
	public void startLoadingBottom() {
		bottomLoadingBar.setVisibility(View.VISIBLE);
		
		if (noItems) {
			noItemsView.setVisibility(View.GONE);
		}
	}
	
	public void finishLoadingBottom() {
		bottomLoadingBar.setVisibility(View.GONE);
		
		if (noItems) {
			noItemsView.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void startLoadingTop() {
		topLoadingBar.setVisibility(View.VISIBLE);
		
		if (noItems) {
			noItemsView.setVisibility(View.GONE);
		}
	}

	@Override
	public void finishLoadingTop() {
		topLoadingBar.setVisibility(View.GONE);
		
		if (noItems) {
			noItemsView.setVisibility(View.VISIBLE);
		}
	}
	
	public View setNoItemsView(int resId) {
		View v = inflater.inflate(resId, null, false);
		setNoItemsView(v);
		return v;
	}
	
	public void setNoItemsView(View v) {
		noItemsView.removeAllViews();
		noItemsView.addView(v);
	}
	
	public void setNoItemsText(String text) {
		noItemsTextView.setText(text);
	}
	
	public void setNoItemsText(int stringId) {
		noItemsTextView.setText(stringId);
	}
	
	public void showNoItemsView() {
		noItems = true;
		noItemsView.setVisibility(View.VISIBLE);
		
		if (loadMore) {
			loadMoreButton.setVisibility(View.GONE);
		}
	}
	
	public void hideNoItemsView() {
		noItems = false;
		noItemsView.setVisibility(View.GONE);
		
		if (loadMore) {
			loadMoreButton.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void startLoadingMore() {
		
	}

	@Override
	public void finishLoadingMore() {
		loadMoreButton.setEnabled(true);
		loadMoreButton.setText(R.string.loaderlistview_update);
	}
}