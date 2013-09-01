package com.boardhood.mobile.list;

import com.boardhood.mobile.R;
import com.boardhood.mobile.widget.LoadingBar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimpleListView extends NormalListView {
	protected int headerId = R.layout.listview_simple_header;
	protected int footerId = R.layout.listview_simple_footer;
	
	public SimpleListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SimpleListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SimpleListView(Context context) {
		super(context);
	}
	
	public void init() {
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerView = inflater.inflate(R.layout.listview_simple_header, null, false);
        footerView = inflater.inflate(R.layout.listview_simple_footer, null, false);
        
        addHeaderView(headerView, null, false);
        addFooterView(footerView, null, false);
                
        noItemsView = (LinearLayout) footerView.findViewById(R.id.listview_noitems);
        noItemsTextView = (TextView) noItemsView.findViewById(R.id.listview_noitems_text);                   
        loadMoreButton = (Button) footerView.findViewById(R.id.listview_loadmore);
        topLoadingBar = (LoadingBar) headerView.findViewById(R.id.listview_loading_bar_top);
        bottomLoadingBar = (LoadingBar) footerView.findViewById(R.id.listview_loading_bar_bottom);
        
        loadProgressBarBackground();
        
        loadMoreButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadMoreButton.setEnabled(false);
				loadMoreButton.setText(R.string.loaderlistview_updating);
				
				if (listener != null)
					listener.onClick(v);
			}
        });
	}
	
	public void loadProgressBarBackground() {
		bottomLoadingBar.setBackgroundResource(R.drawable.bottom_border_noradius_normal);
		topLoadingBar.setBackgroundResource(R.drawable.top_border_noradius_normal);
	}
}
