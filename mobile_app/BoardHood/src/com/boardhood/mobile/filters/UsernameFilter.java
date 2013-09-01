package com.boardhood.mobile.filters;

import android.text.InputFilter;
import android.text.Spanned;

public class UsernameFilter implements InputFilter {

	@Override
	public CharSequence filter(CharSequence source, int start, int end,
    		Spanned dest, int dstart, int dend) {
        for (int i = start; i < end; i++) {
            if (!Character.isLetterOrDigit(source.charAt(i)) && source.charAt(i) != '_') {
                return "";
            }
        }
        return null;
    }

}
