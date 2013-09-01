package com.boardhood.mobile.adapter;

import java.util.List;

import com.boardhood.api.model.Conversation;
import com.boardhood.mobile.Extra;
import com.boardhood.mobile.R;
import com.boardhood.mobile.activity.ProfileActivity;
import com.boardhood.mobile.parser.ConversationParser;
import com.boardhood.mobile.widget.ProfileImageView;
import com.github.ignition.core.widgets.RemoteImageView;
import com.github.ignition.support.images.remote.RemoteImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ConversationAdapter extends ArrayAdapter<Conversation> {
	private Activity context;
	private ConversationParser parser;
	private RemoteImageLoader imageLoader;
	private boolean forReplies = false;
	
	public ConversationAdapter(Activity context, List<Conversation> conversations, boolean forReplies) {
		super(context, R.layout.listitem_conversation, conversations);
		this.context = context;
	    this.forReplies = forReplies;
	    init();
	}
	
	public ConversationAdapter(Activity context, List<Conversation> conversations) {
		super(context, R.layout.listitem_conversation, conversations);
		this.context = context;
	    init();
	}
	
	private void init() {
		imageLoader = new RemoteImageLoader(context, true);
	    RemoteImageView.setSharedImageLoader(imageLoader);
	    parser = new ConversationParser(context);
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        ConversationHolder holder = null;

        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.listitem_conversation, parent, false);
            holder = new ConversationHolder(view);
            view.setTag(holder);
        } else {
            holder = (ConversationHolder) view.getTag();
        }

        final Conversation item = getItem(position);
        parser.setObject(item);

    	if (item.getUser().getAvatar() != null) {
    		holder.getAvatar().setImageUrl(item.getUser().getAvatar());
    		holder.getAvatar().loadImage();
    	} else {
    		holder.getAvatar().setNoImageDrawable(R.drawable.no_avatar);
    	}
        
    	holder.getAvatar().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("ConversationAdapter", "Go to user: " + item.getUser().getName());
				Intent intent = ProfileActivity.createIntent(context);
				intent.putExtra(Extra.USER_NAME, item.getUser().getName());
				context.startActivity(intent);
			}
		});
    	
        if (forReplies) {
        	holder.getTitle().setText(parser.getSimpleTitle());
        	holder.getRepliesCount().setText("");
        } else {
        	holder.getTitle().setText(parser.getTitle());
        	holder.getRepliesCount().setText(parser.getReplies());
        }
        
        holder.getMessage().setText(item.getMessage());
        holder.getInfo().setText(parser.getInfo());
        
        return view;
    }
	
	private class ConversationHolder {

        View base;
        TextView title = null;
        TextView message = null;
        TextView info = null;
        TextView repliesCount = null;
        ProfileImageView avatar = null;

        public ConversationHolder(View base) {
            this.base = base;
        }

        public TextView getTitle() {
            if (title == null) {
                title = (TextView) base.findViewById(R.id.conversationitem_title);
            }
            return title;
        }

        public TextView getMessage() {
            if (message == null) {
            	message = (TextView) base.findViewById(R.id.conversationitem_message);
            }
            return message;
        }
        
        public TextView getInfo() {
            if (info == null) {
            	info = (TextView) base.findViewById(R.id.conversationitem_info);
            }
            return info;
        }
        
        public TextView getRepliesCount() {
            if (repliesCount == null) {
            	repliesCount = (TextView) base.findViewById(R.id.conversationitem_repliescount);
            }
            return repliesCount;
        }

        public ProfileImageView getAvatar() {
            if (avatar == null) {
            	avatar = (ProfileImageView) base.findViewById(R.id.conversationitem_avatar);
            }
            return avatar;
        }
    }
}
