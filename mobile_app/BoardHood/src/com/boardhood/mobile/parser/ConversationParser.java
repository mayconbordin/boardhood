package com.boardhood.mobile.parser;

import java.util.Date;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;

import com.boardhood.api.model.Conversation;
import com.boardhood.mobile.R;
import com.boardhood.mobile.utils.DateHelper;

public class ConversationParser {
	public static final String TITLE = "<b>%s</b> %s <b>%s</b>:";
	public static final String TIME = "%s %s";
	public static final String DIST = " %s %d%s %s";
	
	private Context ctx;
	private Conversation obj;
	
	public ConversationParser(Context ctx) {
		this.ctx = ctx;
	}
	
	public ConversationParser(Context ctx, Conversation obj) {
		this.ctx = ctx;
		this.obj = obj;
	}
	
	public Spanned getTitle() {
		String title = String.format(TITLE, obj.getUser().getName(), 
				ctx.getString(R.string.saidOn), obj.getInterest().getName());
		
		return Html.fromHtml(title);
	}
	
	public Spanned getSimpleTitle() {
		String title = String.format("<b>%s</b> %s:", obj.getUser().getName(), 
				ctx.getString(R.string.said));
		
		return Html.fromHtml(title);
	}
	
	public String getInfo() {
		String info = DateHelper.getTimeDiff(ctx, obj.getCreatedAt(), new Date());
        
        if (obj.getDistance() != null)
        	info += String.format(DIST, "and", obj.getDistance().intValue(), ctx.getString(R.string.distance), "away");
        
        return info;
	}
	
	public String getReplies() {
		String replies = "";
        if (obj.getRepliesCount() == 0)
        	replies = ctx.getString(R.string.noreplies);
        else if (obj.getRepliesCount() == 1)
        	replies = "1 " + ctx.getString(R.string.reply);
        else
        	replies = obj.getRepliesCount() + " " + ctx.getString(R.string.replies);
        return replies;
	}
	
	public void setObject(Conversation obj) {
		this.obj = obj;
	}
}
