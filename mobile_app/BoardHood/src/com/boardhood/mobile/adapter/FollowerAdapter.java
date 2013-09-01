package com.boardhood.mobile.adapter;

import java.util.List;

import com.boardhood.api.model.User;
import com.boardhood.mobile.R;
import com.boardhood.mobile.widget.ProfileImageView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FollowerAdapter extends ArrayAdapter<User> {
	private Activity context;

	public FollowerAdapter(Activity context, List<User> followers) {
		super(context, R.layout.listitem_follower, followers);
		this.context = context;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        InterestHolder holder = null;

        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.listitem_follower, parent, false);
            holder = new InterestHolder(view);
            view.setTag(holder);
        } else {
            holder = (InterestHolder) view.getTag();
        }

        User item = getItem(position);
        holder.getName().setText(item.getName());

        if (item.getAvatar() != null) {
        	holder.getAvatar().setImageUrl(item.getAvatar());
	    	holder.getAvatar().loadImage();
        } else {
    		holder.getAvatar().setNoImageDrawable(R.drawable.no_avatar);
    	}

        return view;
    }
	
	private class InterestHolder {
        View base;
        TextView name = null;
        ProfileImageView avatar = null;

        public InterestHolder(View base) {
            this.base = base;
        }

        public TextView getName() {
            if (name == null) {
                name = (TextView) base.findViewById(R.id.follower_name);
            }
            return name;
        }
        
        public ProfileImageView getAvatar() {
            if (avatar == null) {
            	avatar = (ProfileImageView) base.findViewById(R.id.followeritem_avatar);
            }
            return avatar;
        }
    }
}
