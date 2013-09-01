package com.boardhood.mobile.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class DimensionHelper {
	public static int dipToPixels(Context ctx, int dipValue) {
		Resources r = ctx.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
        return px;
	}
}
