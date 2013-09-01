package com.boardhood.mobile.activity;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boardhood.api.exception.ValidationError;
import com.boardhood.api.exception.ValidationException;
import com.boardhood.api.model.User;
import com.boardhood.mobile.Extra;
import com.boardhood.mobile.R;
import com.boardhood.mobile.activity.base.BoardHoodActivity;
import com.boardhood.mobile.filters.UsernameFilter;
import com.boardhood.mobile.loader.UserUpdateTask;
import com.boardhood.mobile.utils.ImageHelper;
import com.boardhood.mobile.utils.TaskLoader;
import com.boardhood.mobile.utils.Validator;
import com.boardhood.mobile.utils.Validator.Field;
import com.boardhood.mobile.widget.Button;
import com.boardhood.mobile.widget.ImageSourceDialog;
import com.boardhood.mobile.widget.LoadingButton;
import com.boardhood.mobile.widget.ProfileImageView;

public class ProfileEditActivity extends BoardHoodActivity {
	private User user;
	
	private EditText usernameEditText;
	private EditText emailEditText;
	private EditText passwordEditText;
	private ProfileImageView avatarImageView;
	private ImageView localAvatarImageView;
	private TextView errorsTextView;
	private Button cancelButton;
	private LoadingButton saveButton;
	
	private ImageSourceDialog imageSourceDialog;
	
	private Validator validator;
	private Field fieldUsername;
	private Field fieldEmail;
	private Field fieldPassword;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        
        registerLogoutReceiver();
        
        initAttrs();
        initUser();
        initListeners();
	}
	
	public void initAttrs() {
		usernameEditText = (EditText) findViewById(R.id.profile_username);
		emailEditText = (EditText) findViewById(R.id.profile_email);
		passwordEditText = (EditText) findViewById(R.id.profile_password);
		avatarImageView = (ProfileImageView) findViewById(R.id.profile_avatar);
		localAvatarImageView = (ImageView) findViewById(R.id.profile_local_avatar);
		errorsTextView = (TextView) findViewById(R.id.profile_errors);
		cancelButton = (Button) findViewById(R.id.profile_cancel);
		saveButton = (LoadingButton) findViewById(R.id.profile_save);
		imageSourceDialog = new ImageSourceDialog(ProfileEditActivity.this);
		
		cancelButton.toGray();
		saveButton.toGreen();
		saveButton.setText(getString(R.string.label_save));
		
		validator = new Validator(this);
		fieldUsername = validator.addField("name", usernameEditText);
		fieldEmail    = validator.addField("email", emailEditText);
		fieldPassword = validator.addField("password", passwordEditText);
	}
	
	public void initListeners() {
		View.OnClickListener openImageSource = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageSourceDialog.show();
			}
		};
		
		avatarImageView.setOnClickListener(openImageSource);
		localAvatarImageView.setOnClickListener(openImageSource);
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		saveButton.setOnClickListener(new View.OnClickListener() {
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
				
				if (password.length() > 0 && password.length() < 3) {
					fieldPassword.addError(validator.getErrorMessage(fieldPassword.getName(), ValidationError.BELOW_MIN_LENGTH));
				}
				
				if (!validator.validate(null)) {
					return;
				}
				
				user.setName(usernameEditText.getText().toString());
				user.setEmail(emailEditText.getText().toString());
				user.setPassword(passwordEditText.getText().toString());
				
				new UserUpdateTask(client, updateListener).execute(user);
			}
		});
	}
	
	public void initUser() {
		user = (User) getIntent().getSerializableExtra(Extra.USER_NAME);
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				usernameEditText.setText(user.getName());
				usernameEditText.setFilters(new InputFilter[]{new UsernameFilter()});
				emailEditText.setText(user.getEmail());
				
				if (user.getAvatar() != null) {
					avatarImageView.setImageUrl(user.getAvatar());
					avatarImageView.loadImage();
				}
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		imageSourceDialog.hide();
		Log.i("ProfileEditActivity", "Request code: " + requestCode);
		
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
		    	case ImageSourceDialog.IMAGE_PICK:
		    		imageFromGallery(resultCode, data);
		    		break;
		    	case ImageSourceDialog.IMAGE_CAPTURE:
		    		imageFromCamera(resultCode, data);
		    		break;
		    	default:
		    		break;
		    }
		}
	}
	
	/**
	 * Image result from camera
	 * @param resultCode
	 * @param data
	 */
	private void imageFromCamera(int resultCode, Intent data) {
		setAvatarImage((Bitmap) data.getExtras().get("data"));
	}

	/**
	 * Image result from gallery
	 * @param resultCode
	 * @param data
	 */
	private void imageFromGallery(int resultCode, Intent data) {
		Uri selectedImage = data.getData();
		String [] filePathColumn = {MediaStore.Images.Media.DATA};

		Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String filePath = cursor.getString(columnIndex);
		cursor.close();
		
		setAvatarImage(BitmapFactory.decodeFile(filePath));
	}
	
	private void setAvatarImage(Bitmap avatar) {
		avatar = ImageHelper.scaleCenterCrop(avatar, 128, 128);
		
		localAvatarImageView.setVisibility(View.VISIBLE);
		avatarImageView.setVisibility(View.GONE);
		localAvatarImageView.setImageBitmap(avatar);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		avatar.compress(CompressFormat.JPEG, 75, bos);
		user.setNewAvatar(bos.toByteArray());
	}
	
	private TaskLoader.TaskListener<User> updateListener = new TaskLoader.TaskListener<User>() {
		@Override
		public void onPreExecute() {
			saveButton.showLoader();
		}

		@Override
		public void onTaskSuccess(User result) {
			saveButton.hideLoader();
			errorsTextView.setVisibility(View.GONE);
			
			finish();
			Toast.makeText(ProfileEditActivity.this, R.string.profile_update_success, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onTaskFailed(Exception error) {
			//Log.e("ProfileEditActivity", error.getMessage());
			Log.e("ProfileEditActivity", error.getClass().getName());
			saveButton.hideLoader();
			
									
			if (error instanceof ValidationException) {
				validator.validate((ValidationException) error);
			} else {
				errorsTextView.setVisibility(View.VISIBLE);
				errorsTextView.setText(getString(R.string.unknown_error));
			}
		}
	};
	
	public static Intent createIntent(Context context) {
        Intent i = new Intent(context, ProfileEditActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
}
