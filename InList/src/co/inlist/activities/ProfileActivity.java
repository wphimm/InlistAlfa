package co.inlist.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import co.inlist.adapter.ReservedEventsAdapter;
import co.inlist.serverutils.WebServiceDataPosterAsyncTask;
import co.inlist.util.Constant;
import co.inlist.util.MyProgressbar;
import co.inlist.util.UtilInList;

@SuppressLint("SimpleDateFormat")
public class ProfileActivity extends Activity implements
		ActionBar.OnNavigationListener {

	TextView txtName, txtEmail, txtPhone, txtNoEvents, txtSomething;
	public static ProfileActivity profObj;
	RelativeLayout relativeCategories, relativeArchive, relativeVip;
	LinearLayout linearNoEvents;
	private TextView txt_vip_membership_req;

	public static boolean flagReset, flagIfProgress;
	View viewCategories, viewArchive;
	private static PullToRefreshLayout mPullToRefreshLayout;
	ReservedEventsAdapter adapterReservedEvents;
	ReservedEventsAdapter adapterArchieveEvents;

	Typeface typeAkzidgrobeligex, typeAkzidgrobemedex, typeAvenir,
			typeLeaguegothic_condensedregular;

	public ArrayList<String> listReservedEvents = new ArrayList<String>();
	public ArrayList<String> listArchieveEvents = new ArrayList<String>();
	String todaysDate = "";
	public boolean selectedArchieve = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		profObj = this;

		init();

		actionBarAndButtonActions();

		txtName.setTypeface(typeAkzidgrobemedex);
		txtEmail.setTypeface(typeAkzidgrobemedex);
		txtPhone.setTypeface(typeAkzidgrobemedex);
		txt_vip_membership_req.setTypeface(typeAkzidgrobemedex);
		txtNoEvents.setTypeface(typeAkzidgrobeligex);
		txtSomething.setTypeface(typeAkzidgrobemedex);

		txtName.setText(""
				+ UtilInList.ReadSharePrefrence(getApplicationContext(),
						Constant.SHRED_PR.KEY_FIRSTNAME)
				+ " "
				+ UtilInList.ReadSharePrefrence(getApplicationContext(),
						Constant.SHRED_PR.KEY_LASTNAME));
		txtEmail.setText(""
				+ UtilInList.ReadSharePrefrence(getApplicationContext(),
						Constant.SHRED_PR.KEY_EMAIL));
		txtPhone.setText(""
				+ UtilInList.ReadSharePrefrence(getApplicationContext(),
						Constant.SHRED_PR.KEY_PHONE));

		if (UtilInList
				.ReadSharePrefrence(ProfileActivity.this,
						Constant.SHRED_PR.KEY_VIP_STATUS).toString()
				.equals("vip")) {

			relativeVip.setVisibility(View.GONE);

		} else if (UtilInList
				.ReadSharePrefrence(ProfileActivity.this,
						Constant.SHRED_PR.KEY_VIP_STATUS).toString()
				.equals("pending")) {

			relativeVip.setVisibility(View.VISIBLE);
			txt_vip_membership_req.setText("PENDING VIP REQUEST");

		} else {

			relativeVip.setVisibility(View.VISIBLE);
			txt_vip_membership_req.setText("REQUEST VIP MEMBERSHIP");

		}

		Calendar c = Calendar.getInstance();
		int dd = c.get(Calendar.DATE);
		int MM = c.get(Calendar.MONTH) + 1;
		int yyyy = c.get(Calendar.YEAR);
		String strMM = "" + MM;
		if (MM < 10)
			strMM = "0" + MM;
		todaysDate = strMM + "/" + dd + "/" + yyyy;

		adapterReservedEvents = new ReservedEventsAdapter(listReservedEvents,
				ProfileActivity.this, ProfileActivity.this);
		adapterArchieveEvents = new ReservedEventsAdapter(listArchieveEvents,
				ProfileActivity.this, ProfileActivity.this);

		Handler hn = new Handler();
		hn.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (UtilInList
						.isInternetConnectionExist(getApplicationContext())) {
					flagReset = true;
					flagIfProgress = true;
					new ReservationEventsAsyncTask(ProfileActivity.this)
							.execute("");
				} else {
					UtilInList.validateDialog(ProfileActivity.this, "" + ""
							+ Constant.network_error, Constant.ERRORS.OOPS);
				}

			}
		}, 100);

		relativeCategories.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				viewCategories.setVisibility(View.VISIBLE);
				viewArchive.setVisibility(View.GONE);
				txtNoEvents.setText("NO UPCOMING EVENTS");

				selectedArchieve = false;
				adapterReservedEvents = new ReservedEventsAdapter(
						listReservedEvents, ProfileActivity.this,
						ProfileActivity.this);
				if (listReservedEvents.size() == 0) {
					mPullToRefreshLayout.setVisibility(View.GONE);
				} else {
					mPullToRefreshLayout.setVisibility(View.VISIBLE);
					mPullToRefreshLayout.setAdapter(adapterReservedEvents);
				}
			}
		});

		relativeArchive.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				viewArchive.setVisibility(View.VISIBLE);
				viewCategories.setVisibility(View.GONE);
				txtNoEvents.setText("NO ARCHIVE EVENTS");

				selectedArchieve = true;
				adapterArchieveEvents = new ReservedEventsAdapter(
						listArchieveEvents, ProfileActivity.this,
						ProfileActivity.this);
				if (listArchieveEvents.size() == 0) {
					mPullToRefreshLayout.setVisibility(View.GONE);
				} else {
					mPullToRefreshLayout.setVisibility(View.VISIBLE);
					mPullToRefreshLayout.setAdapter(adapterArchieveEvents);
				}
			}
		});

		relativeVip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!UtilInList
						.ReadSharePrefrence(ProfileActivity.this,
								Constant.SHRED_PR.KEY_VIP_STATUS).toString()
						.equals("non-vip")) {

					AlertDialog.Builder alert = new AlertDialog.Builder(
							ProfileActivity.this);
					alert.setTitle(Constant.AppName);
					alert.setMessage("Please e-mail concierge@inlist.com for any questions regarding your pending VIP request");
					alert.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});

					alert.create();
					alert.show();

				} else {
					UtilInList.WriteSharePrefrence(ProfileActivity.this,
							Constant.SHRED_PR.KEY_ADDCARD_FROM, "0");
					startActivity(new Intent(ProfileActivity.this,
							VipMemberShipActivity.class));
					overridePendingTransition(R.anim.enter_from_left,
							R.anim.hold_bottom);
				}
			}
		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			if (adapterReservedEvents != null) {
				if (selectedArchieve)
					adapterArchieveEvents.refresh();
				else
					adapterReservedEvents.refresh();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void init() {
		// TODO Auto-generated method stub
		txtName = (TextView) findViewById(R.id.txtName);
		txtEmail = (TextView) findViewById(R.id.txtEmail);
		txtPhone = (TextView) findViewById(R.id.txtPhone);
		txtNoEvents = (TextView) findViewById(R.id.txtNoEvents);
		txtSomething = (TextView) findViewById(R.id.txtSomething);
		relativeCategories = (RelativeLayout) findViewById(R.id.linearCategory);
		relativeArchive = (RelativeLayout) findViewById(R.id.linearArchive);
		relativeVip = (RelativeLayout) findViewById(R.id.relativeVIP);
		linearNoEvents = (LinearLayout) findViewById(R.id.linearNoEvents);
		viewCategories = (View) findViewById(R.id.viewCategories);
		viewArchive = (View) findViewById(R.id.viewArchive);
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		mPullToRefreshLayout.setDivider(null);
		mPullToRefreshLayout.setDividerHeight(0);

		typeAkzidgrobeligex = Typeface.createFromAsset(getAssets(),
				"akzidgrobemedex.ttf");
		typeAkzidgrobemedex = Typeface.createFromAsset(getAssets(),
				"helve_unbold.ttf");
		typeLeaguegothic_condensedregular = Typeface.createFromAsset(
				getAssets(), "leaguegothic_condensedregular.otf");
		typeAvenir = Typeface.createFromAsset(getAssets(), "avenir.ttc");

		txt_vip_membership_req = (TextView) findViewById(R.id.txt_vip_membership_req);

	}

	public class ReservationEventsAsyncTask extends
			AsyncTask<String, String, String> {

		private MyProgressbar dialog;

		public ReservationEventsAsyncTask(Context context) {
			dialog = new MyProgressbar(context);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.setMessage("Loading...");
			dialog.setCanceledOnTouchOutside(false);
			if (flagIfProgress)
				dialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			int pageNo = 1;
			if (flagReset) {
				pageNo = 1;
			} else {
				pageNo = ((int) ((listReservedEvents.size() + listArchieveEvents
						.size()) / 10)) + 1;
			}

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("page", "" + pageNo));
			params.add(new BasicNameValuePair("sort_option", "by_date"));
			params.add(new BasicNameValuePair("device_type", "android"));
			params.add(new BasicNameValuePair("PHPSESSIONID", ""
					+ UtilInList.ReadSharePrefrence(ProfileActivity.this,
							Constant.SHRED_PR.KEY_SESSIONID)));

			String response = UtilInList.postData(getApplicationContext(),
					params, Constant.API + Constant.ACTIONS.RESERVATION_LIST);
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
						dialog.cancel();
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (result != null) {
				if (flagReset) {
					listReservedEvents.clear();
				}

				try {
					JSONObject jObject = new JSONObject(result);
					String str_temp = jObject.getString("status");
					if (str_temp.equals("success")) {
						JSONObject jObjectData = new JSONObject(
								jObject.getString("data"));
						JSONArray data = jObjectData.getJSONArray("entries");
						Log.e("Length of json array ----->", "" + data.length());
						for (int i = 0; i < data.length(); i++) {
							JSONObject obj = data.getJSONObject(i);

							boolean flag = true;
							for (int j = 0; j < listReservedEvents.size(); j++) {
								JSONObject jObj = new JSONObject(
										listReservedEvents.get(j));
								if (obj.getString("order_id").equals(
										jObj.getString("order_id")))
									flag = false;
							}
							for (int j = 0; j < listArchieveEvents.size(); j++) {
								JSONObject jObj = new JSONObject(
										listArchieveEvents.get(j));
								if (obj.getString("order_id").equals(
										jObj.getString("order_id")))
									flag = false;
							}

							if (flag) {
								SimpleDateFormat sdf = new SimpleDateFormat(
										"MM/dd/yyyy");
								try {
									Date date1 = sdf
											.parse(""
													+ obj.getString("event_start_date"));
									Date date2 = sdf.parse(todaysDate);

									if (date1.compareTo(date2) >= 0) {
										// Upcoming Events:
										if (flagReset) {
											listReservedEvents.add("" + obj);
										} else {
											adapterReservedEvents.add("" + obj);
										}
									} else {
										// Archieve Events:
										if (flagReset) {
											listArchieveEvents.add("" + obj);
										} else {
											adapterArchieveEvents.add("" + obj);
										}
									}

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

					} else {

						UtilInList.validateDialog(ProfileActivity.this, jObject
								.getJSONArray("errors").getString(0),
								Constant.ERRORS.OOPS);
					}

				} catch (JSONException e) { // TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.e("size:", "" + listReservedEvents.size() + "::"
						+ listArchieveEvents.size());
				if (selectedArchieve) {
					if (flagReset) {
						adapterArchieveEvents = new ReservedEventsAdapter(
								listArchieveEvents, ProfileActivity.this,
								ProfileActivity.this);
						if (listArchieveEvents.size() == 0) {
							mPullToRefreshLayout.setVisibility(View.GONE);
						} else {
							mPullToRefreshLayout.setVisibility(View.VISIBLE);
							mPullToRefreshLayout
									.setAdapter(adapterArchieveEvents);
						}
					}
				} else {
					if (flagReset) {
						adapterReservedEvents = new ReservedEventsAdapter(
								listReservedEvents, ProfileActivity.this,
								ProfileActivity.this);
						if (listReservedEvents.size() == 0) {
							mPullToRefreshLayout.setVisibility(View.GONE);
						} else {
							mPullToRefreshLayout.setVisibility(View.VISIBLE);
							mPullToRefreshLayout
									.setAdapter(adapterReservedEvents);
						}
					}
				}
			}

			try {
				JSONObject jObj = new JSONObject(result);
				UtilInList.WriteSharePrefrence(ProfileActivity.this,
						Constant.SHRED_PR.KEY_SESSIONID,
						jObj.getJSONObject("session").getJSONObject("userInfo")
								.getString("sessionId"));
				UtilInList.WriteSharePrefrence(ProfileActivity.this,
						Constant.SHRED_PR.KEY_VIP_STATUS,
						jObj.getJSONObject("session").getJSONObject("userInfo")
								.getString("vip_status"));

			} catch (Exception e) {
				// TODO: handle exception
			}

			if (UtilInList
					.ReadSharePrefrence(ProfileActivity.this,
							Constant.SHRED_PR.KEY_VIP_STATUS).toString()
					.equals("vip")) {

				relativeVip.setVisibility(View.GONE);

			} else if (UtilInList
					.ReadSharePrefrence(ProfileActivity.this,
							Constant.SHRED_PR.KEY_VIP_STATUS).toString()
					.equals("pending")) {

				relativeVip.setVisibility(View.VISIBLE);
				txt_vip_membership_req.setText("PENDING VIP REQUEST");

			} else {

				relativeVip.setVisibility(View.VISIBLE);
				txt_vip_membership_req.setText("REQUEST VIP MEMBERSHIP");

			}

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
			finish();
			overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
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

		RelativeLayout relativeActionBar = (RelativeLayout) actionBar
				.getCustomView().findViewById(R.id.relativeActionBar);
		ImageButton action_button = (ImageButton) actionBar.getCustomView()
				.findViewById(R.id.btn_action_bar);
		TextView txtVIP = (TextView) actionBar.getCustomView().findViewById(
				R.id.txtVIP);
		txtVIP.setTypeface(typeAkzidgrobemedex);

		if (UtilInList
				.ReadSharePrefrence(ProfileActivity.this,
						Constant.SHRED_PR.KEY_VIP_STATUS).toString()
				.equals("vip")) {

			txtVIP.setVisibility(View.VISIBLE);

		} else {
			txtVIP.setVisibility(View.GONE);
		}

		action_button.setBackgroundResource(R.drawable.edit_onclick);

		action_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(ProfileActivity.this,
						EditProfileActivity.class));
				overridePendingTransition(R.anim.enter_from_left,
						R.anim.hold_bottom);
			}
		});

		relativeActionBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(ProfileActivity.this,
						EditProfileActivity.class));
				overridePendingTransition(R.anim.enter_from_left,
						R.anim.hold_bottom);
			}
		});

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
	}

}
