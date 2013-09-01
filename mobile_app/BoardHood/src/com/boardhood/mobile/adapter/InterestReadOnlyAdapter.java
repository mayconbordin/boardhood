package com.boardhood.mobile.adapter;

import java.util.List;

import com.boardhood.api.model.Interest;
import com.boardhood.mobile.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class InterestReadOnlyAdapter extends ArrayAdapter<Interest> {
	private Activity context;

	public InterestReadOnlyAdapter(Activity context, List<Interest> interests) {
		super(context, R.layout.listitem_interest_readonly, interests);
		this.context = context;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        InterestHolder holder = null;

        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            
            //if (position == 0)
            //	view = inflater.inflate(R.layout.interest_readonly_top_item, parent, false);
            //else if (position == (getCount()-1))
            //	view = inflater.inflate(R.layout.interest_readonly_bottom_item, parent, false);
            //else
            
            view = inflater.inflate(R.layout.listitem_interest_readonly, parent, false);
            
            holder = new InterestHolder(view);
            view.setTag(holder);
        } else {
            holder = (InterestHolder) view.getTag();
        }

        Interest item = getItem(position);
        holder.getName().setText(item.getName());

        return view;
    }
	
	private class InterestHolder {
        View base;
        TextView name = null;

        public InterestHolder(View base) {
            this.base = base;
        }

        public TextView getName() {
            if (name == null) {
                name = (TextView) base.findViewById(R.id.interest_name);
            }
            return name;
        }
    }
}
