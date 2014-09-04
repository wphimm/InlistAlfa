package co.inlist.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import co.inlist.util.Constant;
import co.inlist.util.GPSTracker;
import co.inlist.util.MyProgressbar;
import co.inlist.util.UtilInList;
import co.inlist.util.ViewPagerCustomDuration;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

@SuppressLint("SimpleDateFormat")
public class EventDetailsActivity extends Activity implements
		ActionBar.OnNavigationListener {

	public static EventDetailsActivity edObj;
	private ScrollView scrollMain;
	private RelativeLayout relative_zoom_map, relative_google_map;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	private RelativeLayout relativeThumb, relativeConcierge, relativeTable;
	private View viewTable;
	private ImageButton btnDirection;
	private TextView txt_event_title;
	private TextView txt_event_location_city;
	private ImageView img_event_poster_url;
	private TextView txt_date_time;
	private TextView txt_details;
	private TextView txt_atmosphere;
	private TextView txt_music;
	private Spinner spinnerTable;
	private TextView txt_minimum;
	private TextView txt_MinimumDetails;
	private TextView txtaddress;
	private TextView txtcity;

	private GoogleMap googleMap, zoomMap;
	String resultlistEvents;
	HashMap<String, String> map;
	Context context = this;
	ViewPagerCustomDuration pager;
	int pagerPosition = 0;
	Timer timer;
	MyTimerTask myTimerTask;
	GestureDetector tapGestureDetector;

	Double latitude, longitude;
	public ArrayList<String> pricing = new ArrayList<String>();
	public ArrayList<HashMap<String, String>> gallery = new ArrayList<HashMap<String, String>>();

	@SuppressWarnings({ "deprecation", "unused" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_details);

		init();
		onCreateData();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		actionBarAndButtonActions();
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		myTimerTask = new MyTimerTask();
		timer.schedule(myTimerTask, 3000, 3000);

		if (UtilInList.ReadSharePrefrence(EventDetailsActivity.this,
				Constant.SHRED_PR.KEY_LOGIN_STATUS).equals("true")) {
			relativeConcierge.setVisibility(View.VISIBLE);
		} else {
			relativeConcierge.setVisibility(View.GONE);
		}
	}

	private void onCreateData() {
		// TODO Auto-generated method stub
		edObj = this;

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			resultlistEvents = extras.getString("resultlistEvents");
		}

		try {
			JSONObject obj = new JSONObject(resultlistEvents);
			map = new HashMap<String, String>();
			map.put("event_id", "" + obj.getString("event_id"));
			map.put("event_title", "" + obj.getString("event_title"));
			map.put("event_start_date", "" + obj.getString("event_start_date"));
			map.put("event_start_time", "" + obj.getString("event_start_time"));
			map.put("event_min_price", "" + obj.getString("event_min_price"));
			map.put("card_required", "" + obj.getString("card_required"));
			map.put("quote_allowed", "" + obj.getString("quote_allowed"));
			map.put("event_description",
					"" + obj.getString("event_description"));

			map.put("event_location_address",
					"" + obj.getString("event_location_address"));
			map.put("event_location_city",
					"" + obj.getString("event_location_city"));
			map.put("event_location_state",
					"" + obj.getString("event_location_state"));
			map.put("event_location_zip",
					"" + obj.getString("event_location_zip"));
			map.put("event_location_latitude",
					"" + obj.getString("event_location_latitude"));
			map.put("event_location_longitude",
					"" + obj.getString("event_location_longitude"));
			map.put("event_location_club",
					"" + obj.getString("event_location_club"));
			try {
				map.put("event_end_time", "" + obj.getString("event_end_time"));
				map.put("tables_total", "" + obj.getString("tables_total"));
				map.put("tables_available",
						"" + obj.getString("tables_available"));
			} catch (Exception e) {
				// TODO: handle exception
			}

			map.put("tax", "" + obj.getString("tax"));
			map.put("gratuity", "" + obj.getString("gratuity"));

			map.put("event_poster_url", "" + obj.getString("event_poster_url"));

			map.put("atmosphere", "" + obj.getString("atmosphere"));
			map.put("music_type", "" + obj.getString("music_type"));
			map.put("payment_type", "" + obj.getString("payment_type"));

		} catch (JSONException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		options = new DisplayImageOptions.Builder()

		.showImageOnLoading(R.drawable.event_details_overlay)
				.resetViewBeforeLoading(true)
				.showImageForEmptyUri(R.drawable.event_details_overlay)
				.showImageOnFail(R.drawable.event_details_overlay)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));

		Typeface typeAkzidgrobemedex = Typeface.createFromAsset(
				context.getAssets(), "helve_unbold.ttf");
		txt_event_title.setTypeface(typeAkzidgrobemedex);
		txt_event_location_city.setTypeface(typeAkzidgrobemedex);

		txt_event_title.setShadowLayer(2, 2, 0, Color.BLACK);
		txt_event_title.setText("" + map.get("event_title"));
		txt_event_location_city.setText("" + map.get("event_location_club")
				+ ", " + map.get("event_location_city"));

		String image_url = "" + map.get("event_poster_url");
		imageLoader.displayImage(image_url, img_event_poster_url, options);

		pager = (ViewPagerCustomDuration) findViewById(R.id.pager);

		// ***** Date Format ************************************//
		String strDate = "" + map.get("event_start_date");
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date date1;

		try {
			date1 = sdf.parse(strDate);

			SimpleDateFormat format = new SimpleDateFormat("d");
			String date = format.format(date1);

			if (date.endsWith("1") && !date.endsWith("11"))
				format = new SimpleDateFormat("EEE, MMM d'st'");
			else if (date.endsWith("2") && !date.endsWith("12"))
				format = new SimpleDateFormat("EEE, MMM d'nd'");
			else if (date.endsWith("3") && !date.endsWith("13"))
				format = new SimpleDateFormat("EEE, MMM d'rd'");
			else
				format = new SimpleDateFormat("EEE, MMM d'th'");

			strDate = format.format(date1);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String strDateTime = "" + strDate;
		try {
			String strStartTime = ""
					+ map.get("event_start_time").replace(" ", "");
			if (!strStartTime.equals("null")) {
				strDateTime = "" + strDate + " " + strStartTime;
				String strEndTime = ""
						+ map.get("event_end_time").replace(" ", "");
				if (strEndTime.equals("null")) {
					txt_date_time.setText("" + strDate + " " + strStartTime);
					strDateTime = "" + strDate + " " + strStartTime;
				} else {
					txt_date_time.setText("" + strDate + " " + strStartTime
							+ " - " + strEndTime);
					strDateTime = "" + strDate + " " + strStartTime + " - "
							+ strEndTime;
				}
			}
		} catch (Exception e) {
			Log.v("", "Exception : " + e);
		}
		txt_date_time.setText("" + strDateTime);

		// ***** Date Format ************************************//

		txt_details.setText("" + map.get("event_description") + " ");

		try {
			UtilInList.makeTextViewResizable(txt_details, 3, "MORE", true);
		} catch (Exception e) {
			// TODO: handle exception
		}

		txt_atmosphere.setText("" + map.get("atmosphere"));
		txt_music.setText("" + map.get("music_type"));

		txt_minimum.setText("$" + map.get("event_min_price"));

		txtaddress.setText("" + map.get("event_location_address"));
		txtcity.setText("" + map.get("event_location_city") + ", "
				+ map.get("event_location_state") + " "
				+ map.get("event_location_zip"));

		// ****** Payment Type **********************//
		if (map.get("payment_type").equals("at_door")) {
			txt_MinimumDetails.setText(""
					+ getResources().getString(R.string.your_minimum2));
		} else {
			txt_MinimumDetails.setText(""
					+ getResources().getString(R.string.your_minimum1));
		}
		// ***************************************** //

		if (map.get("quote_allowed").equals("1")) {
			viewTable.setVisibility(View.GONE);
			relativeTable.setVisibility(View.GONE);
		} else {
			viewTable.setVisibility(View.VISIBLE);
			relativeTable.setVisibility(View.VISIBLE);
		}

		// ********** Google Map ************//

		googleMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		if (googleMap != null) {

			googleMap.getUiSettings().setZoomControlsEnabled(false);
			googleMap.getUiSettings().setCompassEnabled(false);

			latitude = Double.parseDouble(""
					+ map.get("event_location_latitude"));
			longitude = Double.parseDouble(""
					+ map.get("event_location_longitude"));

			LatLng HAMBURG = new LatLng(latitude, longitude);
			Marker humburg = googleMap.addMarker(new MarkerOptions().position(
					HAMBURG).icon(
					BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));

			googleMap
					.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(HAMBURG) // Sets the center of the map to Mountain
										// View
					.zoom(15) // Sets the zoom
					.bearing(90) // Sets the orientation of the camera to east
					.tilt(30) // Sets the tilt of the camera to 30 degrees
					.build(); // Creates a CameraPosition from the builder
			googleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
		}

		zoomMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.zoom_map)).getMap();

		if (zoomMap != null) {

			zoomMap.getUiSettings().setCompassEnabled(false);
			latitude = Double.parseDouble(""
					+ map.get("event_location_latitude"));
			longitude = Double.parseDouble(""
					+ map.get("event_location_longitude"));

			LatLng HAMBURG = new LatLng(latitude, longitude);
			Marker humburg = zoomMap.addMarker(new MarkerOptions().position(
					HAMBURG).icon(
					BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
			zoomMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));
			zoomMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(HAMBURG) // Sets the center of the map to Mountain
										// View
					.zoom(15) // Sets the zoom
					.bearing(90) // Sets the orientation of the camera to east
					.tilt(30) // Sets the tilt of the camera to 30 degrees
					.build(); // Creates a CameraPosition from the builder
			zoomMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
		}

		if (googleMap != null) {
			googleMap.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng arg0) {
					// TODO Auto-generated method stub
					ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f,
							0.0f, 1.0f, Animation.RELATIVE_TO_SELF,
							(float) 0.0, Animation.RELATIVE_TO_SELF,
							(float) 1.0);
					animation.setDuration(500);

					relativeConcierge.setVisibility(View.GONE);
					relative_zoom_map.setVisibility(View.VISIBLE);
					relative_zoom_map.setAnimation(animation);
				}
			});
		}

		// ******************************************** //

		btnDirection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GPSTracker gps = new GPSTracker(EventDetailsActivity.this);
				startActivity(new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?saddr="
								+ gps.getLatitude() + "," + gps.getLongitude()
								+ "&daddr=" + latitude + "," + longitude)));
			}
		});

		relativeThumb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (gallery.size() > 0) {
					startActivity(new Intent(EventDetailsActivity.this,
							GalleryActivity.class));
					overridePendingTransition(R.anim.enter_from_left,
							R.anim.hold_bottom);
				}
			}
		});

		relativeConcierge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				makeDialogAlert();
			}
		});

		tapGestureDetector = new GestureDetector(this, new TapGestureListener());
		pager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				tapGestureDetector.onTouchEvent(event);
				return false;
			}
		});

		Handler hn = new Handler();
		hn.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (UtilInList
						.isInternetConnectionExist(getApplicationContext())) {
					new EventEntryAsyncTask(EventDetailsActivity.this)
							.execute("");
				} else {
					UtilInList
							.validateDialog(EventDetailsActivity.this, "" + ""
									+ Constant.network_error,
									Constant.ERRORS.OOPS);
				}
			}
		}, 500);
	}

	private void makeDialogAlert() {

		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.concierge_dialog_screen);
		dialog.show();

		LinearLayout linearCall = (LinearLayout) dialog
				.findViewById(R.id.linearCall);
		LinearLayout linearEmail = (LinearLayout) dialog
				.findViewById(R.id.linearEmail);

		linearCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alert = new AlertDialog.Builder(
						EventDetailsActivity.this);
				alert.setTitle("Confirmation Required");
				alert.setMessage("Are you sure you want to call the concierge at 8887057714 ?");
				alert.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								Intent callIntent = new Intent(
										Intent.ACTION_CALL);
								callIntent.setData(Uri.parse("tel:8887057714"));
								startActivity(callIntent);
							}
						});
				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.cancel();
							}
						});
				alert.create();
				alert.show();

			}
		});

		linearEmail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialog.cancel();
				String sharingString = ""
						+ txt_event_title.getText().toString() + "\n"
						+ txt_date_time.getText().toString() + "\n"
						+ map.get("event_location_club") + "\n"
						+ txtaddress.getText().toString() + "\n"
						+ txtcity.getText().toString();

				String strSubject = ""
						+ UtilInList.ReadSharePrefrence(
								getApplicationContext(),
								Constant.SHRED_PR.KEY_FIRSTNAME)
						+ " "
						+ UtilInList.ReadSharePrefrence(
								getApplicationContext(),
								Constant.SHRED_PR.KEY_LASTNAME)
						+ " Re: General Questions";

				String strExtra = sharingString
						+ "\n\n\n\n\nContact Information:\n\n"
						+ ""
						+ UtilInList.ReadSharePrefrence(
								getApplicationContext(),
								Constant.SHRED_PR.KEY_FIRSTNAME)
						+ " "
						+ UtilInList.ReadSharePrefrence(
								getApplicationContext(),
								Constant.SHRED_PR.KEY_LASTNAME)
						+ "\n"
						+ ""
						+ UtilInList.ReadSharePrefrence(
								getApplicationContext(),
								Constant.SHRED_PR.KEY_EMAIL)
						+ "\n"
						+ UtilInList.ReadSharePrefrence(
								getApplicationContext(),
								Constant.SHRED_PR.KEY_PHONE) + "\n\n";

				Intent emailIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				emailIntent.setType("message/rfc822");

				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						strSubject);
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, ""
						+ strExtra);
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						new String[] { "concierge@inlist.com" });
				startActivity(Intent.createChooser(emailIntent, "Email:"));
			}
		});
	}

	private void init() {
		// TODO Auto-generated method stub
		scrollMain = (ScrollView) findViewById(R.id.scrollmain);
		relativeThumb = (RelativeLayout) findViewById(R.id.r1);
		relativeTable = (RelativeLayout) findViewById(R.id.r3);
		viewTable = (View) findViewById(R.id.v3);
		relativeConcierge = (RelativeLayout) findViewById(R.id.relativeConcierge);
		relative_google_map = (RelativeLayout) findViewById(R.id.relative_google_map);
		relative_zoom_map = (RelativeLayout) findViewById(R.id.relative_zoom_map);
		txt_event_title = (TextView) findViewById(R.id.event_title);
		txt_event_title.setShadowLayer(2, 2, 0, Color.BLACK);
		txt_event_location_city = (TextView) findViewById(R.id.event_location_city);
		img_event_poster_url = (ImageView) findViewById(R.id.img);
		txt_date_time = (TextView) findViewById(R.id.txt_date_time);
		txt_details = (TextView) findViewById(R.id.txt_details);
		btnDirection = (ImageButton) findViewById(R.id.btnDirection);

		txt_atmosphere = (TextView) findViewById(R.id.txt_atmosphere);
		txt_music = (TextView) findViewById(R.id.txt_music);
		spinnerTable = (Spinner) findViewById(R.id.spinnerTable);
		txt_minimum = (TextView) findViewById(R.id.txt_minimum);
		txt_MinimumDetails = (TextView) findViewById(R.id.txtMinimumDetails);
		txtaddress = (TextView) findViewById(R.id.txtaddress);
		txtcity = (TextView) findViewById(R.id.txtcity);
	}

	class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// Your Code here
			if (gallery.size() > 0) {
				startActivity(new Intent(EventDetailsActivity.this,
						GalleryActivity.class));
				overridePendingTransition(R.anim.enter_from_left,
						R.anim.hold_bottom);
			}
			return false;
		}
	}

	public class MyAdapter extends ArrayAdapter<String> {

		ArrayList<String> local;

		public MyAdapter(Context context, int textViewResourceId,
				ArrayList<String> spinner_data1) {
			super(context, textViewResourceId, spinner_data1);
			local = spinner_data1;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.spinnertable_row, parent, false);
			}
			TextView label = (TextView) convertView
					.findViewById(R.id.spinnerTarget);
			JSONObject jObj;
			try {
				jObj = new JSONObject(local.get(position));
				label.setText(jObj.getString("club_section_name"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return convertView;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.spinnertable_row, parent, false);
			}
			TextView label = (TextView) convertView
					.findViewById(R.id.spinnerTarget);

			JSONObject jObj;
			try {
				jObj = new JSONObject(local.get(position));
				label.setText(jObj.getString("club_section_name"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return convertView;
		}
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private LayoutInflater inflater;
		ArrayList<HashMap<String, String>> locallist;

		private int[] pageIDsArray;
		private int count;

		ImagePagerAdapter(ArrayList<HashMap<String, String>> list,
				final ViewPager pager, int... pageIDs) {
			super();
			int actualNoOfIDs = pageIDs.length;
			count = actualNoOfIDs + 2;
			pageIDsArray = new int[count];
			for (int i = 0; i < actualNoOfIDs; i++) {
				pageIDsArray[i + 1] = pageIDs[i];
			}
			pageIDsArray[0] = pageIDs[actualNoOfIDs - 1];
			pageIDsArray[count - 1] = pageIDs[0];

			pager.setOnPageChangeListener(new OnPageChangeListener() {

				public void onPageSelected(int position) {

					int pageCount = getCount();
					if (position == 0) {
						pager.setCurrentItem(pageCount - 2, false);
					} else if (position == pageCount - 1) {
						pager.setCurrentItem(1, false);
					}
				}

				public void onPageScrolled(int position, float positionOffset,
						int positionOffsetPixels) {
					// TODO Auto-generated method stub
				}

				public void onPageScrollStateChanged(int state) {
					// TODO Auto-generated method stub
				}
			});

			locallist = list;
			inflater = getLayoutInflater();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}

		@Override
		public int getCount() {
			return count;
		}

		@SuppressLint("NewApi")
		@Override
		public Object instantiateItem(View view, int position) {
			int pageId = pageIDsArray[position];

			final View imageLayout = inflater.inflate(pageId, null);

			final ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);

			imageLoader.displayImage(
					""
							+ locallist.get(position % locallist.size()).get(
									"source"), imageView, options);

			((ViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
			// TODO Auto-generated method stub
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
			// TODO Auto-generated method stub
		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View container) {
			// TODO Auto-generated method stub
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			if (timer != null) {
				timer.cancel();
			}

			if (myTimerTask != null) {
				myTimerTask.cancel();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (relative_zoom_map.getVisibility() == View.VISIBLE) {

			ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 1.0f,
					0.0f, Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 1.0);
			animation.setDuration(500);

			relative_zoom_map.startAnimation(animation);
			relative_zoom_map.setVisibility(View.GONE);
			scrollMain.setVisibility(View.VISIBLE);
			relative_google_map.setVisibility(View.VISIBLE);
			relativeConcierge.setVisibility(View.VISIBLE);
		} else {
			super.onBackPressed();
			finish();
			overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				public void run() {
					pagerPosition++;

					pager.setScrollDurationFactor(4);
					if (pagerPosition == 0) {
						pager.setCurrentItem(gallery.size(), false);
					} else if (pagerPosition == gallery.size() + 1) {
						pager.setCurrentItem(1, false);
						pagerPosition = 0;
					} else {
						pager.setCurrentItem(pagerPosition);
					}

					if (pagerPosition == 4)
						pagerPosition = 0;

				}
			});
		}
	}

	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {

		case android.R.id.home:
			if (relative_zoom_map.getVisibility() == View.VISIBLE) {

				ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 1.0f,
						0.0f, Animation.RELATIVE_TO_SELF, (float) 0.0,
						Animation.RELATIVE_TO_SELF, (float) 1.0);
				animation.setDuration(500);

				relative_zoom_map.startAnimation(animation);
				relative_zoom_map.setVisibility(View.GONE);
				scrollMain.setVisibility(View.VISIBLE);
				relative_google_map.setVisibility(View.VISIBLE);
				relativeConcierge.setVisibility(View.VISIBLE);
			} else {
				finish();
				overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class EventEntryAsyncTask extends AsyncTask<String, String, String> {

		private MyProgressbar dialog;

		public EventEntryAsyncTask(Context context) {
			dialog = new MyProgressbar(context);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.setMessage("Loading...");
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			Log.e("Name Value Pair", nameValuePairs.toString());
			String response = UtilInList.postData(
					getApplicationContext(),
					nameValuePairs,
					""
							+ Constant.API
							+ "event/"
							+ map.get("event_id")
							+ "/?apiMode=VIP&json=true"
							+ "&PHPSESSIONID="
							+ UtilInList.ReadSharePrefrence(
									EventDetailsActivity.this,
									Constant.SHRED_PR.KEY_SESSIONID));
			Log.e("Response In Activity-->", response);
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// fragment_addconnection_search

			try {
				if (dialog != null) {
					if (dialog.isShowing()) {
						dialog.dismiss();
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			UtilInList.WriteSharePrefrence(EventDetailsActivity.this,
					Constant.SHRED_PR.KEY_RESULT_GALLERY, "" + result);

			gallery.clear();
			pricing.clear();

			try {
				JSONObject jObject = new JSONObject(result);
				String str_temp = jObject.getString("status");
				if (str_temp.equals("success")) {
					JSONObject jObjectData = new JSONObject(
							jObject.getString("data"));
					JSONArray data = jObjectData.getJSONArray("gallery");
					Log.e("Length of json array ----->", "" + data.length());

					for (int i = 0; i < data.length(); i++) {
						JSONObject obj = data.getJSONObject(i);
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("thumbnail", "" + obj.getString("thumbnail"));
						map.put("source", "" + obj.getString("source"));
						map.put("description",
								"" + obj.getString("description"));

						gallery.add(map);
					}

					JSONArray data1 = jObjectData.getJSONArray("pricing");
					Log.e("Length of json array ----->", "" + data1.length());

					for (int i = 0; i < data1.length(); i++) {
						JSONObject obj = data1.getJSONObject(i);
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("event_pricing_id",
								"" + obj.getString("event_pricing_id"));
						map.put("club_section_name",
								"" + obj.getString("club_section_name"));
						map.put("club_section_id",
								"" + obj.getString("club_section_id"));
						map.put("table_capacity",
								"" + obj.getString("table_capacity"));
						map.put("net_price", "" + obj.getString("net_price"));
						map.put("tax", "" + obj.getString("tax"));
						map.put("gratuity", "" + obj.getString("gratuity"));
						map.put("payment_type",
								"" + obj.getString("payment_type"));
						map.put("commission", "" + obj.getString("commission"));
						map.put("price", "" + obj.getString("price"));
						map.put("total_price",
								"" + obj.getString("total_price"));
						map.put("to_pay_in_app",
								"" + obj.getString("to_pay_in_app"));

						pricing.add("" + obj);
					}

					spinnerTable.setAdapter(new MyAdapter(
							EventDetailsActivity.this,
							R.layout.spinnertable_row, pricing));

					int[] ids = new int[gallery.size()];
					for (int i = 0; i < ids.length; i++) {
						ids[i] = R.layout.item_pager_image;
					}

					pager.setAdapter(new ImagePagerAdapter(gallery, pager, ids));

				}

			} catch (JSONException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				JSONObject jObj = new JSONObject(result);
				UtilInList.WriteSharePrefrence(EventDetailsActivity.this,
						Constant.SHRED_PR.KEY_SESSIONID,
						jObj.getJSONObject("session").getJSONObject("userInfo")
								.getString("sessionId"));
				UtilInList.WriteSharePrefrence(EventDetailsActivity.this,
						Constant.SHRED_PR.KEY_VIP_STATUS,
						jObj.getJSONObject("session").getJSONObject("userInfo")
								.getString("vip_status"));

			} catch (Exception e) {
				// TODO: handle exception
			}

			if (UtilInList.isInternetConnectionExist(getApplicationContext())) {
				new PushNotificationTest().execute();
			}

		}

	}

	class PushNotificationTest extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params1) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("screen", "APN_EVENT_ENTRY"));
			params.add(new BasicNameValuePair("event_id", ""
					+ map.get("event_id")));
			params.add(new BasicNameValuePair("device_id", ""
					+ UtilInList.getDeviceId(getApplicationContext())));
			params.add(new BasicNameValuePair("device_type", "android"));
			params.add(new BasicNameValuePair("PHPSESSIONID", ""
					+ UtilInList.ReadSharePrefrence(EventDetailsActivity.this,
							Constant.SHRED_PR.KEY_SESSIONID)));

			String response = UtilInList.postData(getApplicationContext(),
					params, "" + Constant.API
							+ Constant.ACTIONS.PUSHNOTIFICATIONS_TEST);

			Log.e("Response In Activity-->", "+++++" + response);
			return null;
		}

	}

	private void actionBarAndButtonActions() {
		ActionBar actionBar = getActionBar();
		// add the custom view to the action bar
		actionBar.setCustomView(R.layout.custome_action_bar);

		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);

		actionBar.setDisplayHomeAsUpEnabled(true);

		RelativeLayout relativeActionBar = (RelativeLayout) actionBar
				.getCustomView().findViewById(R.id.relativeActionBar);
		ImageButton action_button = (ImageButton) actionBar.getCustomView()
				.findViewById(R.id.btn_action_bar);

		// action_button.setBackgroundResource(R.drawable.ev)

		// ********** VIP Text: ******************************//
		Typeface typeAkzidgrobemedex = Typeface.createFromAsset(getAssets(),
				"helve_unbold.ttf");
		TextView txtVIP = (TextView) actionBar.getCustomView().findViewById(
				R.id.txtVIP);
		txtVIP.setTypeface(typeAkzidgrobemedex);

		if (UtilInList
				.ReadSharePrefrence(EventDetailsActivity.this,
						Constant.SHRED_PR.KEY_VIP_STATUS).toString()
				.equals("vip")) {

			txtVIP.setVisibility(View.VISIBLE);

		} else {
			txtVIP.setVisibility(View.GONE);
		}
		// *****************************************************//

		relativeActionBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (spinnerTable.getSelectedItemPosition() >= 0) {
					JSONObject jObj;
					try {
						jObj = new JSONObject(pricing.get(spinnerTable
								.getSelectedItemPosition()));

						UtilInList.WriteSharePrefrence(
								EventDetailsActivity.this,
								Constant.SHRED_PR.KEY_PRICE_TABLE_CAPACITY, ""
										+ jObj.getString("table_capacity"));
						UtilInList.WriteSharePrefrence(
								EventDetailsActivity.this,
								Constant.SHRED_PR.KEY_PRICE_EVENT_PRICING_ID,
								"" + jObj.getString("event_pricing_id"));
						UtilInList.WriteSharePrefrence(
								EventDetailsActivity.this,
								Constant.SHRED_PR.KEY_PRICE_CLUB_SECTION_NAME,
								"" + jObj.getString("club_section_name"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					UtilInList.WriteSharePrefrence(EventDetailsActivity.this,
							Constant.SHRED_PR.KEY_EVENT_ID,
							"" + map.get("event_id"));
					UtilInList.WriteSharePrefrence(EventDetailsActivity.this,
							Constant.SHRED_PR.KEY_YOUR_MINIMUM,
							"" + map.get("event_min_price"));

					UtilInList.WriteSharePrefrence(EventDetailsActivity.this,
							Constant.SHRED_PR.KEY_CURRENT_resultlistEvents, ""
									+ resultlistEvents);
					UtilInList.WriteSharePrefrence(EventDetailsActivity.this,
							Constant.SHRED_PR.KEY_CARD_REQUIRED,
							"" + map.get("card_required"));

					if (UtilInList.ReadSharePrefrence(
							EventDetailsActivity.this,
							Constant.SHRED_PR.KEY_LOGIN_STATUS).equals("true")) {

						// User Already Logged in:

						if (UtilInList
								.ReadSharePrefrence(EventDetailsActivity.this,
										Constant.SHRED_PR.KEY_VIP_STATUS)
								.toString().equals("non-vip")) {

							// Need to send VIP Request:

							UtilInList.WriteSharePrefrence(
									EventDetailsActivity.this,
									Constant.SHRED_PR.KEY_ADDCARD_FROM, "1");
							startActivity(new Intent(EventDetailsActivity.this,
									VipMemberShipActivity.class));
							overridePendingTransition(R.anim.enter_from_bottom,
									R.anim.hold_bottom);

						} else {

							if (UtilInList
									.ReadSharePrefrence(
											EventDetailsActivity.this,
											Constant.SHRED_PR.KEY_USER_CARD_ADDED)
									.toString().equals("1")
									|| map.get("card_required").equals("0")) {

								// Card already added or card not required:

								startActivity(new Intent(
										EventDetailsActivity.this,
										CompletePurchaseActivity.class));
								overridePendingTransition(
										R.anim.enter_from_left,
										R.anim.hold_bottom);

							} else {

								// Card required:

								UtilInList
										.WriteSharePrefrence(
												EventDetailsActivity.this,
												Constant.SHRED_PR.KEY_ADDCARD_FROM,
												"1");
								startActivity(new Intent(
										EventDetailsActivity.this,
										AddCardActivity.class));
								overridePendingTransition(
										R.anim.enter_from_bottom,
										R.anim.hold_bottom);

							}
						}

					} else {

						// User not logged in:

						UtilInList.WriteSharePrefrence(
								EventDetailsActivity.this,
								Constant.SHRED_PR.KEY_LOGIN_FROM, "1");

						startActivity(new Intent(EventDetailsActivity.this,
								LoginActivity.class));
						overridePendingTransition(R.anim.enter_from_bottom,
								R.anim.hold_bottom);

					}
				}
			}
		});

		if (UtilInList.ReadSharePrefrence(EventDetailsActivity.this,
				Constant.SHRED_PR.KEY_LOGIN_STATUS).equals("true")) {
			action_button.setBackgroundResource(R.drawable.purchanse_onclick);
		} else {
			action_button.setBackgroundResource(R.drawable.login_onclick);
		}

	}

}
