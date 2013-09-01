package com.boardhood.mobile.widget;

import com.boardhood.mobile.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class LoadingBar extends LinearLayout {
	private LayoutInflater inflater;
	private LinearLayout view;
	private int layout = R.layout.loading_bar;
	
	public LoadingBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public LoadingBar(Context context, AttributeSet attrs, int layout) {
		super(context, attrs);
		this.layout = layout;
		init();
	}

	public LoadingBar(Context context) {
		super(context);
		init();
	}
	
	public LoadingBar(Context context, int layout) {
		super(context);
		this.layout = layout;
		init();
	}

	public void init() {
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		view = (LinearLayout) inflater.inflate(layout, null);
        addView(view);
        hide();
	}
	
	public void show() {
		setVisibility(View.VISIBLE);
	}
	
	public void hide() {
		setVisibility(View.GONE);
	}
}
