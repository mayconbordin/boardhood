package com.boardhood.mobile.widget;

import com.boardhood.api.model.Conversation;
import com.boardhood.api.model.Interest;
import com.boardhood.mobile.Extra;
import com.boardhood.mobile.R;
import com.boardhood.mobile.activity.ActivityActivity;
import com.boardhood.mobile.activity.ExploreActivity;
import com.boardhood.mobile.activity.FeedActivity;
import com.boardhood.mobile.activity.NewConversationActivity;
import com.boardhood.mobile.activity.ProfileActivity;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class FooterBar extends LinearLayout implements OnClickListener {
	public static final int BTN_FEED     = 1;
	public static final int BTN_EXPLORE  = 2;
	public static final int BTN_ACTIVITY = 3;
	public static final int BTN_PROFILE  = 4;
	
	private LayoutInflater inflater;
    private LinearLayout footerBarView;
    private ImageTextButton feedButton;
    private ImageTextButton exploreButton;
    private ImageButton newConversationButton;
    private ImageTextButton activityButton;
    private ImageTextButton profileButton;
    
    private int currentBtn = -1;
    
    private Interest interest;
	private Conversation parentConversation;

	public FooterBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public void init() {
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		footerBarView = (LinearLayout) inflater.inflate(R.layout.footerbar, null);
        addView(footerBarView);
        
        feedButton = (ImageTextButton) footerBarView.findViewById(R.id.footerbar_feed);
        feedButton.setOnClickListener(this);
        
        exploreButton = (ImageTextButton) footerBarView.findViewById(R.id.footerbar_explore);
        exploreButton.setOnClickListener(this);
        
        newConversationButton = (ImageButton) footerBarView.findViewById(R.id.footerbar_new);
        newConversationButton.setOnClickListener(this);
        
        activityButton = (ImageTextButton) footerBarView.findViewById(R.id.footerbar_activity);
        activityButton.setOnClickListener(this);
        
        profileButton = (ImageTextButton) footerBarView.findViewById(R.id.footerbar_profile);
        profileButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (feedButton.getId() == v.getId()) {
			getContext().startActivity(FeedActivity.createIntent(getContext()));
			setCurrentButton(BTN_FEED);
		}
		
		else if (exploreButton.getId() == v.getId()) {
			getContext().startActivity(ExploreActivity.createIntent(getContext()));
			setCurrentButton(BTN_EXPLORE);
		}
		
		else if (newConversationButton.getId() == v.getId()) {
			Intent intent = NewConversationActivity.createIntent(getContext());
			if (interest != null)
				intent.putExtra(Extra.INTEREST, interest);
			if (parentConversation != null)
				intent.putExtra(Extra.PARENT_CONVERSATION, parentConversation);
			getContext().startActivity(intent);
		}
		
		else if (activityButton.getId() == v.getId()) {
			getContext().startActivity(ActivityActivity.createIntent(getContext()));
			setCurrentButton(BTN_ACTIVITY);
		}
		
		else if (profileButton.getId() == v.getId()) {
			Intent intent = ProfileActivity.createIntent(getContext());
			getContext().startActivity(intent);
			setCurrentButton(BTN_PROFILE);
		}
	}
	
	public void setInterest(Interest interest) {
		this.interest = interest;
	}
	
	public void setParentConversation(Conversation conversation) {
		this.parentConversation = conversation;
	}
	
	public void setCurrentButton(int btnId) {
		if (currentBtn != -1) {
			setSelected(currentBtn, false);
		}
		
		currentBtn = btnId;
		setSelected(btnId, true);
	}
	
	protected void setSelected(int btnId, boolean pressed) {
		switch (btnId) {
			case BTN_FEED:
				feedButton.setSelected(pressed);
				break;
				
			case BTN_EXPLORE:
				exploreButton.setSelected(pressed);
				break;
				
			case BTN_ACTIVITY:
				activityButton.setSelected(pressed);
				break;
				
			case BTN_PROFILE:
				profileButton.setSelected(pressed);
				break;
		}
	}
}
