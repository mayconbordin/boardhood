package com.boardhood.mobile.adapter;

import java.util.List;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Interest;
import com.boardhood.mobile.R;
import com.boardhood.mobile.widget.FollowButton;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class InterestAdapter extends ArrayAdapter<Interest> {
	private Activity context;
	private BoardHood client;

	public InterestAdapter(Activity context, List<Interest> interests) {
		super(context, R.layout.listitem_interest, interests);
		this.context = context;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        InterestHolder holder = null;

        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.listitem_interest, parent, false);
            holder = new InterestHolder(view);
            view.setTag(holder);
        } else {
            holder = (InterestHolder) view.getTag();
        }

        Interest item = getItem(position);

        holder.getName().setText(item.getName());
        holder.getFollow().setup(item, client);

        return view;
    }
	
	private class InterestHolder {
        View base;
        TextView name = null;
        FollowButton follow = null;

        public InterestHolder(View base) {
            this.base = base;
        }

        public TextView getName() {
            if (name == null) {
                name = (TextView) base.findViewById(R.id.interest_name);
            }
            return name;
        }

        public FollowButton getFollow() {
            if (follow == null) {
            	follow = (FollowButton) base.findViewById(R.id.interest_follow);
            }
            return follow;
        }
    }
	
	public void setClient(BoardHood client) {
		this.client = client;
	}
}
