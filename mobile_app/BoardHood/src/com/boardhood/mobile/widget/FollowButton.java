package com.boardhood.mobile.widget;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Interest;
import com.boardhood.mobile.R;
import com.boardhood.mobile.loader.InterestCreateTask;
import com.boardhood.mobile.loader.InterestFollowTask;
import com.boardhood.mobile.utils.TaskLoader;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FollowButton extends LinearLayout implements View.OnClickListener {
	private BoardHood client;
	private Interest interest;
	
	private LayoutInflater inflater;
    private LinearLayout view;
    
    private ProgressBar progressBar;
    private TextView textView;
    
	public FollowButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FollowButton(Context context) {
		super(context);
		init();
	}
	
	public void init() {
		setClickable(true);
		setGravity(Gravity.CENTER);
		setBackgroundResource(R.drawable.button_orange);
		
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = (LinearLayout) inflater.inflate(R.layout.button_wloader, null);
        addView(view);
        
        textView = (TextView) findViewById(R.id.button_text);
        progressBar = (ProgressBar) findViewById(R.id.button_loader);
        hideLoader();
        
		setOnClickListener(this);
	}

	public void setFollowing(boolean following) {
		String text;
		if (following) {
			toGreen();
			text = getContext().getString(R.string.following);
		} else {
			toOrange();
			text = getContext().getString(R.string.follow);
		}
		setText(text);
	}
	
	public void setup(Interest interest, BoardHood client) {
		this.interest = interest;
		this.client = client;
		setFollowing(interest.isLoggedUserFollows());
		
		if (interest.getId() == null) {
			setText(getContext().getString(R.string.create_and_follow));
		}
	}
	
	public void toOrange() {
		setBackgroundResource(R.drawable.button_orange);
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

	@Override
	public void onClick(View arg0) {
		if (interest.getId() == null) {
			new InterestCreateTask(client, new TaskLoader.TaskListener<Interest>() {
				@Override
				public void onPreExecute() {
					showLoader();
				}

				@Override
				public void onTaskSuccess(Interest result) {
					//interest = result;
					interest.setId(result.getId());
					new InterestFollowTask(client, followListener).execute(interest);
				}

				@Override
				public void onTaskFailed(Exception error) {
					Log.e("FollowButton", "Error: " + error.getMessage());
					Toast.makeText(getContext(), R.string.interest_create_error, Toast.LENGTH_SHORT).show();
					hideLoader();
				}
			}).execute(interest);
		}
		else
			new InterestFollowTask(client, followListener).execute(interest);
	}
	
	TaskLoader.TaskListener<Boolean> followListener = new TaskLoader.TaskListener<Boolean>() {
		@Override
		public void onPreExecute() {
			showLoader();
		}

		@Override
		public void onTaskSuccess(Boolean result) {
			Log.i("FollowButton", "Is following interest: " + interest.isLoggedUserFollows());
			hideLoader();
			setFollowing(result);
		}

		@Override
		public void onTaskFailed(Exception error) {
			Log.e("FollowButton", "Error: " + error.getMessage());
			Toast.makeText(getContext(), R.string.interest_follow_error, Toast.LENGTH_SHORT).show();
			hideLoader();
		}
	};
}
