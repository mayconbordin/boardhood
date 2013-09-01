package com.boardhood.mobile.list;

import java.net.ConnectException;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.boardhood.api.util.BHList;
import com.boardhood.api.util.DateUtils;
import com.boardhood.mobile.R;
import com.boardhood.mobile.utils.NetworkUtil;
import com.boardhood.mobile.utils.TaskLoader;
import com.boardhood.mobile.widget.ActionBar;

@SuppressWarnings("rawtypes")
public class ListListener<T> extends TaskLoader.TaskListener<BHList<T>> {
	public static final int REFRESH = 1;
	public static final int LOAD_MORE = 2;
	public static final int UPDATE = 3;
	public static final int REVERSE_UPDATE = 4;
	
	protected Activity activity;
	protected ActionBar actionBar;
	protected BHList<T> list;
	protected ArrayAdapter<T> adapter;
	protected ListContext loaderContext;
	protected IListView listView;
	protected int errorMessageResource = R.string.list_update_error;
	protected OnExecuteListener onPreExecute;
	protected OnExecuteListener onPostExecute;
	protected OnEmptyListListener onEmptyListListener;
	protected long startTime;
	
	public ListListener(Activity activity, ActionBar actionBar, BHList<T> list,
			ArrayAdapter<T> adapter, IListView listView, ListContext loaderContext) {
		super();
		this.activity = activity;
		this.actionBar = actionBar;
		this.list = list;
		this.adapter = adapter;
		this.loaderContext = loaderContext;
		this.listView = listView;
		
		enableDefaultEmptyListListener();
	}
	
	public ListListener(Activity activity, ActionBar actionBar, BHList<T> list,
			ArrayAdapter<T> adapter, ListContext loaderContext) {
		super();
		this.activity = activity;
		this.actionBar = actionBar;
		this.list = list;
		this.adapter = adapter;
		this.loaderContext = loaderContext;
	}
	
	public ListListener(Activity activity, ActionBar actionBar, BHList<T> list,
			ArrayAdapter<T> adapter) {
		super();
		this.activity = activity;
		this.actionBar = actionBar;
		this.list = list;
		this.adapter = adapter;
	}

	@Override
	public void onPreExecute() {
		startTime = System.currentTimeMillis();
		actionBar.showProgressBar();
		
		if (listView != null) {
			if (type == LOAD_MORE) {
				listView.startLoadingMore();
			} else if (type == REVERSE_UPDATE) {
				listView.startLoadingBottom();
			} else {
				listView.startLoadingTop();
			}
		}
		
		if (onPreExecute != null) {
			onPreExecute.execute();
		}
	}

	@Override
	public void onTaskSuccess(final BHList<T> result) {
		int time = (int) ((System.currentTimeMillis() - startTime) / 1000);
		Log.i("LoadListener", "Running time: " + time + " seconds");
		
		if (result != null) {
			Log.i("LoadListener", result.size() + " items received");
			Log.i("LoadListener", "Updated at: " + DateUtils.toIsoFormat(result.getCreatedAt()));
		
			if (result.isDiskCacheData()) {
				Toast.makeText(activity, R.string.connection_error_internet, Toast.LENGTH_SHORT).show();
			}
		}
		
		if (onPostExecute != null) {
			onPostExecute.execute();
		}
		
		activity.runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	        	if (result != null && result.size() > 0) {
	        		if (onEmptyListListener != null) {
	        			onEmptyListListener.listNotEmpty();
	        		}
	        		
		        	if (type == REFRESH) {
		            	list.clear();
		                if (loaderContext != null)
		                	loaderContext.setPage(1);
		        	}
		        	
		        	else if (type == LOAD_MORE && loaderContext != null) {
		        		loaderContext.increasePage(1);
		        	}
		        	
		        	if (type == UPDATE) {
		        		for (int i=(result.size()-1); i>=0; i--) {
		        			list.add(0, result.get(i));
		        		}
		        		
		        		//if (loaderContext != null)
		        		//	loaderContext.setAfter(result.getCreatedAt());
		        	} else {
		        		list.addAll(result);
		        	}
		        	
		        	if ((type == UPDATE || type == REVERSE_UPDATE) && loaderContext != null)
	        			loaderContext.setAfter(result.getCreatedAt());
		        	
		        	adapter.notifyDataSetChanged();
	        	} else {
	        		if (result != null && result.size() == 0) {
	        			if (type == REFRESH) {
			            	list.clear();
			                if (loaderContext != null)
			                	loaderContext.setPage(1);
			        	}
	        			adapter.notifyDataSetChanged();
	        		}
	        		
	        		if (onEmptyListListener != null && list.size() == 0) {
	        			onEmptyListListener.listIsEmpty();
	        		}
	        	}
	        	
	        	finish();
	        }
	    });
	}

	@Override
	public void onTaskFailed(Exception error) {
		error.printStackTrace();
		Log.e("LoadListener", error.toString());
		
		if (onPostExecute != null) {
			onPostExecute.execute();
		}
		
		finish();
		
		if (error instanceof ConnectException) {
			if (NetworkUtil.isOnline(activity)) {
				Toast.makeText(activity, R.string.connection_error_host, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(activity, R.string.connection_error_internet, Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(activity, errorMessageResource, Toast.LENGTH_SHORT).show();
		}
	}
	
	public void setListView(IListView listView) {
		this.listView = listView;
		enableDefaultEmptyListListener();
	}
	
	private void finish() {
		if (listView != null) {
			if (type == LOAD_MORE) {
				listView.finishLoadingMore();
			} else if (type == REVERSE_UPDATE) {
				listView.finishLoadingBottom();
			} else {
				listView.finishLoadingTop();
			}
		}

        actionBar.hideProgressBar();
	}

	public void setErrorMessageResource(int r) {
		this.errorMessageResource = r;
	}
	
	public void setOnPreExecute(OnExecuteListener onPreExecute) {
		this.onPreExecute = onPreExecute;
	}

	public void setOnPostExecute(OnExecuteListener onPostExecute) {
		this.onPostExecute = onPostExecute;
	}

	public void setOnEmptyListListener(OnEmptyListListener onEmptyListListener) {
		this.onEmptyListListener = onEmptyListListener;
	}
	
	public void enableDefaultEmptyListListener() {
		setOnEmptyListListener(new ListListener.OnEmptyListListener() {
			@Override
			public void listIsEmpty() {
				listView.showNoItemsView();
			}

			@Override
			public void listNotEmpty() {
				listView.hideNoItemsView();
			}
		});
	}
	
	public void disableDefaultEmptyListListener() {
		setOnEmptyListListener(null);
	}

	public static interface OnExecuteListener {
		public void execute();
	}
	
	public static interface OnEmptyListListener {
		public void listIsEmpty();
		public void listNotEmpty();
	}
}
