package com.boardhood.mobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boardhood.api.model.Conversation;
import com.boardhood.api.model.Interest;
import com.boardhood.api.model.User;
import com.boardhood.api.util.BHArrayList;
import com.boardhood.api.util.BHList;
import com.boardhood.mobile.BoardHoodApplication;
import com.boardhood.mobile.BoardHoodSettings;
import com.boardhood.mobile.CredentialManager;
import com.boardhood.mobile.Extra;
import com.boardhood.mobile.R;
import com.boardhood.mobile.activity.base.BoardHoodActivity;
import com.boardhood.mobile.adapter.ConversationAdapter;
import com.boardhood.mobile.adapter.InterestAdapter;
import com.boardhood.mobile.adapter.InterestReadOnlyAdapter;
import com.boardhood.mobile.list.ListListener;
import com.boardhood.mobile.list.ListContext;
import com.boardhood.mobile.list.NormalListView;
import com.boardhood.mobile.list.RoundedListView;
import com.boardhood.mobile.loader.AuthUserTask;
import com.boardhood.mobile.loader.UserConversationsTask;
import com.boardhood.mobile.loader.UserInterestsTask;
import com.boardhood.mobile.loader.UserTask;
import com.boardhood.mobile.utils.TaskLoader;
import com.boardhood.mobile.widget.ActionBar;
import com.boardhood.mobile.widget.ProfileImageView;

public class ProfileActivity extends BoardHoodActivity {
	public static final int TAB_CONVERSATIONS = 1;
	public static final int TAB_INTERESTS = 2;
	
	private TextView usernameView;
	private ProfileImageView avatarView;
	private TextView conversationsCountView;
	private TextView conversationsLabelView;
	private TextView interestsCountView;
	private TextView interestsLabelView;
	private Button editProfileButton;
	
	private ActionBar actionBar;
	
	private LinearLayout tabContainer;
	private RelativeLayout tabSelectorConversations;
	private RelativeLayout tabSelectorInterests;
	
	private View tabConversations;
	private View tabInterests;
	private int currentTab = TAB_CONVERSATIONS;
	
	private BHList<Conversation> conversationsList = new BHArrayList<Conversation>();
	private BHList<Interest> interestsList = new BHArrayList<Interest>();
	
	private NormalListView conversationListView;
	private RoundedListView interestListView;
	
	private ConversationAdapter conversationsAdapter;
	private ArrayAdapter<Interest> interestsAdapter;
	
	private ListListener<Conversation> conversationListListener;
	private ListListener<Interest> interestListListener;
	
	private ListContext<User> conversationListContext;
	private ListContext<User> interestListContext;
	
	private int colorOrange;
	private int colorDarkGray;

	private User user;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        registerLogoutReceiver();
        
