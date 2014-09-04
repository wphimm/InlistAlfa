package co.inlist.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;
import co.inlist.adapter.EventsAdapter;
import co.inlist.adapter.TitleNavigationAdapter;
import co.inlist.interfaces.AsyncTaskCompleteListener;
import co.inlist.serverutils.WebServiceDataPosterAsyncTask;
import co.inlist.util.Constant;
import co.inlist.util.MyProgressbar;
import co.inlist.util.UtilInList;

@SuppressLint("NewApi")
public class HomeScreenActivity extends Activity implements
		ActionBar.OnNavigationListener, OnRefreshListener,
		AsyncTaskCompleteListener {

	// action bar
	private static ActionBar actionBar;

	// Navigation adapter
	private static TitleNavigationAdapter adapter;

	private static PullToRefreshLayout mPullToRefreshLayout;

	int selected_position = 0;
	public static HomeScreenActivity HomeScreenObj;
	private static EventsAdapter adapterEvents;
	private Context context = this;
	public static boolean flagReset, flagIfProgress;

	private ArrayList<String> listEvents = new ArrayList<String>();
	private ArrayList<HashMap<String, String>> party_area = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homescreen);

		getData();
		onCreateData();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		invalidateOptionsMenu();
	}

	private void getData() {
		// TODO Auto-generated method stub
		String result = UtilInList.ReadSharePrefrence(HomeScreenActivity.this,
				Constant.SHRED_PR.KEY_RESULT_PARTY_AREA);
		if (result != null) {
			try {
				JSONObject jObject = new JSONObject(result);
				String str_temp = jObject.getString("status");

				party_area.clear();

				if (str_temp.equals("success")) {
					JSONArray data = jObject.getJSONArray("data");
					Log.e("Length of json array ----->", "" + data.length());
					for (int i = 0; i < data.length(); i++) {
						JSONObject obj = data.getJSONObject(i);
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("party_area_id",
								"" + obj.getString("party_area_id"));
						map.put("title", "" + obj.getString("title"));
						map.put("icon", "" + obj.getString("icon"));
						map.put("latitude", "" + obj.getString("latitude"));
						map.put("longitude", "" + obj.getString("longitude"));
						map.put("timezone", "" + obj.getString("timezone"));
						map.put("is_dst", "" + obj.getString("is_dst"));
						map.put("timezone_text",
								"" + obj.getString("timezone_text"));
						map.put("order", "" + obj.getString("order"));
						map.put("status", "" + obj.getString("status"));
						map.put("distance", "" + obj.getString("distance"));

						party_area.add(map);
					}
				}
			} catch (JSONException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void onCreateData() {
		// TODO Auto-generated method stub

		actionBar = getActionBar();
		// Hide the action bar title
		actionBar.setDisplayShowTitleEnabled(false);

		// Enabling Spinner dropdown navigation
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// title drop down adapter
		adapter = new TitleNavigationAdapter(getApplicationContext(),
				party_area);
		// assigning the spinner navigation
		actionBar.setListNavigationCallbacks(adapter, this);

		HomeScreenObj = this;

		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		mPullToRefreshLayout.setDivider(null);
		mPullToRefreshLayout.setDividerHeight(0);

		ActionBarPullToRefresh.from(this).options(Options.create().build())
				.allChildrenArePullable().listener(this)
				.setup(mPullToRefreshLayout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);

		MenuItem action_profile = menu.findItem(R.id.action_profile);
		MenuItem action_settings = menu.findItem(R.id.action_settings);
		MenuItem action_terms = menu.findItem(R.id.action_terms);
		MenuItem action_login = menu.findItem(R.id.action_login);
		MenuItem action_signup = menu.findItem(R.id.action_signup);
		MenuItem action_concierge = menu.findItem(R.id.action_concierge);

		action_profile.setVisible(true);
		action_settings.setVisible(true);
		action_terms.setVisible(true);
		action_login.setVisible(true);
		action_signup.setVisible(true);
		action_concierge.setVisible(true);

		if (UtilInList.ReadSharePrefrence(HomeScreenActivity.this,
				Constant.SHRED_PR.KEY_LOGIN_STATUS).equals("true")) {

			action_login.setVisible(false);
			action_signup.setVisible(false);
			action_terms.setTitle("Logout");

		} else {
			action_concierge.setVisible(false);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case R.id.action_profile:
			// search action

			if (UtilInList.ReadSharePrefrence(HomeScreenActivity.this,
					Constant.SHRED_PR.KEY_LOGIN_STATUS).equals("true")) {

				startActivity(new Intent(HomeScreenActivity.this,
						ProfileActivity.class));
				overridePendingTransition(R.anim.enter_from_bottom,
						R.anim.hold_bottom);

			} else {
				makeAlert();
			}

			return true;
		case R.id.action_settings:

			if (UtilInList.ReadSharePrefrence(HomeScreenActivity.this,
					Constant.SHRED_PR.KEY_LOGIN_STATUS).equals("true")) {

				startActivity(new Intent(HomeScreenActivity.this,
						SettingAccountListActivity.class));
				overridePendingTransition(R.anim.enter_from_bottom,
						R.anim.hold_bottom);

			} else {
				makeAlert();
			}

			return true;

		case R.id.action_concierge:
			// location found
			if (UtilInList.ReadSharePrefrence(HomeScreenActivity.this,
					Constant.SHRED_PR.KEY_LOGIN_STATUS).equals("true")) {

				makeDialogAlert();

			} else {
				makeAlert();
			}
			return true;

		case R.id.action_login:

			UtilInList.WriteSharePrefrence(HomeScreenActivity.this,
					Constant.SHRED_PR.KEY_LOGIN_FROM, "1");

			startActivity(new Intent(HomeScreenActivity.this,
					LoginActivity.class));
			overridePendingTransition(R.anim.enter_from_bottom,
					R.anim.hold_bottom);

			return true;

		case R.id.action_signup:

			startActivity(new Intent(HomeScreenActivity.this,
					SignUpActivity.class));
			overridePendingTransition(R.anim.enter_from_bottom,
					R.anim.hold_bottom);

			return true;

		case R.id.action_terms:

			if (UtilInList.ReadSharePrefrence(HomeScreenActivity.this,
					Constant.SHRED_PR.KEY_LOGIN_STATUS).equals("true")) {

				AlertDialog.Builder alert = new AlertDialog.Builder(
						HomeScreenActivity.this);
				alert.setTitle(Constant.AppName);
				alert.setMessage("Are you sure you want to logout?");
				alert.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (UtilInList
										.isInternetConnectionExist(getApplicationContext())) {

									List<NameValuePair> params = new ArrayList<NameValuePair>();

									params.add(new BasicNameValuePair(
											"device_id",
											""
													+ UtilInList
															.getDeviceId(getApplicationContext())));
									params.add(new BasicNameValuePair(
											"device_type", "android"));
									params.add(new BasicNameValuePair(
											"PHPSESSIONID",
											""
													+ UtilInList
															.ReadSharePrefrence(
																	HomeScreenActivity.this,
																	Constant.SHRED_PR.KEY_SESSIONID)));

									new WebServiceDataPosterAsyncTask(
											HomeScreenActivity.this,
											params,
											Constant.API
													+ "user/logout/?apiMode=VIP&json=true")
											.execute();

								} else {
									UtilInList.validateDialog(
											HomeScreenActivity.this, ""
													+ Constant.network_error,
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

			} else {

				UtilInList.WriteSharePrefrence(HomeScreenActivity.this,
						Constant.SHRED_PR.KEY_TERMS_FROM, "1");
				startActivity(new Intent(HomeScreenActivity.this,
						TermsConditionsActivity.class));
				overridePendingTransition(R.anim.enter_from_bottom,
						R.anim.hold_bottom);
			}

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
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
						HomeScreenActivity.this);
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
				String strSubject = ""
						+ UtilInList.ReadSharePrefrence(
								getApplicationContext(),
								Constant.SHRED_PR.KEY_FIRSTNAME)
						+ " "
						+ UtilInList.ReadSharePrefrence(
								getApplicationContext(),
								Constant.SHRED_PR.KEY_LASTNAME)
						+ " Re: General Questions";

				String strExtra = "\n\n\n\n\nContact Information:\n\n"
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

	/*
	 * Actionbar navigation item select listener
	 */
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// Action to be taken after selecting a spinner item

		if (party_area.get(itemPosition).get("status").equals("0")) {
			UtilInList.validateDialog(HomeScreenActivity.this, "" + ""
					+ Constant.ERRORS.NO_EVENTS_FOUND, Constant.ERRORS.OOPS);
			return false;
		}

		selected_position = itemPosition;

		if (UtilInList.isInternetConnectionExist(getApplicationContext())) {
			new PartyAreaAsyncTask(HomeScreenActivity.this).execute("");
		} else {
			UtilInList.validateDialog(HomeScreenActivity.this, "" + ""
					+ Constant.network_error, Constant.ERRORS.OOPS);
		}

		return false;
	}

	public class PartyAreaAsyncTask extends AsyncTask<String, String, String> {

		private MyProgressbar dialog;

		public PartyAreaAsyncTask(Context context) {
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
							+ Constant.ACTIONS.PARTY_AREA_SET
							+ "?apiMode=VIP&json=true"
							+ "&party_area_id="
							+ party_area.get(selected_position).get(
									"party_area_id")
							+ "&PHPSESSIONID="
							+ UtilInList.ReadSharePrefrence(
									HomeScreenActivity.this,
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
						dialog.cancel();
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (result != null) {
				try {
					JSONObject jObject = new JSONObject(result);
					String str_temp = jObject.getString("status");

					if (str_temp.equals("success")) {

						UtilInList.WriteSharePrefrence(
								HomeScreenActivity.this,
								Constant.SHRED_PR.KEY_SESSIONID,
								jObject.getJSONObject("session")
										.getJSONObject("userInfo")
										.getString("sessionId"));

						UtilInList.WriteSharePrefrence(
								HomeScreenActivity.this,
								Constant.SHRED_PR.KEY_VIP_STATUS,
								jObject.getJSONObject("session")
										.getJSONObject("userInfo")
										.getString("vip_status"));

						if (UtilInList
								.isInternetConnectionExist(getApplicationContext())) {
							flagReset = true;
							flagIfProgress = true;
							HomeScreenObj.new EventsAsyncTask(
									HomeScreenActivity.this).execute("");
						} else {
							UtilInList.validateDialog(HomeScreenActivity.this,
									"" + "" + Constant.network_error,
									Constant.ERRORS.OOPS);
							mPullToRefreshLayout.setRefreshComplete();
						}
					}

				} catch (JSONException e) { // TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

	private void makeAlert() {

		final CharSequence[] items = { "Log In", "Sign Up" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Account");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				if (items[item].equals("Log In")) {
					UtilInList.WriteSharePrefrence(HomeScreenActivity.this,
							Constant.SHRED_PR.KEY_LOGIN_FROM, "1");
					startActivity(new Intent(HomeScreenActivity.this,
							LoginActivity.class));
					overridePendingTransition(R.anim.enter_from_bottom,
							R.anim.hold_bottom);
				} else {
					startActivity(new Intent(HomeScreenActivity.this,
							SignUpActivity.class));
					overridePendingTransition(R.anim.enter_from_bottom,
							R.anim.hold_bottom);
				}
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();

	}

	public class EventsAsyncTask extends AsyncTask<String, String, String> {

		private MyProgressbar dialog;

		public EventsAsyncTask(Context context) {
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
				pageNo = ((int) (listEvents.size() / 10)) + 1;
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			Log.e("Name Value Pair", nameValuePairs.toString());
			String response = UtilInList.postData(
					getApplicationContext(),
					nameValuePairs,
					""
							+ Constant.API
							+ Constant.ACTIONS.EVENTS
							+ pageNo
							+ "/?apiMode=VIP&json=true"
							+ "&PHPSESSIONID="
							+ UtilInList.ReadSharePrefrence(
									HomeScreenActivity.this,
									Constant.SHRED_PR.KEY_SESSIONID));

			Log.e("Response In Activity-->", response);
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// fragment_addconnection_search

			mPullToRefreshLayout.setRefreshComplete();
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
					listEvents.clear();
				}
				try {
					reload(result);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

	public void reload(String result) throws JSONException {
		// TODO Auto-generated method stub
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

					if (obj.getString("quote_allowed").toString().equals("1")) {
						Toast.makeText(getApplicationContext(), "position:"+i,
								Toast.LENGTH_SHORT).show();
					}
					
					if (flagReset) {
						listEvents.add("" + obj);
					} else {
						adapterEvents.add("" + obj);
					}
				}

			} else {

				UtilInList.validateDialog(HomeScreenActivity.this, jObject
						.getJSONArray("errors").getString(0),
						Constant.ERRORS.OOPS);
			}

		} catch (JSONException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (flagReset) {
			adapterEvents = new EventsAdapter(listEvents, this,
					HomeScreenActivity.this);
			mPullToRefreshLayout.setAdapter(adapterEvents);

		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		if (UtilInList.ReadSharePrefrence(HomeScreenActivity.this,
				Constant.SHRED_PR.KEY_LOGIN_STATUS).equals("true")) {
			try {
				LeadingActivity.laObj.finish();
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
			overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
		}
	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		if (UtilInList.isInternetConnectionExist(HomeScreenActivity.this)) {
			flagReset = true;
			flagIfProgress = false;
			HomeScreenObj.new EventsAsyncTask(HomeScreenActivity.this)
					.execute("");
		} else {
			UtilInList.validateDialog(HomeScreenActivity.this, "" + ""
					+ Constant.network_error, Constant.ERRORS.OOPS);
		}
	}

	@Override
	public void onTaskComplete(String result1) {
		// TODO Auto-generated method stub
		JSONObject result = null;
		try {
			result = new JSONObject(result1);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			if (result.getString("success").equals("true")) {

				UtilInList.WriteSharePrefrence(HomeScreenActivity.this,
						Constant.SHRED_PR.KEY_LOGIN_STATUS, "false");

				UtilInList.WriteSharePrefrence(HomeScreenActivity.this,
						Constant.SHRED_PR.KEY_USER_CARD_ADDED, "0");

				invalidateOptionsMenu();

			} else {
				UtilInList.validateDialog(HomeScreenActivity.this, result
						.getJSONArray("errors").getString(0),
						Constant.ERRORS.OOPS);
			}
		} catch (Exception e) {
			Log.v("", "Exception : " + e);
		}
	}

}
