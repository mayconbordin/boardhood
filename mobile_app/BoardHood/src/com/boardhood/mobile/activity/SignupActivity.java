package com.boardhood.mobile.activity;

import org.apache.http.conn.HttpHostConnectException;

import com.boardhood.api.exception.ValidationError;
import com.boardhood.api.exception.ValidationException;
import com.boardhood.api.model.User;
import com.boardhood.mobile.BoardHoodClientManager;
import com.boardhood.mobile.BoardHoodWebCacheClient;
import com.boardhood.mobile.CredentialManager;
import com.boardhood.mobile.R;
import com.boardhood.mobile.activity.base.BoardHoodActivity;
import com.boardhood.mobile.loader.UserCreateTask;
import com.boardhood.mobile.utils.NetworkUtil;
import com.boardhood.mobile.utils.TaskLoader;
import com.boardhood.mobile.utils.Validator;
import com.boardhood.mobile.utils.Validator.Field;
import com.boardhood.mobile.widget.Button;
import com.boardhood.mobile.widget.LoadingButton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class SignupActivity extends BoardHoodActivity {
	private EditText usernameEditText;
	private EditText emailEditText;
	private EditText passwordEditText;
	private LoadingButton createButton;
	private Button cancelButton;
	
	private User user;
	
	private Validator validator;
	private Field fieldUsername;
	private Field fieldEmail;
	private Field fieldPassword;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        
        initAttrs();
        initListeners();
        
        // for a better gradient background
        // not sure if it works
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        getWindow().getDecorView().getBackground().setDither(true);
    }
    
    public void initAttrs() {
    	usernameEditText = (EditText) findViewById(R.id.signup_username);
    	emailEditText = (EditText) findViewById(R.id.signup_email);
    	passwordEditText = (EditText) findViewById(R.id.signup_password);
    	createButton = (LoadingButton) findViewById(R.id.signup_create);
    	cancelButton = (Button) findViewById(R.id.signup_cancel);
    	
    	createButton.setText(getString(R.string.create));
    	cancelButton.toGray();
    	
    	validator = new Validator(this);
		fieldUsername = validator.addField("name", usernameEditText);
		fieldEmail    = validator.addField("email", emailEditText);
		fieldPassword = validator.addField("password", passwordEditText);
    }
    
    public void initListeners() {
    	createButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				validator.clearErrors();
				String username = usernameEditText.getText().toString();
				String email = emailEditText.getText().toString();
				String password = passwordEditText.getText().toString();
				
				if (TextUtils.isEmpty(username)) {
					fieldUsername.addError(validator.getErrorMessage(fieldUsername.getName(), ValidationError.EMPTY_FIELD));
				} else if (username.length() > 80) {
					fieldUsername.addError(validator.getErrorMessage(fieldUsername.getName(), ValidationError.ABOVE_MAX_LENGTH));
				}
				
				if (TextUtils.isEmpty(email)) {
					fieldEmail.addError(validator.getErrorMessage(fieldEmail.getName(), ValidationError.EMPTY_FIELD));
				} else if (email.length() > 120) {
					fieldEmail.addError(validator.getErrorMessage(fieldEmail.getName(), ValidationError.ABOVE_MAX_LENGTH));
				}
				
				if (TextUtils.isEmpty(password)) {
					fieldPassword.addError(validator.getErrorMessage(fieldPassword.getName(), ValidationError.EMPTY_FIELD));
				} else if (password.length() < 3) {
					fieldPassword.addError(validator.getErrorMessage(fieldPassword.getName(), ValidationError.BELOW_MIN_LENGTH));
				}
				
				if (!validator.validate(null)) {
					return;
				}
					
				user = new User();
				user.setName(username);
				user.setEmail(email);
				user.setPassword(password);
				
				new UserCreateTask(client, new TaskLoader.TaskListener<User>() {
					@Override
					public void onPreExecute() {
						setAllEnabled(false);
						createButton.showLoader();
					}

					@Override
					public void onTaskSuccess(User result) {
						Log.i("SignupActivity", "user registered");

						CredentialManager.setAuthUser(SignupActivity.this, user);
						startFeedActivity();
						finish();
					}

					@Override
					public void onTaskFailed(Exception error) {
						Log.e("SignupActivity", error.getMessage());
						
						if (error instanceof ValidationException) {
							validator.validate((ValidationException) error);
						} else if (!NetworkUtil.isOnline(SignupActivity.this)) {
							showErrorDialog(R.string.connection_error, R.string.connection_error_internet);
						} else if (error instanceof HttpHostConnectException) {
							showErrorDialog(R.string.connection_error, R.string.connection_error_host);
						} else {
							showErrorDialog(R.string.signup_error, R.string.signup_error_unknown);
						}

						createButton.hideLoader();
						setAllEnabled(true);
					}
				}).execute(user);
			}
		});
    	
    	cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(LoginActivity.createIntent(SignupActivity.this));
				finish();
			}
		});
    	
    	usernameEditText.addTextChangedListener(new TextWatcher() {
    	    public void afterTextChanged(Editable s) {}
    	    public void beforeTextChanged(CharSequence s, int start, int count, int after){}
    	    public void onTextChanged(CharSequence s, int start, int before, int count){
    	        if(s != null && s.length() > 0 && usernameEditText.getError() != null) {
    	        	usernameEditText.setError(null);
    	        }
    	    }
    	});
    	
    	emailEditText.addTextChangedListener(new TextWatcher() {
    	    public void afterTextChanged(Editable s) {}
    	    public void beforeTextChanged(CharSequence s, int start, int count, int after){}
    	    public void onTextChanged(CharSequence s, int start, int before, int count){
    	        if(s != null && s.length() > 0 && emailEditText.getError() != null) {
    	        	emailEditText.setError(null);
    	        }
    	    }
    	});
    	
    	passwordEditText.addTextChangedListener(new TextWatcher() {
    	    public void afterTextChanged(Editable s) {}
    	    public void beforeTextChanged(CharSequence s, int start, int count, int after){}
    	    public void onTextChanged(CharSequence s, int start, int before, int count){
    	        if(s != null && s.length() > 0 && passwordEditText.getError() != null) {
    	        	passwordEditText.setError(null);
    	        }
    	    }
    	});
    }
    
    public void startFeedActivity() {
    	BoardHoodClientManager.setClient(
        		new BoardHoodWebCacheClient(this, BoardHoodClientManager.URL));
    	BoardHoodClientManager.setCredentials(user);
    	
    	Intent intent = FeedActivity.createIntent(SignupActivity.this);
		startActivity(intent);
    }
    
    public void showErrorDialog(int titleId, int messageId) {
    	String title = getString(titleId);
    	String message = getString(messageId);
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
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
    	createButton.setEnabled(enable);
    	cancelButton.setEnabled(enable);
    }
    
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, SignupActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
}
