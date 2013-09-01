package com.boardhood.mobile.activity;

import org.apache.http.conn.HttpHostConnectException;

import com.boardhood.api.model.User;
import com.boardhood.mobile.BoardHoodClientManager;
import com.boardhood.mobile.CredentialManager;
import com.boardhood.mobile.R;
import com.boardhood.mobile.activity.base.BoardHoodActivity;
import com.boardhood.mobile.loader.LoginTask;
import com.boardhood.mobile.utils.NetworkUtil;
import com.boardhood.mobile.utils.TaskLoader;
import com.boardhood.mobile.widget.Button;
import com.boardhood.mobile.widget.LoadingButton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class LoginActivity extends BoardHoodActivity {
	private EditText usernameEditText;
	private EditText passwordEditText;
	private LoadingButton loginButton;
	private Button signupButton;
	
	private User user;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (CredentialManager.getAuthUser(this) != null) {
        	startFeedActivity();
        }
        
        setContentView(R.layout.activity_login);
        
        initAttrs();
        initListeners();
        
        // for a better gradient background
        // not sure if it works
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        getWindow().getDecorView().getBackground().setDither(true);
    }
    
    public void initAttrs() {
    	usernameEditText = (EditText) findViewById(R.id.login_username);
    	passwordEditText = (EditText) findViewById(R.id.login_password);
    	loginButton = (LoadingButton) findViewById(R.id.login_login);
    	signupButton = (Button) findViewById(R.id.login_signup);
    	
    	loginButton.setText(getString(R.string.login));
    	signupButton.toGray();
    }
    
    public void initListeners() {
    	loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				user = new User();
				user.setName(usernameEditText.getText().toString());
				user.setPassword(passwordEditText.getText().toString());
				
				new LoginTask(client, new TaskLoader.TaskListener<Boolean>() {
					@Override
					public void onPreExecute() {
						setAllEnabled(false);
						loginButton.showLoader();
					}

					@Override
					public void onTaskSuccess(Boolean result) {
						Log.i("LoginActivity", "Login result: " + result.toString());
						
						if (result.booleanValue() == false) {
							showErrorDialog(R.string.login_error, R.string.login_error_wrong);
							loginButton.hideLoader();
							setAllEnabled(true);
						} else {
							CredentialManager.setAuthUser(LoginActivity.this, user);
							startFeedActivity();
						}
					}

					@Override
					public void onTaskFailed(Exception error) {
						Log.e("LoginActivity", error.getMessage());
						
						if (!NetworkUtil.isOnline(LoginActivity.this)) {
							showErrorDialog(R.string.connection_error, R.string.connection_error_internet);
						} else if (error instanceof HttpHostConnectException) {
							showErrorDialog(R.string.connection_error, R.string.connection_error_host);
						} else {
							showErrorDialog(R.string.login_error, R.string.login_error_unknown);
						}

						loginButton.hideLoader();
						setAllEnabled(true);
					}
				}).execute(user);
			}
		});
    	
    	signupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(SignupActivity.createIntent(LoginActivity.this));
			}
		});
    }
    
    public void startFeedActivity() {
    	BoardHoodClientManager.setCredentials(CredentialManager.getAuthUser(this));
    	Intent intent = FeedActivity.createIntent(LoginActivity.this);
		startActivity(intent);
    }
    
    public void showErrorDialog(int titleId, int messageId) {
    	String title = getString(titleId);
    	String message = getString(messageId);
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
		builder.setMessage(message)
			   .setTitle(title)
		       .setNeutralButton(getString(R.string.close), new DialogInterface.OnClickListener() {
		          public void onClick(final DialogInterface dialog, final int which) {
		        	  dialog.cancel();
		          }
		       });
		AlertDialog dialog = builder.create();
		dialog.show();
    }
    
    public void setAllEnabled(boolean enable) {
    	usernameEditText.setEnabled(enable);
    	passwordEditText.setEnabled(enable);
    	loginButton.setEnabled(enable);
    	signupButton.setEnabled(enable);
    }
    
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
}
