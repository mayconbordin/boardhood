package com.boardhood.mobile.parser;

import java.util.Date;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;

import com.boardhood.api.model.Conversation;
import com.boardhood.mobile.R;
import com.boardhood.mobile.utils.DateHelper;

public class ActivityParser {
	public static final String TITLE = "<b>%s</b> %s <b>%s</b>";
	public static final String TIME = "%s%s";
	
	private Context ctx;
	private Conversation obj;
	
	public ActivityParser(Context ctx, Conversation obj) {
		this.ctx = ctx;
		this.obj = obj;
	}
	
	public ActivityParser(Context ctx) {
		this.ctx = ctx;
	}
	
	public Spanned getTitle() {
		String title = String.format(TITLE, obj.getUser().getName(), 
				ctx.getString(R.string.replied_to_conversation), obj.getInterest().getName());
		
		return Html.fromHtml(title);
	}
	
	public String getInfo() {
		return DateHelper.getShortTimeDiff(ctx, obj.getCreatedAt(), new Date());
	}
	
	public void setObject(Conversation obj) {
		this.obj = obj;
	}
}
