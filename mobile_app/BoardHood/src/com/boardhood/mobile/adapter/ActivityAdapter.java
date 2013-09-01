package com.boardhood.mobile.adapter;

import java.util.List;

import com.boardhood.api.model.Conversation;
import com.boardhood.mobile.R;
import com.boardhood.mobile.parser.ActivityParser;
import com.boardhood.mobile.widget.ProfileImageView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ActivityAdapter extends ArrayAdapter<Conversation> {
	private Activity context;
	private ActivityParser parser;

	public ActivityAdapter(Activity context, List<Conversation> activities) {
		super(context, R.layout.listitem_activity, activities);
		this.context = context;
		parser = new ActivityParser(context);
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ActivityHolder holder = null;

        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.listitem_activity, parent, false);
            holder = new ActivityHolder(view);
            view.setTag(holder);
        } else {
            holder = (ActivityHolder) view.getTag();
        }

        Conversation item = getItem(position);
        parser.setObject(item);
        
        holder.getDescription().setText(parser.getTitle());
        holder.getInfo().setText(parser.getInfo());
        
        if (item.getUser() != null && item.getUser().getAvatar() != null) {
        	holder.getAvatar().setImageUrl(item.getUser().getAvatar());
	    	holder.getAvatar().loadImage();
        } else {
    		holder.getAvatar().setNoImageDrawable(R.drawable.no_avatar);
    	}

        return view;
    }
	
	private class ActivityHolder {
        View base;
        TextView description = null;
        TextView info = null;
        ProfileImageView avatar = null;

        public ActivityHolder(View base) {
            this.base = base;
        }

        public TextView getDescription() {
            if (description == null) {
            	description = (TextView) base.findViewById(R.id.activity_description);
            }
            return description;
        }
        
        public TextView getInfo() {
            if (info == null) {
            	info = (TextView) base.findViewById(R.id.activity_info);
            }
            return info;
        }
        
        public ProfileImageView getAvatar() {
            if (avatar == null) {
            	avatar = (ProfileImageView) base.findViewById(R.id.useritem_avatar);
            }
            return avatar;
        }
    }
}
