package com.boardhood.mobile.widget;

import com.boardhood.mobile.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingButton extends LinearLayout {
	private LayoutInflater inflater;
    private LinearLayout view;
    
    private ProgressBar progressBar;
    private TextView textView;
    
	public LoadingButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LoadingButton(Context context) {
		super(context);
		init();
	}
	
	public void init() {
		setClickable(true);
		setGravity(Gravity.CENTER);
		setBackgroundResource(R.drawable.button_orange);
		
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = (LinearLayout) inflater.inflate(R.layout.button_loader, null);
        addView(view);
        
        textView = (TextView) findViewById(R.id.button_text);
        progressBar = (ProgressBar) findViewById(R.id.button_loader);
        hideLoader();
	}
	
	public void toOrange() {
		setBackgroundResource(R.drawable.button_orange);
	}
	
	public void toGray() {
		setBackgroundResource(R.drawable.button_gray);
	}

	public void toGreen() {
		setBackgroundResource(R.drawable.button_green);
	}
	
	public void showLoader() {
		progressBar.setVisibility(View.VISIBLE);
		textView.setVisibility(View.GONE);
	}
	
	public void hideLoader() {
		progressBar.setVisibility(View.GONE);
		textView.setVisibility(View.VISIBLE);
	}
	
	public void setText(String text) {
		textView.setText(text);
	}
}
