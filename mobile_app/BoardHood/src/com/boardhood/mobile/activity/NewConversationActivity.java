package com.boardhood.mobile.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.boardhood.api.model.Conversation;
import com.boardhood.api.model.Coordinates;
import com.boardhood.api.model.Interest;
import com.boardhood.mobile.Extra;
import com.boardhood.mobile.LocationFinder;
import com.boardhood.mobile.R;
import com.boardhood.mobile.activity.base.BoardHoodActivity;
import com.boardhood.mobile.loader.NewConversationTask;
import com.boardhood.mobile.utils.TaskLoader;

public class NewConversationActivity extends BoardHoodActivity {
	private Interest interest;
	private Conversation parent;
	
	private EditText messageText;
	private Button targetButton;
	private Button cancelButton;
	private Button sendButton;
	private ImageButton locationButton;
	private ProgressBar loadingBar;
	
	private boolean shareLocation = false;
		
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        registerLogoutReceiver();
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_conversation);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        
        messageText = (EditText) findViewById(R.id.conversation_message);
        targetButton = (Button) findViewById(R.id.conversation_interest);
        cancelButton = (Button) findViewById(R.id.conversation_cancel);
        sendButton = (Button) findViewById(R.id.conversation_send);
        loadingBar = (ProgressBar) findViewById(R.id.loading_bar);
        locationButton = (ImageButton) findViewById(R.id.conversation_location);
        
        // Get the intent that started this activity
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_SEND) 
        		&& intent.getType().equals("text/plain")) {
        	String message = intent.getStringExtra(Intent.EXTRA_TEXT);
        	messageText.setText(message);
        }
        
        messageText.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
				tryToEnableSendButton();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        
        targetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = ExploreActivity.createIntent(NewConversationActivity.this);
				intent.putExtra(Extra.FOR_RESULT, true);
				startActivityForResult(intent, 1);
			}
		});
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_CANCELED, getIntent());
				finish();
			}
		});
        
        locationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (shareLocation) {
					shareLocation = false;
					locationButton.setImageResource(R.drawable.location_disabled);
				} else {
					shareLocation = true;
					locationButton.setImageResource(R.drawable.location);
				}
			}
		});
        
        sendButton.setEnabled(false);
        sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				interest = (interest == null) ? parent.getInterest() : interest;
				Conversation conversation = new Conversation();
				conversation.setInterest(interest);
				conversation.setMessage(messageText.getText().toString());
				conversation.setParent(parent);
				
				if (shareLocation) {
					Location l = LocationFinder.getInstance().getLastLocation();
					if (l != null) {
						conversation.setLocation(new Coordinates(l.getLatitude(), l.getLongitude()));
					}
				}
								
				new NewConversationTask(client, new TaskLoader.TaskListener<Conversation>() {
					@Override
					public void onPreExecute() {
						loadingBar.setVisibility(View.VISIBLE);
					}

					@Override
					public void onTaskSuccess(Conversation result) {
						Toast.makeText(NewConversationActivity.this, R.string.conversation_created, Toast.LENGTH_SHORT).show();
						
	    				setResult(RESULT_OK, getIntent());
	    				loadingBar.setVisibility(View.GONE);
						finish();
					}

					@Override
					public void onTaskFailed(Exception error) {
						Log.e("NewConversationActivity", "Error: " + error.getMessage());
						Toast.makeText(NewConversationActivity.this, R.string.conversation_create_error, Toast.LENGTH_SHORT).show();
						loadingBar.setVisibility(View.GONE);
					}
				}).execute(conversation);
			}
		});
        
        setInterest(null);
        setParentConversation(null);
    }
    
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, NewConversationActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				setInterest((Interest) data.getExtras().getSerializable(Extra.INTEREST));
				tryToEnableSendButton();
				Log.i("NewConversationActivity", "Choosed interest: " + interest.getId());
			}
		}
	}
	
	private void setInterest(Interest interest) {
		if (interest == null)
			interest = (Interest) getIntent().getSerializableExtra(Extra.INTEREST);
		
		if (interest != null) {
			this.interest = interest;
			targetButton.setText(interest.getName());
		}
	}
	
	private void setParentConversation(Conversation parent) {
		if (parent == null)
			parent = (Conversation) getIntent().getSerializableExtra(Extra.PARENT_CONVERSATION);
		
		if (parent != null) {
			this.parent = parent;
			targetButton.setText(getString(R.string.in_response_to) + " " + parent.getUser().getName());
			targetButton.setEnabled(false);
		}
	}
	
	public void tryToEnableSendButton() {
		if (messageText.getText().length() > 3 && (interest != null || parent != null)) {
			sendButton.setEnabled(true);
		} else {
			sendButton.setEnabled(false);
		}
	}
}