        initAttrs();
        initUser();
	}
	
	public void initAttrs() {
		actionBar = (ActionBar) findViewById(R.id.actionbar);
		//footerBar = (FooterBar) findViewById(R.id.footerbar);
        usernameView = (TextView) findViewById(R.id.user_name);
        avatarView = (ProfileImageView) findViewById(R.id.user_avatar);
        conversationsCountView = (TextView) findViewById(R.id.user_conversations_count);
        conversationsLabelView = (TextView) findViewById(R.id.user_conversations_label);
        interestsCountView = (TextView) findViewById(R.id.user_interests_count);
        interestsLabelView = (TextView) findViewById(R.id.user_interests_label);
        tabContainer = (LinearLayout) findViewById(R.id.tab_container);
        tabSelectorConversations = (RelativeLayout) findViewById(R.id.conversations_tab_selector);
        tabSelectorInterests = (RelativeLayout) findViewById(R.id.interests_tab_selector);
        editProfileButton = (Button) findViewById(R.id.edit_profile);

		colorOrange = getResources().getColor(R.color.orange);
        colorDarkGray = getResources().getColor(R.color.dark_gray);
        
        actionBar.setOnRefreshAction(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (user != null) {
					reloadUser();
				}
			}
        });
	}
	
	public void initLists() {
		conversationsAdapter = new ConversationAdapter(this, conversationsList);
		
		if (CredentialManager.getAuthUser(this).getName().equals(user.getName())) {
        	interestsAdapter = new InterestAdapter(this, interestsList);
        	((InterestAdapter)interestsAdapter).setClient(client);
        } else {
        	interestsAdapter = new InterestReadOnlyAdapter(this, interestsList);
        }
		
		conversationListContext = new ListContext<User>();
		interestListContext = new ListContext<User>();
		
		
        conversationListListener = new ListListener<Conversation>(this, actionBar, 
        		conversationsList, conversationsAdapter, conversationListContext);
        
        interestListListener = new ListListener<Interest>(this, actionBar, 
        		interestsList, interestsAdapter, interestListContext);
	}
	
	public void initListeners() {
		tabSelectorConversations.setOnClickListener(onClickListener);
        tabSelectorInterests.setOnClickListener(onClickListener);
	}
	
	public void initUser() {
		String username = getIntent().getStringExtra(Extra.USER_NAME);
		
		if (username == null || CredentialManager.getAuthUser(this).getName().equals(username)) {
			//footerBar.setCurrentButton(FooterBar.BTN_PROFILE);
			
			editProfileButton.setVisibility(View.VISIBLE);
			editProfileButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (user != null) {
						Intent editIntent = ProfileEditActivity.createIntent(ProfileActivity.this);
						editIntent.putExtra(Extra.USER_NAME, user);
						startActivity(editIntent);
					}
				}
			});
			
			actionBar.setOnLogoutAction(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CredentialManager.deleteAuthUser();
					BoardHoodSettings.clear();
					cache.clear(true);
					
					Intent broadcastIntent = new Intent();
					broadcastIntent.setAction(BoardHoodApplication.ACTION_LOGOUT);
					sendBroadcast(broadcastIntent);
					
					startActivity(LoginActivity.createIntent(ProfileActivity.this));
				}
	        });
			
			new AuthUserTask(client, userListener).execute();
		} else {
			new UserTask(client, userListener).execute(username);
		}
	}
	
	public void reloadUser() {
		if (CredentialManager.getAuthUser(this).getName().equals(user.getName())) {
			new AuthUserTask(client, userRefreshListener).execute();
		} else {
			new UserTask(client, userRefreshListener).execute(user.getName());
		}
	}
	
	public static Intent createIntent(Context context) {
        Intent i = new Intent(context, ProfileActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
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
				
			case TAB_INTERESTS:
				currentTab = TAB_INTERESTS;
				selectInterestsTab(true);
				tabContainer.addView(getTabInterests(), params);
				refreshTabData(TAB_INTERESTS);
				break;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void refreshTabData(int tabId) {
		if (user == null) return;
		
		switch (tabId) {
			case TAB_CONVERSATIONS:
					new UserConversationsTask(client, conversationListListener,
							ListListener.REFRESH).execute(conversationListContext);
				break;
				
			case TAB_INTERESTS:
					new UserInterestsTask(client, interestListListener,
							ListListener.REFRESH).execute(interestListContext);
				break;
		}
	}
	
	private View getTabConversations() {
		if (tabConversations == null) {
			tabConversations = View.inflate(ProfileActivity.this, R.layout.tab_conversations, null);
			conversationListView = (NormalListView) tabConversations.findViewById(R.id.conversation_list);
			conversationListView.setNoItemsText(R.string.no_conversations);
			conversationListView.setAdapter(conversationsAdapter);
			conversationListView.setOnLoadMoreListener(onLoadMoreListener);
			conversationListView.setOnItemClickListener(onConversationItemClickListener);
			conversationListListener.setListView(conversationListView);
			conversationListView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
		}
		
		conversationListView.refreshDrawableState();

		return tabConversations;
	}
	
	private View getTabInterests() {
		if (tabInterests == null) {
			tabInterests = View.inflate(ProfileActivity.this, R.layout.tab_interests, null);
			interestListView = (RoundedListView) tabInterests.findViewById(R.id.interest_list);
			interestListView.setNoItemsText(R.string.no_interests);
			interestListView.setAdapter(interestsAdapter);
			interestListView.setOnLoadMoreListener(onLoadMoreListener);
			interestListView.setOnItemClickListener(onInterestItemClickListener);
			interestListListener.setListView(interestListView);
			interestListView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
		}
		
		return tabInterests;
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
	
	private void selectInterestsTab(boolean enable) {
		if (enable == true) {
			interestsCountView.setTextColor(colorOrange);
			interestsLabelView.setTextColor(colorOrange);
		} else {
			interestsCountView.setTextColor(colorDarkGray);
			interestsLabelView.setTextColor(colorDarkGray);
		}
	}
	
	private void disableTabSelectors() {
		selectConversationsTab(false);
		selectInterestsTab(false);
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		public void onClick(final View v) {
			disableTabSelectors();
			
			switch (v.getId()) {
				case R.id.conversations_tab_selector:
					tabContainer.removeAllViews();
					generateView(TAB_CONVERSATIONS);
					break;
		
				case R.id.interests_tab_selector:
					tabContainer.removeAllViews();
					generateView(TAB_INTERESTS);
					break;
			}
		}
	};
	
	private OnClickListener onLoadMoreListener = new OnClickListener() {
		@SuppressWarnings("unchecked")
		public void onClick(final View v) {
			if (user == null)
				return;
			
			switch (currentTab) {
				case TAB_CONVERSATIONS:
					new UserConversationsTask(client, conversationListListener,
							ListListener.LOAD_MORE).execute(conversationListContext);
					break;
		
				case TAB_INTERESTS:
					new UserInterestsTask(client, interestListListener, 
							ListListener.LOAD_MORE).execute(interestListContext);
					break;
			}
		}
	};
	
	private OnItemClickListener onConversationItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent conversationIntent = ConversationActivity.createIntent(ProfileActivity.this);
			conversationIntent.putExtra(Extra.CONVERSATION, (Conversation) conversationListView.getItemAtPosition(position));
	        startActivity(conversationIntent);
		}
	};
	
	private OnItemClickListener onInterestItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent interestIntent = InterestActivity.createIntent(ProfileActivity.this);
			interestIntent.putExtra(Extra.INTEREST, (Interest) interestListView.getItemAtPosition(position));
	        startActivity(interestIntent);
		}
	};
	
	private TaskLoader.TaskListener<User> userListener = new TaskLoader.TaskListener<User>() {
		@Override
		public void onPreExecute() {
			actionBar.showProgressBar();
		}

		@Override
		public void onTaskSuccess(User result) {
			user = result;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					usernameView.setText(user.getName());
					if (user.getAvatar() != null) {
						avatarView.setImageUrl(user.getAvatar());
						avatarView.loadImage();
					} else {
						avatarView.setNoImageDrawable(R.drawable.no_avatar);
					}
					
					conversationsCountView.setText(String.valueOf(user.getConversationsCount()));
					interestsCountView.setText(String.valueOf(user.getInterestsCount()));
					actionBar.hideProgressBar();
					
					initLists();
					initListeners();
					
					conversationListContext.setObject(user);
					interestListContext.setObject(user);
					
					tabContainer.removeAllViews();
					generateView(currentTab);
				}
			});
		}

		@Override
		public void onTaskFailed(Exception error) {
			Log.e("ProfileActivity", "Error: " + error.getMessage());
			actionBar.hideProgressBar();
			Toast.makeText(ProfileActivity.this, R.string.profile_load_error, Toast.LENGTH_SHORT).show();
		}
    };
    
    private TaskLoader.TaskListener<User> userRefreshListener = new TaskLoader.TaskListener<User>() {
		@Override
		public void onPreExecute() {
			actionBar.showProgressBar();
		}

		@Override
		public void onTaskSuccess(User result) {
			user = result;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					usernameView.setText(user.getName());
					if (user.getAvatar() != null) {
						avatarView.setImageUrl(user.getAvatar());
						avatarView.loadImage();
					} else {
						avatarView.setNoImageDrawable(R.drawable.no_avatar);
					}
					
					conversationsCountView.setText(String.valueOf(user.getConversationsCount()));
					interestsCountView.setText(String.valueOf(user.getInterestsCount()));
					
					conversationListContext.setObject(user);
					interestListContext.setObject(user);
					
					refreshTabData(currentTab);
				}
			});
		}

		@Override
		public void onTaskFailed(Exception error) {
			Log.e("ProfileActivity", "Error: " + error.getMessage());
			actionBar.hideProgressBar();
			Toast.makeText(ProfileActivity.this, R.string.profile_load_error, Toast.LENGTH_SHORT).show();
		}
    };
}
