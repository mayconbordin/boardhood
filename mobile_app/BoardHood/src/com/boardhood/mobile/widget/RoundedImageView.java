package com.boardhood.mobile.widget;

import com.boardhood.mobile.utils.ImageHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundedImageView(Context context) {
		super(context);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {		
		BitmapDrawable drawable = (BitmapDrawable) getDrawable();
		
        Bitmap bitmap = resize(drawable.getBitmap().copy(Bitmap.Config.ARGB_8888, true));
        Bitmap roundBitmap = ImageHelper.getRoundedCornerBitmap(bitmap, 5);
        
        canvas.drawBitmap(roundBitmap, 0, 0, null);
	}
	
	protected Bitmap resize(Bitmap bitmap) {
		int width = bitmap.getWidth();
	    int height = bitmap.getHeight();
	    int newWidth = getWidth();
	    int newHeight = getHeight();

	    // calculate the scale - in this case = 0.4f
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;

	    // createa matrix for the manipulation
	    Matrix matrix = new Matrix();
	    // resize the bit map
	    matrix.postScale(scaleWidth, scaleHeight);

	    // recreate the new Bitmap
	    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true); 

	    return resizedBitmap;
	}

}
