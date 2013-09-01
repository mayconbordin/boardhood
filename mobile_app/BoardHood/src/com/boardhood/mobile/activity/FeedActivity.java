package com.boardhood.mobile.activity;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Conversation;
import com.boardhood.api.model.Coordinates;
import com.boardhood.api.util.BHArrayList;
import com.boardhood.api.util.BHList;
import com.boardhood.mobile.BoardHoodSettings;
import com.boardhood.mobile.Extra;
import com.boardhood.mobile.LocationFinder;
import com.boardhood.mobile.R;
import com.boardhood.mobile.activity.base.BoardHoodListActivity;
import com.boardhood.mobile.adapter.ConversationAdapter;
import com.boardhood.mobile.list.ListListener;
import com.boardhood.mobile.list.ListContext;
import com.boardhood.mobile.list.NormalListView;
import com.boardhood.mobile.loader.FeedTask;
import com.boardhood.mobile.widget.FilterDialog;
import com.boardhood.mobile.widget.ActionBar;
import com.boardhood.mobile.widget.FooterBar;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class FeedActivity extends BoardHoodListActivity {
		
    private BHList<Conversation> list = new BHArrayList<Conversation>();
    private ConversationAdapter adapter;
    private NormalListView listView;
    
    private View progressBar;
    
    private ListListener<Conversation> listListener;
    private ListContext<Void> listContext;
    
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        
        registerLogoutReceiver();
        
        initAttrs();
        initList();
        initListeners();
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		footerBar.setCurrentButton(FooterBar.BTN_FEED);
	}

	@SuppressWarnings("unchecked")
	public void initList() {
    	adapter = new ConversationAdapter(this, list);
        setListAdapter(adapter);
        
        listContext = new ListContext<Void>();
        listListener = new ListListener<Conversation>(this, actionBar, list, 
        		adapter, listView, listContext);
        
        listListener.setOnPreExecute(new ListListener.OnExecuteListener() {
			@Override
			public void execute() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listView.setVisibility(View.GONE);
				    	progressBar.setVisibility(View.VISIBLE);
					}
				});
			}
		});
        
        listListener.setOnPostExecute(new ListListener.OnExecuteListener() {
			@Override
			public void execute() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listView.setVisibility(View.VISIBLE);
				    	progressBar.setVisibility(View.GONE);
				    	listListener.setOnPreExecute(null);
				    	listListener.setOnPostExecute(null);
					}
				});
			}
		});
		
        new FeedTask(client, listListener, ListListener.REFRESH).execute(listContext);
    }
    
    public void initAttrs() {
    	actionBar = (ActionBar) findViewById(R.id.actionbar);
    	footerBar = (FooterBar) findViewById(R.id.footerbar);
    	footerBar.setCurrentButton(FooterBar.BTN_FEED);
    	
    	listView = (NormalListView) getListView();
    	View v = listView.setNoItemsView(R.layout.listitem_conversation_noitems);
    	((Button)v.findViewById(R.id.conversation_noitems_addinterests)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(ExploreActivity.createIntent(FeedActivity.this));
			}
		});
    	
    	progressBar = findViewById(R.id.progress_bar);
    }
    
    @SuppressWarnings("unchecked")
    public void initListeners() {
    	actionBar.setOnRefreshAction(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new FeedTask(client, listListener, ListListener.REFRESH).execute(listContext);
			}
		});
    	
    	actionBar.setFilterAction(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FilterDialog dialog = new FilterDialog(FeedActivity.this);
				dialog.setOnApplyFilterListener(new FilterDialog.OnApplyFilterListener() {
					@Override
					public void filterApplied(String orderBy, int radius) {
						setContextData(orderBy, radius);
						new FeedTask(client, listListener, ListListener.REFRESH).execute(listContext);
					}
				});
				dialog.show();
			}
		});
    	
    	listView.setOnItemClickListener(new ListView.OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i("FeedActivity", "Item clicked");
				Intent conversationIntent = ConversationActivity.createIntent(FeedActivity.this);
				conversationIntent.putExtra(Extra.CONVERSATION, (Conversation) listView.getItemAtPosition(position));
		        startActivity(conversationIntent);
    		}
    	});
    	
        listView.setOnLoadMoreListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new FeedTask(client, listListener, ListListener.LOAD_MORE).execute(listContext);
			}
		});
    }
    
    public void loadDefaultContextData() {
    	String orderBy = BoardHoodSettings.getInstance().getString(BoardHoodSettings.FILTER_ORDER_BY, BoardHood.ORDER_POPULAR);
		int radius  = BoardHoodSettings.getInstance().getInt(BoardHoodSettings.FILTER_RADIUS, 0);
		setContextData(orderBy, radius);
    }
    
    public void setContextData(String orderBy, int radius) {
    	listContext.setOrder(orderBy);
    	listContext.setRadius(radius);
		
		if (radius != 0 || orderBy == BoardHood.ORDER_DISTANCE) {
			Location l = LocationFinder.getInstance().getLastLocation();
			if (l != null) {
				Coordinates c = new Coordinates(l.getLatitude(), l.getLongitude());
				listContext.setLocation(c);
			}
		} else {
			listContext.setLocation(null);
		}
    }
    
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, FeedActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
}