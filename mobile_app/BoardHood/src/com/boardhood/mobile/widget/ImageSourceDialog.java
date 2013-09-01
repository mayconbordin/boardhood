package com.boardhood.mobile.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.boardhood.mobile.R;
import com.boardhood.mobile.adapter.AppAdapter;

public class ImageSourceDialog extends android.app.Dialog {
	public static final int IMAGE_PICK     = 1;
	public static final int IMAGE_CAPTURE  = 2;
	
	private AppAdapter adapter;
	private List<ResolveInfo> list = new ArrayList<ResolveInfo>();
	private List<ImageSource> sourceList = new ArrayList<ImageSource>();
	private ListView listView;
	
	public ImageSourceDialog(final Activity context) {
		super(context);
		
		Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
		galleryIntent.setType("image/*");
		//galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
		
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		List<ResolveInfo> listCam = context.getPackageManager().queryIntentActivities(cameraIntent, 0);
		for (ResolveInfo res : listCam) {
		    final Intent finalIntent = new Intent(cameraIntent);
		    finalIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		    sourceList.add(new ImageSource(finalIntent, IMAGE_CAPTURE));
		}

		List<ResolveInfo> listGall = context.getPackageManager().queryIntentActivities(galleryIntent, 0);
		for (ResolveInfo res : listGall) {
		    final Intent finalIntent = new Intent(galleryIntent);
		    finalIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		    sourceList.add(new ImageSource(finalIntent, IMAGE_PICK));
		}
		
		list.addAll(listCam);
		list.addAll(listGall);
		
		
		for (int i=0; i<list.size(); i++) {
			if (list.get(i).activityInfo.name.equals("com.android.fallback.Fallback")) {
				list.remove(i);
				sourceList.remove(i);
			}
		}
		
		setContentView(R.layout.listview_apps);
		setTitle("Pick a source");
		
		adapter = new AppAdapter(context, this.list);
		listView = (ListView) findViewById(R.id.apps_list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i("ImageSourceDialog", "Starting app " + list.get(position).activityInfo.name);
				context.startActivityForResult(sourceList.get(position).intent, sourceList.get(position).type);
			}
		});
	}
	
	private class ImageSource {
		public Intent intent;
		public int type;
		
		public ImageSource(Intent intent, int type) {
			this.intent = intent;
			this.type = type;
		}
	}
}
