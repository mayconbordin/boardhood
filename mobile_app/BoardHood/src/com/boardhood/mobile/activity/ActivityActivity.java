package com.boardhood.mobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.boardhood.api.model.Conversation;
import com.boardhood.api.util.BHArrayList;
import com.boardhood.api.util.BHList;
import com.boardhood.mobile.Extra;
import com.boardhood.mobile.R;
import com.boardhood.mobile.activity.base.BoardHoodListActivity;
import com.boardhood.mobile.adapter.ActivityAdapter;
import com.boardhood.mobile.list.ListListener;
import com.boardhood.mobile.list.ListContext;
import com.boardhood.mobile.list.SimpleListView;
import com.boardhood.mobile.loader.ActivityTask;
import com.boardhood.mobile.widget.ActionBar;
import com.boardhood.mobile.widget.FooterBar;

public class ActivityActivity extends BoardHoodListActivity {
	private BHList<Conversation> list = new BHArrayList<Conversation>();
    private ActivityAdapter adapter;
	private SimpleListView listView;
	
	private ListListener<Conversation> listListener;
	private ListContext<Void> listContext;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);
        
        registerLogoutReceiver();
        
        initAttrs();
        initList();
        initListeners();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		footerBar.setCurrentButton(FooterBar.BTN_ACTIVITY);
	}
	
	public void initAttrs() {
		actionBar = (ActionBar) findViewById(R.id.actionbar);
		footerBar = (FooterBar) findViewById(R.id.footerbar);
    	footerBar.setCurrentButton(FooterBar.BTN_ACTIVITY);
    	
		listView = (SimpleListView) getListView();
		listView.setNoItemsView(R.layout.listitem_activity_noitems);
	}
	
	@SuppressWarnings("unchecked")
	public void initListeners() {
		actionBar.setOnRefreshAction(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new ActivityTask(client, listListener, ListListener.UPDATE).execute(listContext);
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent conversationIntent = ConversationActivity.createIntent(ActivityActivity.this);
				conversationIntent.putExtra(Extra.CONVERSATION, (Conversation) listView.getItemAtPosition(position));
		        startActivity(conversationIntent);
			}
        });
		
		listView.setOnLoadMoreListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new ActivityTask(client, listListener, ListListener.LOAD_MORE).execute(listContext);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public void initList() {
		adapter = new ActivityAdapter(this, list);
        setListAdapter(adapter);
        
        listContext = new ListContext<Void>();
        listListener = new ListListener<Conversation>(this, actionBar, list, adapter, listView, listContext);
        
		new ActivityTask(client, listListener, ListListener.REFRESH).execute(listContext);
	}
	
	public static Intent createIntent(Context context) {
        Intent i = new Intent(context, ActivityActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
}
