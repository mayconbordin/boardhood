package com.boardhood.mobile.widget;

import com.boardhood.mobile.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;

public class Button extends android.widget.TextView {

	public Button(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Button(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Button(Context context) {
		super(context);
		init();
	}
	
	public void init() {
		setClickable(true);
		setGravity(Gravity.CENTER);
		setTextColor(Color.WHITE);
		setBackgroundResource(R.drawable.button_orange);
	}
	
	public void toOrange() {
		setBackgroundResource(R.drawable.button_orange);
	}

	public void toGreen() {
		setBackgroundResource(R.drawable.button_green);
	}
	
	public void toGray() {
		setBackgroundResource(R.drawable.button_gray);
	}
}
