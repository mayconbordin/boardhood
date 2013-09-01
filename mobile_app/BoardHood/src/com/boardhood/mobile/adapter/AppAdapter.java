package com.boardhood.mobile.adapter;

import java.util.List;

import com.boardhood.mobile.R;

import android.app.Activity;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppAdapter extends ArrayAdapter<ResolveInfo> {
	private Activity context;

	public AppAdapter(Activity context, List<ResolveInfo> apps) {
		super(context, R.layout.listitem_app, apps);
		this.context = context;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        AppHolder holder = null;

        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.listitem_app, parent, false);
            holder = new AppHolder(view);
            view.setTag(holder);
        } else {
            holder = (AppHolder) view.getTag();
        }

        ResolveInfo item = getItem(position);
        holder.getIcon().setImageDrawable(item.loadIcon(context.getPackageManager()));
        holder.getName().setText(item.loadLabel(context.getPackageManager()));

        return view;
    }
	
	private class AppHolder {
        View base;
        ImageView icon = null;
        TextView name = null;

        public AppHolder(View base) {
            this.base = base;
        }

        public TextView getName() {
            if (name == null) {
                name = (TextView) base.findViewById(R.id.app_name);
            }
            return name;
        }

        public ImageView getIcon() {
            if (icon == null) {
            	icon = (ImageView) base.findViewById(R.id.app_icon);
            }
            return icon;
        }
    }
}
