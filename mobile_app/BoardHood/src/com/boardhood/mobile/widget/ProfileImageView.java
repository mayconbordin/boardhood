package com.boardhood.mobile.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ProgressBar;

import com.boardhood.mobile.R;
import com.boardhood.mobile.utils.DimensionHelper;
import com.github.ignition.core.widgets.RemoteImageView;
import com.github.ignition.support.images.remote.RemoteImageLoaderHandler;

public class ProfileImageView extends RemoteImageView {

	public ProfileImageView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	public ProfileImageView(Context context, String imageUrl, boolean autoLoad) {
		super(context, imageUrl, autoLoad);
	}

	public ProfileImageView(Context context, String imageUrl,
			Drawable progressDrawable, Drawable errorDrawable, boolean autoLoad) {
		super(context, imageUrl, progressDrawable, errorDrawable, autoLoad);
	}

	protected void addLoadingSpinnerView(Context context) {
        loadingSpinner = new ProgressBar(context);
        loadingSpinner.setIndeterminate(true);
        if (this.progressDrawable == null) {
            this.progressDrawable = loadingSpinner.getIndeterminateDrawable();
        } else {
            loadingSpinner.setIndeterminateDrawable(progressDrawable);
            if (progressDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable) progressDrawable).start();
            }
        }

        int width = DimensionHelper.dipToPixels(getContext(), 20);
        int heigth = DimensionHelper.dipToPixels(getContext(), 20);
        
        //setBackgroundColor(R.color.conversationlist_avatar_background);
        this.setBackgroundResource(R.color.conversationlist_avatar_background);
        
        LayoutParams lp = new LayoutParams(width, heigth);
        lp.gravity = Gravity.CENTER;
        
        addView(loadingSpinner, 0, lp);
    }
	
	public void loadImage() {
        if (imageUrl == null) {
            throw new IllegalStateException(
                    "image URL is null; did you forget to set it for this view?");
        }
        setDisplayedChild(0);
        imageLoader.loadImage(imageUrl, imageView, new BoardHoodImageLoaderHandler());
    }

	private class BoardHoodImageLoaderHandler extends RemoteImageLoaderHandler {

        public BoardHoodImageLoaderHandler() {
            super(imageView, imageUrl, errorDrawable);
        }

        @Override
        protected boolean handleImageLoaded(Bitmap bitmap, Message msg) {
            boolean wasUpdated = super.handleImageLoaded(bitmap, msg);
            if (wasUpdated) {
                isLoaded = true;
                
                if (bitmap != null)
                	setBackgroundResource(R.color.conversationlist_item_background);
                
                setDisplayedChild(1);
            }
            return wasUpdated;
        }
    }
}
