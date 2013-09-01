package com.boardhood.mobile.widget;

import com.boardhood.mobile.R;
import com.boardhood.mobile.activity.FeedActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class ActionBar extends LinearLayout {

    private LayoutInflater inflater;
    private LinearLayout barView;
    private LinearLayout filterActionView;
    private LinearLayout refreshActionView;
    private LinearLayout logoutActionView;
    
    private ProgressBar refreshActionProgressBar;
    private ImageView refreshActionIcon;
    private ImageView logoImageView;

	public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        barView = (LinearLayout) inflater.inflate(R.layout.actionbar, null);
        addView(barView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        
        filterActionView = (LinearLayout) barView.findViewById(R.id.actionbar_filter);
        refreshActionView = (LinearLayout) barView.findViewById(R.id.actionbar_refresh);
        logoutActionView = (LinearLayout) barView.findViewById(R.id.actionbar_logout);
        refreshActionProgressBar = (ProgressBar) barView.findViewById(R.id.actionbar_refresh_pb);
        refreshActionIcon = (ImageView) barView.findViewById(R.id.actionbar_refresh_icon);
        
        logoImageView = (ImageView) barView.findViewById(R.id.actionbar_logo);
        logoImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getContext().startActivity(FeedActivity.createIntent(getContext()));
			}
		});
    }
    
    public void setFilterAction(OnClickListener filterAction) {
    	filterActionView.setVisibility(View.VISIBLE);
    	filterActionView.setOnClickListener(filterAction);
    }
    
    public void setOnRefreshAction(OnClickListener refreshAction) {
    	refreshActionView.setVisibility(View.VISIBLE);
    	refreshActionView.setOnClickListener(refreshAction);
    }
    
    public void setOnLogoutAction(OnClickListener refreshAction) {
    	logoutActionView.setVisibility(View.VISIBLE);
    	logoutActionView.setOnClickListener(refreshAction);
    }
    
    public void showProgressBar() {
    	refreshActionIcon.setVisibility(View.GONE);
    	refreshActionProgressBar.setVisibility(View.VISIBLE);
    }
    
    public void hideProgressBar() {
    	refreshActionIcon.setVisibility(View.VISIBLE);
    	refreshActionProgressBar.setVisibility(View.GONE);
    }
}
