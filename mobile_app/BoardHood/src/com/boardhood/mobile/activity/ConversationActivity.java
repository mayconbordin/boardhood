package com.boardhood.mobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boardhood.api.model.Conversation;
import com.boardhood.api.util.BHArrayList;
import com.boardhood.api.util.BHList;
import com.boardhood.mobile.Extra;
import com.boardhood.mobile.R;
import com.boardhood.mobile.activity.base.BoardHoodListActivity;
import com.boardhood.mobile.adapter.ConversationAdapter;
import com.boardhood.mobile.list.ListListener;
import com.boardhood.mobile.list.ListContext;
import com.boardhood.mobile.list.NormalListView;
import com.boardhood.mobile.loader.ConversationRepliesTask;
import com.boardhood.mobile.loader.ConversationTask;
import com.boardhood.mobile.parser.ConversationParser;
import com.boardhood.mobile.utils.TaskLoader;
import com.boardhood.mobile.widget.FooterBar;
import com.boardhood.mobile.widget.ProfileImageView;
import com.boardhood.mobile.widget.ActionBar;

public class ConversationActivity extends BoardHoodListActivity {
	private static final int RESULT_NEW_CONVERSATION = 1;
	
	private Conversation conversation;
	private ConversationParser parser;
	
	private RelativeLayout interestHeader;
	private ProfileImageView profileImageView;
	private TextView interestTextView;
	private ImageView backImageView;
	private TextView titleTextView;
	private TextView infoTextView;
	private TextView messageTextView;
	private TextView repliesTextView;
	private Button replyButton;
	
	private FooterBar footerBar;
	
	private BHList<Conversation> list = new BHArrayList<Conversation>();
    private ConversationAdapter adapter;
    private NormalListView listView;
    
    private ListListener<Conversation> listListener;
    private ListContext<Conversation> listContext;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        
        registerLogoutReceiver();
        
        initAttrs();
        init();
        initListeners();
	}
	
	public void initAttrs() {
		actionBar = (ActionBar) findViewById(R.id.actionbar);
		footerBar = (FooterBar) findViewById(R.id.footerbar);
		listView = (NormalListView) getListView();
		interestTextView = (TextView) findViewById(R.id.conversation_interest);
		backImageView = (ImageView) findViewById(R.id.conversation_interest_back);
        interestHeader = (RelativeLayout) findViewById(R.id.conversation_interest_header);
        titleTextView = (TextView) findViewById(R.id.conversationitem_title);        
        infoTextView = (TextView) findViewById(R.id.conversationitem_info);        
        messageTextView = (TextView) findViewById(R.id.conversationitem_message);        
        repliesTextView = (TextView) findViewById(R.id.conversationitem_repliescount);        
        profileImageView = (ProfileImageView) findViewById(R.id.conversationitem_avatar);
        
        
        View v = listView.setNoItemsView(R.layout.listview_conversation_replies_footer);
        replyButton = (Button) v.findViewById(R.id.conversation_noreplies_reply);
        replyButton.setOnClickListener(replyListener);
	}
	
	public void init() {
		conversation = (Conversation) getIntent().getSerializableExtra(Extra.CONVERSATION);
		
		if (conversation.getParent() != null) {
			Conversation c = conversation.getParent();
			c.setInterest(conversation.getInterest());
			conversation = c;
			new ConversationTask(client, conversationListener).execute(conversation);
		} else {
			initConversation();
	        initList();
		}
	}
	
	protected void initListeners() {
        actionBar.setOnRefreshAction(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (conversation != null) {
					new ConversationTask(client, conversationListener).execute(conversation);
				}
			}
        });
        
        interestHeader.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.i("ConversationActivity", "Go to interest ID: " + conversation.getInterest().getId());
				Intent interestIntent = InterestActivity.createIntent(ConversationActivity.this);
				interestIntent.putExtra(Extra.INTEREST, conversation.getInterest());
				startActivity(interestIntent);
			}
		});
        
        interestHeader.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					interestTextView.setPressed(false);
					backImageView.setImageResource(R.drawable.back_icon_normal);
				} else {
					interestTextView.setPressed(true);
					backImageView.setPressed(true);
					backImageView.setImageResource(R.drawable.back_icon_pressed);
				}
				
				return false;
			}
        });
                
        interestHeader.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                
        profileImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("ConversationActivity", "Go to user: " + conversation.getUser().getName());
				Intent intent = ProfileActivity.createIntent(ConversationActivity.this);
				intent.putExtra(Extra.USER_NAME, conversation.getUser().getName());
				startActivity(intent);
			}
        });
	}
	
	@SuppressWarnings("unchecked")
	public void initList() {
		adapter = new ConversationAdapter(this, list, true);
        setListAdapter(adapter);
        
        listContext =  new ListContext<Conversation>(conversation);
        listListener = new ListListener<Conversation>(this, actionBar, list, adapter, 
        		listView, listContext);
		
		listView.setOnLoadMoreListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new ConversationRepliesTask(client, listListener, ListListener.LOAD_MORE).execute(listContext);
			}
		});
        
        new ConversationRepliesTask(client, listListener, ListListener.REFRESH).execute(listContext);
	}
	
	public void initConversation() {
		footerBar.setParentConversation(conversation);
		parser = new ConversationParser(this, conversation);
		
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                interestTextView.setText(conversation.getInterest().getName());
                titleTextView.setText(parser.getSimpleTitle());        
                infoTextView.setText(parser.getInfo());        
                messageTextView.setText(conversation.getMessage());        
                repliesTextView.setText(parser.getReplies());
                
            	if (conversation.getUser().getAvatar() != null) {
                	profileImageView.setImageUrl(conversation.getUser().getAvatar());
                	profileImageView.loadImage();
                } else {
                	profileImageView.setNoImageDrawable(R.drawable.no_avatar);
				}
            }
        });
	}
	
	private TaskLoader.TaskListener<Conversation> conversationListener = new TaskLoader.TaskListener<Conversation>() {
		@Override
		public void onPreExecute() {
			actionBar.showProgressBar();
		}

		@Override
		public void onTaskSuccess(Conversation result) {
			conversation = result;
			initConversation();
	        initList();
	        actionBar.hideProgressBar();
		}

		@Override
		public void onTaskFailed(Exception error) {
			Log.e("ConversationRefreshListener", (error.getMessage() == null) ? error.toString() : error.getMessage());
			Toast.makeText(getApplicationContext(), R.string.conversation_update_error, Toast.LENGTH_SHORT).show();
			actionBar.hideProgressBar();
		}
	};
	
	private View.OnClickListener replyListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = NewConversationActivity.createIntent(ConversationActivity.this);
			intent.putExtra(Extra.PARENT_CONVERSATION, conversation);
			startActivityForResult(intent, RESULT_NEW_CONVERSATION);
		}
	};
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_NEW_CONVERSATION) {
			if (resultCode == RESULT_OK) {
				if (list.isEmpty()) {
					new ConversationRepliesTask(client, listListener, ListListener.REFRESH).execute(listContext);
				}
			}
		}
	}
	
	public static Intent createIntent(Context context) {
        Intent i = new Intent(context, ConversationActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
}
