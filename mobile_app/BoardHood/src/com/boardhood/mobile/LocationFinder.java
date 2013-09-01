package com.boardhood.mobile;

import com.boardhood.mobile.utils.MyLocation;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationFinder {
	private static final String LAT = "lastLocationLatitude";
	private static final String LON = "lastLocationLongitude";
	private static final String TIME = "lastLocationTime";
	private static final int T_ELAPSED = 15 * 60 * 1000; // 15min
	
	private static LocationFinder instance;
	private Context context;
	private Location lastLocation;
	private MyLocation myLocation;
	private boolean lookingUp = false;
	
	public LocationFinder(Context context) {
		this.context = context.getApplicationContext();
		
        myLocation = new MyLocation();
        findLocation();
	}
	
	public void findLocation() {
		Log.i("LocationFinder", "Looking for the location...");
		myLocation.getLocation(context, locationResult);
		setLookingUp(true);
	}
	
	public void updateLocation() {
		if (!isLookingUp() && (getLastLocation() == null || 
				(lastLocation.getTime() + T_ELAPSED) < System.currentTimeMillis())) {
			findLocation();
		}		
	}
	
	public Location getLastLocation() {
		if (lastLocation == null) {
			SharedPreferences settings = BoardHoodSettings.getInstance();
			float latitude  = settings.getFloat(LAT, 0);
			float longitude = settings.getFloat(LON, 0);
			long  time      = settings.getLong(TIME, 0);
			
			if (latitude != 0 && longitude != 0 && time != 0) {
				lastLocation = new Location(LocationManager.NETWORK_PROVIDER);
				lastLocation.setLatitude(latitude);
				lastLocation.setLongitude(longitude);
				lastLocation.setTime(time);
			}
		}
		
		return lastLocation;
	}
	
	public void setLastLocation(Location location) {
		lastLocation = location;
		
		if (lastLocation != null) {
	    	SharedPreferences.Editor editor = BoardHoodSettings.getEditor();
	        editor.putFloat(LAT, (float) location.getLatitude());
	        editor.putFloat(LON, (float) location.getLongitude());
	        editor.putLong(TIME, location.getTime());
	        editor.commit();
		}
	}
	
	private MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
        @Override
        public void gotLocation(Location location) {
        	Log.i("LocationFinder", "Location: " + ((location == null) ? "null" : location.toString()));
        	setLastLocation(location);
        	setLookingUp(false);
        }
    };
    
    public synchronized boolean isLookingUp() {
    	return this.lookingUp;
    }
    
    public synchronized void setLookingUp(boolean lookingUp) {
    	this.lookingUp = lookingUp;
    }
    
    public static LocationFinder getInstance() {
    	return instance;
    }
    
    public static void startInstance(Context ctx) {
    	instance = new LocationFinder(ctx);
    }
}
