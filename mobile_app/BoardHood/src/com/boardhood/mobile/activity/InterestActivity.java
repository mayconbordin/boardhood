package com.boardhood.mobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

import com.boardhood.api.model.Conversation;
import com.boardhood.api.model.Interest;
import com.boardhood.api.model.User;
import com.boardhood.api.util.BHArrayList;
import com.boardhood.api.util.BHList;
import com.boardhood.mobile.Extra;
import com.boardhood.mobile.R;
import com.boardhood.mobile.activity.base.BoardHoodActivity;
import com.boardhood.mobile.adapter.ConversationAdapter;
import com.boardhood.mobile.adapter.FollowerAdapter;
import com.boardhood.mobile.list.ListListener;
import com.boardhood.mobile.list.ListContext;
import com.boardhood.mobile.list.NormalListView;
import com.boardhood.mobile.list.RoundedListView;
import com.boardhood.mobile.loader.InterestConversationsTask;
import com.boardhood.mobile.loader.InterestFollowersTask;
import com.boardhood.mobile.loader.InterestTask;
import com.boardhood.mobile.utils.TaskLoader;
import com.boardhood.mobile.widget.FollowButton;
import com.boardhood.mobile.widget.FooterBar;
import com.boardhood.mobile.widget.ActionBar;

public class InterestActivity extends BoardHoodActivity {
	public static final int TAB_FOLLOWERS = 1;
	public static final int TAB_CONVERSATIONS = 2;
	
	private Interest interest;

    private FooterBar footerBar;
    
    private TextView nameTextView;
    
    private TextView followersCountView;
    private TextView followersLabelView;
    private TextView conversationsCountView;
    private TextView conversationsLabelView;
    
    private FollowButton followButton;
    
    private LinearLayout tabContainer;
	private RelativeLayout tabSelectorFollowers;
	private RelativeLayout tabSelectorConversations;
	
	private View tabFollowers;
	private View tabConversations;
	private int currentTab = TAB_FOLLOWERS;
	
	private BHList<User> followersList = new BHArrayList<User>();
	private BHList<Conversation> conversationsList = new BHArrayList<Conversation>();
	
	private RoundedListView followerListView;
	private NormalListView conversationListView;
	
	private FollowerAdapter followersAdapter;
	private ConversationAdapter conversationsAdapter;
		
	private int colorOrange;
	private int colorDarkGray;
	
	private ListListener<User> followerListListener;
	private ListListener<Conversation> conversationListListener;
	
	private ListContext<Interest> followerListContext;
	private ListContext<Interest> conversationListContext;
    	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        
        registerLogoutReceiver();
        
