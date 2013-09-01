package com.boardhood.mobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.boardhood.api.model.Interest;
import com.boardhood.api.util.BHArrayList;
import com.boardhood.api.util.BHList;
import com.boardhood.mobile.Extra;
import com.boardhood.mobile.R;
import com.boardhood.mobile.activity.base.BoardHoodListActivity;
import com.boardhood.mobile.adapter.InterestAdapter;
import com.boardhood.mobile.list.RoundedListView;
import com.boardhood.mobile.loader.InterestAutoCompleteTask;
import com.boardhood.mobile.utils.TaskLoader.TaskListener;
import com.boardhood.mobile.widget.FooterBar;

public class ExploreActivity extends BoardHoodListActivity  {
	private BHList<Interest> list = new BHArrayList<Interest>();
    private InterestAdapter adapter;
    private RoundedListView listView;
    private EditText filterText;
    
    private String searchValue = "";
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
                 
        registerLogoutReceiver();
        
        initAttrs();
        initList();
        initListeners();
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		footerBar.setCurrentButton(FooterBar.BTN_EXPLORE);
	}
    
    public void initAttrs() {
    	footerBar = (FooterBar) findViewById(R.id.footerbar);
    	footerBar.setCurrentButton(FooterBar.BTN_EXPLORE);
    	
    	filterText = (EditText) findViewById(R.id.interest_filter);
        listView = (RoundedListView) getListView();
    }
    
    public void initListeners() {
    	if (getIntent().hasExtra(Extra.FOR_RESULT)) {
    		listView.setOnItemClickListener(new ListView.OnItemClickListener() {
    			@Override
    			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    				Intent i = getIntent();
    				i.putExtra(Extra.INTEREST, (Interest) listView.getItemAtPosition(position));
    				setResult(RESULT_OK, i);
    				finish();
    			}
            });
    	} else {
	    	listView.setOnItemClickListener(new ListView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Log.i("InterestListView", "Clicked on interest item");
					Intent interestIntent = InterestActivity.createIntent(ExploreActivity.this);
					interestIntent.putExtra(Extra.INTEREST, (Interest) listView.getItemAtPosition(position));	
					startActivity(interestIntent);
				}
	        });
    	}
    	
    	filterText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Log.i("ExploreActivity", "filter: " + s);
				
				searchValue = s.toString();
				new Handler().postDelayed(new SearchRunnable(s.toString()) {
					public void run() {
			        	 if (searchValue.equals(value)) {
			        		 Log.i("ExploreActivity", "search for: " + value);
			        		 new InterestAutoCompleteTask(client, interestTaskListener).execute(value);
			        	 }
			         }
				}, 2000);
			}
        });
    }
    
    public void initList() {
    	adapter = new InterestAdapter(this, list);
        adapter.setClient(client);
        setListAdapter(adapter);
        
        new InterestAutoCompleteTask(client, interestTaskListener).execute();
    }
    
    private TaskListener<BHList<Interest>> interestTaskListener = new TaskListener<BHList<Interest>>() {
		@Override
		public void onPreExecute() {
			listView.startLoadingTop();
		}
		
    	@Override
		public void onTaskSuccess(final BHList<Interest> result) {
			Log.i("ExploreActivity", result.size() + " items received");
			runOnUiThread(new Runnable() {
		        @Override
		        public void run() {
		        	adapter.clear();
		        	
		            if (result != null && result.size() > 0) {
		            	list.addAll(result);
		            } else {
		            	Interest i = new Interest();
		            	i.setName(searchValue);
		            	list.add(i);
		            }
		            
		            adapter.notifyDataSetChanged();
		            listView.finishLoadingTop();
		        }
		    });
		}

		@Override
		public void onTaskFailed(Exception error) {
			Log.e("ExploreActivity", "Error: " + error.getMessage() + ". " + error.toString() + ". " + error.getLocalizedMessage());
			Toast.makeText(ExploreActivity.this, R.string.conversation_update_error, Toast.LENGTH_SHORT).show();
			listView.finishLoadingBottom();
		}
    };
    
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, ExploreActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
    
    abstract class SearchRunnable implements Runnable {
    	protected String value;
    	
    	public SearchRunnable(String value) {
    		this.value = value;
    	}
    }
}
