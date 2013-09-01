package com.boardhood.mobile.widget;

import com.boardhood.mobile.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageTextButton extends LinearLayout {
	private LayoutInflater inflater;
    private LinearLayout buttonView;
    private ImageView buttonImage;
    private TextView buttonText;
    private boolean isPressed = false;

	public ImageTextButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        buttonView = (LinearLayout) inflater.inflate(R.layout.button_imagetext, null);
        addView(buttonView);
        
        buttonImage = (ImageView) buttonView.findViewById(R.id.imagetext_button_image);
        buttonText = (TextView) buttonView.findViewById(R.id.imagetext_button_text);
        
        int[] attrsArray = new int[] {
            android.R.attr.text,
            android.R.attr.src
        };
        
        TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
        buttonText.setText(ta.getText(0));
        
        ta = getContext().obtainStyledAttributes(attrs, R.styleable.ImageTextButton);
        buttonImage.setImageResource(ta.getResourceId(R.styleable.ImageTextButton_src, R.drawable.ic_launcher));
        
        // Act like a button
        setClickable(true);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean result = super.onTouchEvent(event);
		
		// Identify whether the button is being pressed or not
		if (event.getAction() == MotionEvent.ACTION_DOWN)
			isPressed = true;
		else
			isPressed = false;
		return result;
	}
	
	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		
		// pass the press state to its childs
		buttonImage.setPressed(isPressed);
		buttonText.setPressed(isPressed);
	}
	
	public void setPressed(boolean pressed) {
		super.setPressed(pressed);
		buttonImage.setPressed(pressed);
		buttonText.setPressed(pressed);
	}

}
