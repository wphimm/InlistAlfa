package co.inlist.activities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import co.inlist.util.Constant;
import co.inlist.util.UtilInList;

@SuppressLint({ "SimpleDateFormat", "UseValueOf" })
public class PurchaseSummaryActivity extends Activity implements
		ActionBar.OnNavigationListener {

	private Button btnFacebook, btnTwitter;

	Context context = this;
	TextView txtEventTitle, txtDate, txtAddress, txtTable, txtVenue, txtIn,
			txtFriends;
	HashMap<String, String> map;
	String sharingString = "I'm in\n";
	Typeface typeAkzidgrobemedex, typehelve_unbold;

	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase_summary);

		init();

		actionBarAndButtonActions();

		try {
			JSONObject obj = new JSONObject(UtilInList.ReadSharePrefrence(
					PurchaseSummaryActivity.this,
					Constant.SHRED_PR.KEY_CURRENT_resultlistEvents).toString());
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

		txtEventTitle.setText("" + map.get("event_title"));
		txtVenue.setText("" + map.get("event_location_club"));
		txtAddress.setText("" + map.get("event_location_address") + "\n"
				+ map.get("event_location_city") + ", "
				+ map.get("event_location_state") + " "
				+ map.get("event_location_zip"));

		String strTable = UtilInList.ReadSharePrefrence(
				PurchaseSummaryActivity.this,
				Constant.SHRED_PR.KEY_PRICE_CLUB_SECTION_NAME);
		txtTable.setText("" + strTable);

		// ***** Date Format ************************************//
		String strDate = "" + map.get("event_start_date");
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date date1;

		try {
			date1 = sdf.parse(strDate);

			SimpleDateFormat format = new SimpleDateFormat("d");
			String date = format.format(date1);

			if (date.endsWith("1") && !date.endsWith("11"))
				format = new SimpleDateFormat("EEEE, MMMM d'st'");
			else if (date.endsWith("2") && !date.endsWith("12"))
				format = new SimpleDateFormat("EEEE, MMMM d'nd'");
			else if (date.endsWith("3") && !date.endsWith("13"))
				format = new SimpleDateFormat("EEEE, MMMM d'rd'");
			else
				format = new SimpleDateFormat("EEEE, MMMM d'th'");

			strDate = format.format(date1);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		txtDate.setText("" + strDate.toUpperCase());

		// ***** Date Format ************************************//

		sharingString += "" + txtEventTitle.getText().toString() + "\n"
				+ txtDate.getText().toString() + "\n"
				+ txtVenue.getText().toString() + "\n"
				+ txtAddress.getText().toString();

		btnFacebook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(PurchaseSummaryActivity.this,
						SharingActivity.class);
				in.putExtra("flagFB", 0);
				in.putExtra("sharingString", "" + sharingString);
				in.putExtra("shareURL", "" + map.get("event_poster_url"));
				startActivity(in);
				overridePendingTransition(R.anim.enter_from_bottom,
						R.anim.hold_bottom);

			}
		});

		btnTwitter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(PurchaseSummaryActivity.this,
						SharingActivity.class);
				in.putExtra("flagFB", 1);
				in.putExtra("sharingString", "" + sharingString);
				in.putExtra("shareURL", "" + map.get("event_poster_url"));
				startActivity(in);
				overridePendingTransition(R.anim.enter_from_bottom,
						R.anim.hold_bottom);
			}
		});

		// Add Event to Calender:
		Handler hn = new Handler();
		hn.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				AddEvent();
			}
		}, 500);

	}

	protected void AddEvent() {
		// TODO Auto-generated method stub

		Calendar cal = Calendar.getInstance();
		String tz = cal.getTimeZone().getID();
		Log.i("timezone: ", ">>" + tz);

		long startMillis = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		try {
			Date mDate = sdf.parse("" + map.get("event_start_date"));
			startMillis = mDate.getTime();
			System.out.println("Date in milli :: " + startMillis);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// SET THE CONTENT VALUE
		ContentValues cv = new ContentValues();

		// MAKE SURE YOU ADD IT TO THE RIGHT CALENDAR
		cv.put("calendar_id", 1);
		cv.put("title", "" + map.get("event_title") + " -InList");
		cv.put("description", "" + map.get("event_description"));
		cv.put("eventLocation", "" + map.get("event_location_address"));

		// SET THE START AND END TIME
		// NOTE: YOU'RE GOING TO NEED TO CONVERT THE DESIRED DATE INTO
		// MILLISECONDS
		cv.put("dtstart", startMillis);
		cv.put("dtend", startMillis + DateUtils.DAY_IN_MILLIS);
		// cv.put("dtstart", System.currentTimeMillis());
		// cv.put("dtend", System.currentTimeMillis() +
		// DateUtils.DAY_IN_MILLIS);

		// LET THE CALENDAR KNOW WHETHER THIS EVENT GOES ON ALL DAY OR NOT
		// TRUE = 1, FALSE = 0
		cv.put("allDay", 1);
		// LET THE CALENDAR KNOW WHETHER AN ALARM SHOULD GO OFF FOR THIS EVENT
		cv.put("hasAlarm", 1);
		cv.put(Events.EVENT_TIMEZONE, "" + tz);

		// ONCE DESIRED FIELDS ARE SET, INSERT IT INTO THE TABLE
		Uri uri = getContentResolver().insert(
				CalendarContract.Events.CONTENT_URI, cv);
		long eventId = new Long(uri.getLastPathSegment());
		Log.e("eventId", ">>" + eventId);

	}

	private void init() {
		// TODO Auto-generated method stub
		txtEventTitle = (TextView) findViewById(R.id.txt_event_title);
		txtDate = (TextView) findViewById(R.id.txt_date);
		txtAddress = (TextView) findViewById(R.id.txt_address);
		txtTable = (TextView) findViewById(R.id.txt_table);
		txtVenue = (TextView) findViewById(R.id.txtVenue);
		txtIn = (TextView) findViewById(R.id.txtIn);
		txtFriends = (TextView) findViewById(R.id.txtFriends);

		btnFacebook = (Button) findViewById(R.id.btnFacebook);
		btnTwitter = (Button) findViewById(R.id.btnTwitter);

		typeAkzidgrobemedex = Typeface.createFromAsset(context.getAssets(),
				"akzidgrobemedex.ttf");
		typehelve_unbold = Typeface.createFromAsset(context.getAssets(),
				"helve_unbold.ttf");

		txtEventTitle.setTypeface(typehelve_unbold);
		txtDate.setTypeface(typeAkzidgrobemedex);
		txtAddress.setTypeface(typehelve_unbold);
		txtTable.setTypeface(typehelve_unbold);
		txtVenue.setTypeface(typehelve_unbold);
		txtIn.setTypeface(typeAkzidgrobemedex);
		txtFriends.setTypeface(typehelve_unbold);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {

		case android.R.id.home:
			EventDetailsActivity.edObj.finish();
			CompletePurchaseActivity.cpObj.finish();
			finish();
			overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		EventDetailsActivity.edObj.finish();
		CompletePurchaseActivity.cpObj.finish();
		finish();
		overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	private void actionBarAndButtonActions() {
		ActionBar actionBar = getActionBar();
		// add the custom view to the action bar
		actionBar.setCustomView(R.layout.custome_action_bar);

		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);

		actionBar.setDisplayHomeAsUpEnabled(true);

		ImageButton action_button = (ImageButton) actionBar.getCustomView()
				.findViewById(R.id.btn_action_bar);

		action_button.setVisibility(View.INVISIBLE);

		// ********** VIP Text: ******************************//
		Typeface typeAkzidgrobemedex = Typeface.createFromAsset(getAssets(),
				"helve_unbold.ttf");
		TextView txtVIP = (TextView) actionBar.getCustomView().findViewById(
				R.id.txtVIP);
		txtVIP.setTypeface(typeAkzidgrobemedex);

		if (UtilInList
				.ReadSharePrefrence(PurchaseSummaryActivity.this,
						Constant.SHRED_PR.KEY_VIP_STATUS).toString()
				.equals("vip")) {

			txtVIP.setVisibility(View.VISIBLE);

		} else {
			txtVIP.setVisibility(View.GONE);
		}
		// *****************************************************//
	}

}