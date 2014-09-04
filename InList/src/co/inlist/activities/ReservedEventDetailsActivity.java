package co.inlist.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import co.inlist.util.Constant;
import co.inlist.util.GPSTracker;
import co.inlist.util.MyProgressbar;
import co.inlist.util.UtilInList;

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
public class ReservedEventDetailsActivity extends Activity implements
		ActionBar.OnNavigationListener {

	public static ReservedEventDetailsActivity edObj;
	private ScrollView scrollMain;
	private RelativeLayout relative_zoom_map, relative_google_map;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	@SuppressWarnings("unused")
	private RelativeLayout relativeThumb;
	private ImageButton btnDirection;
	private TextView txt_event_title;
	private TextView txt_event_location_city;
	private ImageView img_event_poster_url;
	private TextView txt_date_time;
	private TextView txt_details;
	private TextView txt_atmosphere;
	private TextView txt_music;
	private TextView txtaddress;
	private TextView txtcity, txtPoints;
	private ImageButton imgReservation;
	String strAction = "";

	private GoogleMap googleMap, zoomMap;
	String resultlistReservedEvents;
	int position;
	HashMap<String, String> map;
	Context context = this;

	Double latitude, longitude;

	@SuppressWarnings({ "deprecation", "unused" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reserved_eventdetails);

		init();

		actionBarAndButtonActions();
		edObj = this;

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			resultlistReservedEvents = extras
					.getString("resultlistReservedEvents");
			position = extras.getInt("pos");
		}

		String strHTML = "&#8226; I will arrive on-time <br/>"
				+ "&#8226; I will dress approprately for the venue <br/>";
		txtPoints.setText(Html.fromHtml(strHTML));

		JSONObject obj;
		try {
			obj = new JSONObject(resultlistReservedEvents);
			map = new HashMap<String, String>();
			map.put("order_id", "" + obj.getString("order_id"));
			map.put("event_id", "" + obj.getString("event_id"));
			map.put("event_title", "" + obj.getString("event_title"));
			map.put("event_start_date", "" + obj.getString("event_start_date"));
			map.put("event_start_time", "" + obj.getString("event_start_time"));
			map.put("event_min_price", "" + obj.getString("event_min_price"));
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
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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

		UtilInList.makeTextViewResizable(txt_details, 3, "MORE", true);

		txt_atmosphere.setText("" + map.get("atmosphere"));
		txt_music.setText("" + map.get("music_type"));

		txtaddress.setText("" + map.get("event_location_address"));
		txtcity.setText("" + map.get("event_location_city") + ", "
				+ map.get("event_location_state") + " "
				+ map.get("event_location_zip"));

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

					relative_zoom_map.setVisibility(View.VISIBLE);
					relative_zoom_map.setAnimation(animation);
					// scrollMain.setVisibility(View.GONE);
					// relative_google_map.setVisibility(View.INVISIBLE);

				}
			});
		}

		// ******************************************** //

		btnDirection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LeadingActivity.gps = new GPSTracker(
						ReservedEventDetailsActivity.this);
				startActivity(new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?saddr="
								+ LeadingActivity.gps.getLatitude() + ","
								+ LeadingActivity.gps.getLongitude()
								+ "&daddr=" + latitude + "," + longitude)));
			}
		});

		imgReservation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-enerated method stub
				if (strAction.equals("confirmation_required")) {
					if (UtilInList
							.isInternetConnectionExist(getApplicationContext())) {
						new ConfirmReservationAsyncTask(
								ReservedEventDetailsActivity.this).execute("");
					} else {
						UtilInList.validateDialog(
								ReservedEventDetailsActivity.this, "" + ""
										+ Constant.network_error,
								Constant.ERRORS.OOPS);
					}
				}
			}
		});

		Handler hn = new Handler();
		hn.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (UtilInList
						.isInternetConnectionExist(getApplicationContext())) {
					new GetStatusAsyncTask(ReservedEventDetailsActivity.this)
							.execute("");
				} else {
					UtilInList.validateDialog(
							ReservedEventDetailsActivity.this, "" + ""
									+ Constant.network_error,
							Constant.ERRORS.OOPS);
				}
			}
		}, 500);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		actionBarAndButtonActions();
	}

	private void init() {
		// TODO Auto-generated method stub
		scrollMain = (ScrollView) findViewById(R.id.scrollmain);
		relativeThumb = (RelativeLayout) findViewById(R.id.r1);
		relative_google_map = (RelativeLayout) findViewById(R.id.relative_google_map);
		relative_zoom_map = (RelativeLayout) findViewById(R.id.relative_zoom_map);
		txt_event_title = (TextView) findViewById(R.id.event_title);
		txt_event_title.setShadowLayer(2, 2, 0, Color.BLACK);
		txt_event_location_city = (TextView) findViewById(R.id.event_location_city);
		img_event_poster_url = (ImageView) findViewById(R.id.img);
		txt_date_time = (TextView) findViewById(R.id.txt_date_time);
		txt_details = (TextView) findViewById(R.id.txt_details);
		btnDirection = (ImageButton) findViewById(R.id.btnDirection);
		imgReservation = (ImageButton) findViewById(R.id.img_reservation);

		txt_atmosphere = (TextView) findViewById(R.id.txt_atmosphere);
		txt_music = (TextView) findViewById(R.id.txt_music);
		txtaddress = (TextView) findViewById(R.id.txtaddress);
		txtcity = (TextView) findViewById(R.id.txtcity);
		txtPoints = (TextView) findViewById(R.id.txt_points);
	}

	public class MyAdapter extends ArrayAdapter<HashMap<String, String>> {

		ArrayList<HashMap<String, String>> local;

		public MyAdapter(Context context, int textViewResourceId,
				ArrayList<HashMap<String, String>> spinner_data1) {
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
			label.setText(local.get(position).get("club_section_name"));

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
			label.setText(local.get(position).get("club_section_name"));

			return convertView;
		}
	}

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

			} else {
				finish();
				overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
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

	public class ConfirmReservationAsyncTask extends

	AsyncTask<String, String, String> {

		private MyProgressbar dialog;

		public ConfirmReservationAsyncTask(Context context) {
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
							+ "reservation/confirm"
							+ "/?apiMode=VIP&json=true"
							+ "&reservation_id="
							+ map.get("order_id")
							+ "&PHPSESSIONID="
							+ UtilInList.ReadSharePrefrence(
									ReservedEventDetailsActivity.this,
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

				if (result != null) {
					try {
						JSONObject jObject = new JSONObject(result);
						String str_temp = jObject.getString("status");
						if (str_temp.equals("success")) {
							UtilInList
									.validateDialog(
											ReservedEventDetailsActivity.this,
											jObject.getJSONArray("messages")
													.getString(0),
											Constant.ERRORS.OOPS);
						} else {

							UtilInList
									.validateDialog(
											ReservedEventDetailsActivity.this,
											jObject.getJSONArray("errors")
													.getString(0),
											Constant.ERRORS.OOPS);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	}

	public class GetStatusAsyncTask extends AsyncTask<String, String, String> {

		private MyProgressbar dialog;

		public GetStatusAsyncTask(Context context) {
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
							+ "reservation/status"
							+ "/?apiMode=VIP&json=true"
							+ "&reservation_id="
							+ map.get("order_id")
							+ "&PHPSESSIONID="
							+ UtilInList.ReadSharePrefrence(
									ReservedEventDetailsActivity.this,
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

				if (result != null) {
					try {
						JSONObject jObject = new JSONObject(result);
						String str_temp = jObject.getString("status");
						if (str_temp.equals("success")) {
							JSONObject jObjectData = new JSONObject(
									jObject.getString("data"));
							strAction = jObjectData.getString("action");
							imgReservation.setVisibility(View.VISIBLE);
							if (strAction.equals("show_text")) {
								imgReservation
										.setBackgroundResource(R.drawable.purchase_history_reservation_confirmed);
							} else {
								imgReservation
										.setBackgroundResource(R.drawable.purchase_history_reservation_pending);
							}

							AlertDialog.Builder alert = new AlertDialog.Builder(
									ReservedEventDetailsActivity.this);
							alert.setTitle(Constant.AppName);
							alert.setMessage("" + jObjectData.getString("text"));
							alert.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									});

							alert.create();
							alert.show();

						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

			actionBarAndButtonActions();

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

			params.add(new BasicNameValuePair("screen", "APN_RESERVATION_ENTRY"));
			params.add(new BasicNameValuePair("reservation_id", ""
					+ map.get("order_id")));
			params.add(new BasicNameValuePair("device_id", ""
					+ UtilInList.getDeviceId(getApplicationContext())));
			params.add(new BasicNameValuePair("device_type", "android"));
			params.add(new BasicNameValuePair("PHPSESSIONID", ""
					+ UtilInList.ReadSharePrefrence(
							ReservedEventDetailsActivity.this,
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
		actionBar.setCustomView(R.layout.custom_actionbar_two_buttons);

		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);

		actionBar.setDisplayHomeAsUpEnabled(true);

		RelativeLayout relativeActionBarHide = (RelativeLayout) actionBar
				.getCustomView().findViewById(R.id.relativeActionBarHide);
		ImageButton btnHide = (ImageButton) actionBar.getCustomView()
				.findViewById(R.id.btn_action_bar_hide);
		RelativeLayout relativeActionBarConfirm = (RelativeLayout) actionBar
				.getCustomView().findViewById(R.id.relativeActionBarConfirm);
		ImageButton btnConfirm = (ImageButton) actionBar.getCustomView()
				.findViewById(R.id.btn_action_bar_confirm);

		if (strAction.equals("confirmation_required")) {
			btnConfirm.setVisibility(View.VISIBLE);
			relativeActionBarConfirm.setVisibility(View.VISIBLE);
		} else {
			btnConfirm.setVisibility(View.GONE);
			relativeActionBarConfirm.setVisibility(View.GONE);
		}

		// ********** VIP Text: ******************************//
		Typeface typeAkzidgrobemedex = Typeface.createFromAsset(getAssets(),
				"helve_unbold.ttf");
		TextView txtVIP = (TextView) actionBar.getCustomView().findViewById(
				R.id.txtVIP);
		txtVIP.setTypeface(typeAkzidgrobemedex);

		if (UtilInList
				.ReadSharePrefrence(ReservedEventDetailsActivity.this,
						Constant.SHRED_PR.KEY_VIP_STATUS).toString()
				.equals("vip")) {

			txtVIP.setVisibility(View.VISIBLE);

		} else {
			txtVIP.setVisibility(View.GONE);
		}
		// *****************************************************//

		btnHide.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alert = new AlertDialog.Builder(
						ReservedEventDetailsActivity.this);
				alert.setTitle(Constant.AppName);
				alert.setMessage("Are you sure you want to hide this reservation?");
				alert.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (UtilInList
										.isInternetConnectionExist(context)) {

									new HideReservationAsyncTask(
											ReservedEventDetailsActivity.this)
											.execute("");

								} else {
									UtilInList.validateDialog(
											ReservedEventDetailsActivity.this,
											"" + Constant.network_error,
											Constant.AppName);
								}

							}
						});
				alert.setNegativeButton("NO",
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

		relativeActionBarHide.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alert = new AlertDialog.Builder(
						ReservedEventDetailsActivity.this);
				alert.setTitle(Constant.AppName);
				alert.setMessage("Are you sure you want to hide this reservation?");
				alert.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (UtilInList
										.isInternetConnectionExist(context)) {

									new HideReservationAsyncTask(
											ReservedEventDetailsActivity.this)
											.execute("");

								} else {
									UtilInList.validateDialog(
											ReservedEventDetailsActivity.this,
											"" + Constant.network_error,
											Constant.AppName);
								}

							}
						});
				alert.setNegativeButton("NO",
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

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (UtilInList
						.isInternetConnectionExist(getApplicationContext())) {
					new ConfirmReservationAsyncTask(
							ReservedEventDetailsActivity.this).execute("");
				} else {
					UtilInList.validateDialog(
							ReservedEventDetailsActivity.this, "" + ""
									+ Constant.network_error,
							Constant.ERRORS.OOPS);
				}
			}
		});

		relativeActionBarConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (UtilInList
						.isInternetConnectionExist(getApplicationContext())) {
					new ConfirmReservationAsyncTask(
							ReservedEventDetailsActivity.this).execute("");
				} else {
					UtilInList.validateDialog(
							ReservedEventDetailsActivity.this, "" + ""
									+ Constant.network_error,
							Constant.ERRORS.OOPS);
				}
			}
		});

	}

	public class HideReservationAsyncTask extends
			AsyncTask<String, String, String> {

		private MyProgressbar dialog;

		public HideReservationAsyncTask(Context context) {
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
							+ "reservation/hide"
							+ "/?apiMode=VIP&json=true"
							+ "&reservation_id="
							+ map.get("order_id")
							+ "&PHPSESSIONID="
							+ UtilInList.ReadSharePrefrence(
									ReservedEventDetailsActivity.this,
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

				if (result != null) {
					try {
						JSONObject jObject = new JSONObject(result);
						String str_temp = jObject.getString("status");
						if (str_temp.equals("success")) {
							if (ProfileActivity.profObj.selectedArchieve) {
								ProfileActivity.profObj.listArchieveEvents
										.remove(position);
							} else {
								ProfileActivity.profObj.listReservedEvents
										.remove(position);
							}
							finish();
							overridePendingTransition(R.anim.hold_top,
									R.anim.exit_in_left);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	}

}
