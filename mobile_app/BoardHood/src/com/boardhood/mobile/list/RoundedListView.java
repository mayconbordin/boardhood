package com.boardhood.mobile.list;

import com.boardhood.mobile.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class RoundedListView extends NormalListView {
	public RoundedListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RoundedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundedListView(Context context) {
		super(context);
	}

	public void init() {
		super.init();
		
		roundedTopView = headerView.findViewById(R.id.listview_rounded_top);
		roundedTopView.setVisibility(View.VISIBLE);
		
        roundedBottomView = footerView.findViewById(R.id.listview_rounded_bottom);
		roundedBottomView.setVisibility(View.VISIBLE);
	}
	
	public void loadProgressBarBackground() {
		bottomLoadingBar.setBackgroundResource(R.drawable.bottom_border_noradius_normal);
		
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bottomLoadingBar.getLayoutParams();
		params.setMargins(0, 0, 0, 0);		
		bottomLoadingBar.setLayoutParams(params);
		
		topLoadingBar.setBackgroundResource(R.drawable.top_border_noradius_normal);
		params = (LinearLayout.LayoutParams) topLoadingBar.getLayoutParams();
		params.setMargins(0, 0, 0, 0);		
		topLoadingBar.setLayoutParams(params);
	}
	
	public void startLoadingBottom() {
		super.startLoadingBottom();
		showRoundedEnds();
	}
	
	public void finishLoadingBottom() {
		super.finishLoadingBottom();
		
		if (noItems) {
			hideRoundedEnds();
		}
	}
	
	public void showNoItemsView() {
		super.showNoItemsView();
		hideRoundedEnds();
	}
	
	public void hideNoItemsView() {
		super.hideNoItemsView();
		showRoundedEnds();
	}
	
	public void showRoundedEnds() {
		roundedBottomView.setVisibility(View.VISIBLE);
		headerView.setVisibility(View.VISIBLE);
	}
	
	public void hideRoundedEnds() {
		roundedBottomView.setVisibility(View.GONE);
		headerView.setVisibility(View.GONE);
	}
}