        initAttrs();
        initInterest();
	}
	
	public void initAttrs() {
		actionBar = (ActionBar) findViewById(R.id.actionbar);
		footerBar = (FooterBar) findViewById(R.id.footerbar);
		
		nameTextView = (TextView) findViewById(R.id.interest_name);
		followButton = (FollowButton) findViewById(R.id.interest_follow);
		followersCountView = (TextView) findViewById(R.id.interest_followers_count);
		followersLabelView = (TextView) findViewById(R.id.interest_followers_label);
		conversationsCountView = (TextView) findViewById(R.id.interest_conversations_count);
		conversationsLabelView = (TextView) findViewById(R.id.interest_conversations_label);
		
		tabContainer = (LinearLayout) findViewById(R.id.tab_container);
		tabSelectorFollowers = (RelativeLayout) findViewById(R.id.followers_tab_selector);
        tabSelectorConversations = (RelativeLayout) findViewById(R.id.conversations_tab_selector);

		colorOrange = getResources().getColor(R.color.orange);
        colorDarkGray = getResources().getColor(R.color.dark_gray);
	}
	
	public void initLists() {
		followersAdapter = new FollowerAdapter(this, followersList);
		conversationsAdapter = new ConversationAdapter(this, conversationsList);
		
		followerListContext = new ListContext<Interest>(interest);
		conversationListContext = new ListContext<Interest>(interest);
		
		followerListListener = new ListListener<User>(this, actionBar, followersList, 
				followersAdapter, followerListContext);
		conversationListListener = new ListListener<Conversation>(this, actionBar, 
				conversationsList, conversationsAdapter, conversationListContext);
	}
	
	public void initListeners() {
		tabSelectorConversations.setOnClickListener(onClickListener);
        tabSelectorFollowers.setOnClickListener(onClickListener);
        
        actionBar.setOnRefreshAction(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (interest != null) {
					new InterestTask(client, interestRefreshListener).execute(interest);
				}
			}
        });
	}
	
	public void initInterest() {
		interest = (Interest) getIntent().getSerializableExtra(Extra.INTEREST);
		nameTextView.setText(interest.getName());
		
		new InterestTask(client, interestListener).execute(interest);
	}
	
	private void generateView(int tabNum) {
		LayoutParams params = new LayoutParams(tabContainer.getLayoutParams().width, tabContainer.getLayoutParams().height);
		switch (tabNum) {
			case TAB_CONVERSATIONS:
				currentTab = TAB_CONVERSATIONS;
				selectConversationsTab(true);
				tabContainer.addView(getTabConversations(), params);
				refreshTabData(TAB_CONVERSATIONS);
				break;
				
			case TAB_FOLLOWERS:
				currentTab = TAB_FOLLOWERS;
				selectFollowerstsTab(true);
				tabContainer.addView(getTabFollowers(), params);
				refreshTabData(TAB_FOLLOWERS);
				break;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void refreshTabData(int tabId) {
		if (interest == null) return;
		
		switch (tabId) {
			case TAB_CONVERSATIONS:
				new InterestConversationsTask(client, conversationListListener, ListListener.REFRESH)
					.execute(conversationListContext);
				break;
				
			case TAB_FOLLOWERS:
				new InterestFollowersTask(client, followerListListener, ListListener.REFRESH)
					.execute(followerListContext);
				break;
		}
	}
	
	private View getTabConversations() {
		if (tabConversations == null) {
			tabConversations = View.inflate(InterestActivity.this, R.layout.tab_conversations, null);
			conversationListView = (NormalListView) tabConversations.findViewById(R.id.conversation_list);
			conversationListView.setNoItemsText(R.string.no_conversations);
			conversationListView.setAdapter(conversationsAdapter);
			conversationListView.setOnLoadMoreListener(onLoadMoreListener);
			conversationListView.setOnItemClickListener(onConversationItemClickListener);
			conversationListListener.setListView(conversationListView);
		}
		return tabConversations;
	}
	
	private View getTabFollowers() {
		if (tabFollowers == null) {
			tabFollowers = View.inflate(InterestActivity.this, R.layout.tab_followers, null);
			followerListView = (RoundedListView) tabFollowers.findViewById(R.id.follower_list);
			followerListView.setNoItemsText(R.string.no_followers);
			followerListView.setAdapter(followersAdapter);
			followerListView.setOnLoadMoreListener(onLoadMoreListener);
			followerListView.setOnItemClickListener(onFollowerItemClickListener);
			followerListListener.setListView(followerListView);
		}
		return tabFollowers;
	}
	
	private void selectConversationsTab(boolean enable) {
		if (enable == true) {
			conversationsCountView.setTextColor(colorOrange);
			conversationsLabelView.setTextColor(colorOrange);
		} else {
			conversationsCountView.setTextColor(colorDarkGray);
			conversationsLabelView.setTextColor(colorDarkGray);
		}
	}
	
	private void selectFollowerstsTab(boolean enable) {
		if (enable == true) {
			followersCountView.setTextColor(colorOrange);
			followersLabelView.setTextColor(colorOrange);
		} else {
			followersCountView.setTextColor(colorDarkGray);
			followersLabelView.setTextColor(colorDarkGray);
		}
	}
	
	private void disableTabSelectors() {
		selectConversationsTab(false);
		selectFollowerstsTab(false);
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		public void onClick(final View v) {
			disableTabSelectors();
			
			switch (v.getId()) {
				case R.id.conversations_tab_selector:
					tabContainer.removeAllViews();
					generateView(TAB_CONVERSATIONS);
					break;
		
				case R.id.followers_tab_selector:
					tabContainer.removeAllViews();
					generateView(TAB_FOLLOWERS);
					break;
			}
		}
	};
	
	private OnClickListener onLoadMoreListener = new OnClickListener() {
		@SuppressWarnings("unchecked")
		public void onClick(final View v) {
			if (interest == null)
				return;
			
			switch (currentTab) {
				case TAB_CONVERSATIONS:
					new InterestConversationsTask(client, conversationListListener, ListListener.LOAD_MORE)
						.execute(conversationListContext);
					break;
		
				case TAB_FOLLOWERS:
					new InterestFollowersTask(client, followerListListener, ListListener.LOAD_MORE)
						.execute(followerListContext);
					break;
			}
		}
	};
	
	private OnItemClickListener onConversationItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent conversationIntent = ConversationActivity.createIntent(InterestActivity.this);
			conversationIntent.putExtra(Extra.CONVERSATION, (Conversation) conversationListView.getItemAtPosition(position));
	        startActivity(conversationIntent);
		}
	};
	
	private OnItemClickListener onFollowerItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent profileIntent = ProfileActivity.createIntent(InterestActivity.this);
			profileIntent.putExtra(Extra.USER_NAME, ((User)followerListView.getItemAtPosition(position)).getName());
	        startActivity(profileIntent);
		}
	};
	
	private TaskLoader.TaskListener<Interest> interestListener = new TaskLoader.TaskListener<Interest>() {
		@Override
		public void onTaskSuccess(Interest result) {
			interest = result;
			footerBar.setInterest(interest);
			followButton.setup(interest, client);
			followersCountView.setText(String.valueOf(result.getFollowersCount()));
			conversationsCountView.setText(String.valueOf(result.getConversationsCount()));
			
			initLists();      
	        initListeners();
	        
	        tabContainer.removeAllViews();
			generateView(currentTab);
			
			actionBar.hideProgressBar();
		}

		@Override
		public void onTaskFailed(Exception error) {
			Log.e("InterestActivity", "Error: " + error.getMessage());
			Toast.makeText(getApplication(), R.string.interest_load_error, Toast.LENGTH_SHORT).show();
			actionBar.hideProgressBar();
		}

		@Override
		public void onPreExecute() {
			actionBar.showProgressBar();
		}
	};
	
	private TaskLoader.TaskListener<Interest> interestRefreshListener = new TaskLoader.TaskListener<Interest>() {
		@Override
		public void onTaskSuccess(Interest result) {
			interest = result;
			footerBar.setInterest(interest);
			followButton.setup(interest, client);
			followersCountView.setText(String.valueOf(result.getFollowersCount()));
			conversationsCountView.setText(String.valueOf(result.getConversationsCount()));
			
			refreshTabData(currentTab);
		}

		@Override
		public void onTaskFailed(Exception error) {
			Log.e("InterestActivity", "Error: " + error.getMessage());
			Toast.makeText(getApplication(), R.string.interest_load_error, Toast.LENGTH_SHORT).show();
			actionBar.hideProgressBar();
		}

		@Override
		public void onPreExecute() {
			actionBar.showProgressBar();
		}
	};
	
	public static Intent createIntent(Context context) {
        Intent i = new Intent(context, InterestActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
}
