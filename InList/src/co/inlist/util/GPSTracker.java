package co.inlist.util;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;


public class GPSTracker extends Service implements LocationListener {

	long firstGPSFixTime = 0;
	long latestGPSFixTime = 0;
	long previousGPSFixTime = 0;
	@SuppressWarnings("unused")
	private boolean locationDetermined = false;
	int locAccuracy;
	long locationTimeStored = 0;
	boolean gps_found_msg_showed=false;

	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

	// The minimum distance to change Updates in meters
	//private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters

	// The minimum time between updates in milliseconds
	//  private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	private static final long MIN_TIME_BW_UPDATES = 0; // 0 sec

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPSTracker(Context context) {
		this.mContext = context;
		getLocation();
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}


	public boolean checkLoc() {
		//Log.d(LOG_TAG, "Entry :: checkLoc");
		float tempAccuracy = location.getAccuracy();
		locAccuracy = (int) tempAccuracy;
		// get time - store the GPS time the first time
		// it is reported, then check it against future reported times
		latestGPSFixTime = location.getTime();
		if (firstGPSFixTime == 0) {
			firstGPSFixTime = latestGPSFixTime;
		}
		if (previousGPSFixTime == 0) {
			previousGPSFixTime = latestGPSFixTime;
		}
		long timeDiffSecs = (latestGPSFixTime - previousGPSFixTime) / 1000;

		// Check our location - no good if the GPS accuracy is more than 24m
		if ((locAccuracy > 24) || (timeDiffSecs == 0)) {
			if (timeDiffSecs == 0) {
				// nor do we want to report if the GPS time hasn't changed at
				// all - it is probably out of date
				Log.d(Constant.AppName,"Waiting for a GPS fix: phone says last fix is out of date. Please make sure you can see the sky.");
			} else {
				Log.d(Constant.AppName,"Waiting for a GPS fix: phone says last fix had accuracy of "
						+ locAccuracy
						+ "m. (We need accuracy of 24m.) Please make sure you can see the sky.");
			}
		} else if (locAccuracy == 0) {
			// or if no accuracy data is available
			Log.d(Constant.AppName,"Waiting for a GPS fix... Please make sure you can see the sky.");
		} else {
			// but if all the requirements have been met, proceed
			previousGPSFixTime = latestGPSFixTime;
			return true;
		}
		previousGPSFixTime = latestGPSFixTime;
		Log.d(Constant.AppName, "Exit :: checkLoc");
		return false;
	}

	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * */
	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude(){
		if(location != null){
			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude(){
		if(location != null){
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		this.location=location;
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}