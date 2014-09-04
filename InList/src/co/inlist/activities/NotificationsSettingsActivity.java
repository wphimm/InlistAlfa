package co.inlist.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import co.inlist.interfaces.AsyncTaskCompleteListener;
import co.inlist.serverutils.WebServiceDataPosterAsyncTask;
import co.inlist.util.Constant;
import co.inlist.util.MyProgressbar;
import co.inlist.util.UtilInList;

public class NotificationsSettingsActivity extends Activity implements
		ActionBar.OnNavigationListener, AsyncTaskCompleteListener {

	ImageButton btnDailyNotification, btnBillingIssues;
	boolean flagDaily = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_settings_screen);

		init();

		actionBarAndButtonActions();
		btnDailyNotification.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flagDaily = true;
				if (UtilInList
						.isInternetConnectionExist(getApplicationContext())) {

					String strSwitch, strType;
					if (flagDaily) {
						strType = "daily";
						if (UtilInList
								.ReadSharePrefrence(
										NotificationsSettingsActivity.this,
										Constant.SHRED_PR.KEY_DAILY).toString()
								.equals("1")) {
							strSwitch = "disable";
						} else {
							strSwitch = "enable";
						}
					} else {
						strType = "billing";
						if (UtilInList
								.ReadSharePrefrence(
										NotificationsSettingsActivity.this,
										Constant.SHRED_PR.KEY_BILLING)
								.toString().equals("1")) {
							strSwitch = "disable";
						} else {
							strSwitch = "enable";
						}
					}

					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("type", "" + strType));
					params.add(new BasicNameValuePair("device_type", "android"));
					params.add(new BasicNameValuePair("PHPSESSIONID", ""
							+ UtilInList.ReadSharePrefrence(
									NotificationsSettingsActivity.this,
									Constant.SHRED_PR.KEY_SESSIONID)));

					new WebServiceDataPosterAsyncTask(
							NotificationsSettingsActivity.this, params,
							Constant.API + Constant.ACTIONS.PUSHNOTIFICATIONS
									+ strSwitch + "/?apiMode=VIP&json=true")
							.execute();
				} else {
					UtilInList.validateDialog(
							NotificationsSettingsActivity.this, "" + ""
									+ Constant.network_error,
							Constant.ERRORS.OOPS);

				}

			}
		});

		btnBillingIssues.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flagDaily = false;
				if (UtilInList
						.isInternetConnectionExist(getApplicationContext())) {

					String strSwitch, strType;
					if (flagDaily) {
						strType = "daily";
						if (UtilInList
								.ReadSharePrefrence(
										NotificationsSettingsActivity.this,
										Constant.SHRED_PR.KEY_DAILY).toString()
								.equals("1")) {
							strSwitch = "disable";
						} else {
							strSwitch = "enable";
						}
					} else {
						strType = "billing";
						if (UtilInList
								.ReadSharePrefrence(
										NotificationsSettingsActivity.this,
										Constant.SHRED_PR.KEY_BILLING)
								.toString().equals("1")) {
							strSwitch = "disable";
						} else {
							strSwitch = "enable";
						}
					}

					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("type", "" + strType));
					params.add(new BasicNameValuePair("device_type", "android"));
					params.add(new BasicNameValuePair("PHPSESSIONID", ""
							+ UtilInList.ReadSharePrefrence(
									NotificationsSettingsActivity.this,
									Constant.SHRED_PR.KEY_SESSIONID)));

					new WebServiceDataPosterAsyncTask(
							NotificationsSettingsActivity.this, params,
							Constant.API + Constant.ACTIONS.PUSHNOTIFICATIONS
									+ strSwitch + "/?apiMode=VIP&json=true")
							.execute();

				} else {
					UtilInList.validateDialog(
							NotificationsSettingsActivity.this, "" + ""
									+ Constant.network_error,
							Constant.ERRORS.OOPS);

				}

			}
		});
	}

	private void init() {
		// TODO Auto-generated method stub
		btnDailyNotification = (ImageButton) findViewById(R.id.btnDailyNotification);
		btnBillingIssues = (ImageButton) findViewById(R.id.btnBillingIssues);

		if (UtilInList
				.ReadSharePrefrence(NotificationsSettingsActivity.this,
						Constant.SHRED_PR.KEY_DAILY).toString().equals("1")) {
			btnDailyNotification.setBackgroundResource(R.drawable.on);
		} else {
			btnDailyNotification.setBackgroundResource(R.drawable.off);
		}

		if (UtilInList
				.ReadSharePrefrence(NotificationsSettingsActivity.this,
						Constant.SHRED_PR.KEY_BILLING).toString().equals("1")) {
			btnBillingIssues.setBackgroundResource(R.drawable.on);
		} else {
			btnBillingIssues.setBackgroundResource(R.drawable.off);
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
			overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
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

				if (flagDaily) {
					if (UtilInList
							.ReadSharePrefrence(
									NotificationsSettingsActivity.this,
									Constant.SHRED_PR.KEY_DAILY).toString()
							.equals("1")) {
						btnDailyNotification
								.setBackgroundResource(R.drawable.off);
						UtilInList.WriteSharePrefrence(
								NotificationsSettingsActivity.this,
								Constant.SHRED_PR.KEY_DAILY, "0");

					} else {
						btnDailyNotification
								.setBackgroundResource(R.drawable.on);
						UtilInList.WriteSharePrefrence(
								NotificationsSettingsActivity.this,
								Constant.SHRED_PR.KEY_DAILY, "1");
					}
				} else {
					if (UtilInList
							.ReadSharePrefrence(
									NotificationsSettingsActivity.this,
									Constant.SHRED_PR.KEY_BILLING).toString()
							.equals("1")) {
						UtilInList.WriteSharePrefrence(
								NotificationsSettingsActivity.this,
								Constant.SHRED_PR.KEY_BILLING, "0");
						btnBillingIssues.setBackgroundResource(R.drawable.off);
					} else {

						if (UtilInList
								.isInternetConnectionExist(getApplicationContext())) {
							new PushNotificationTest(
									NotificationsSettingsActivity.this)
									.execute();
						}
					}
				}

			} else {
				UtilInList.validateDialog(NotificationsSettingsActivity.this,
						result.getJSONArray("errors").getString(0),
						Constant.ERRORS.OOPS);
			}
		} catch (Exception e) {
			Log.v("", "Exception : " + e);
		}

	}

	class PushNotificationTest extends AsyncTask<Void, String, String> {

		private MyProgressbar dialog;;

		public PushNotificationTest(Context context) {
			// TODO Auto-generated constructor stub
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
		protected String doInBackground(Void... params1) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("screen", "APN_USER_BILLING"));
			params.add(new BasicNameValuePair("device_id", ""
					+ UtilInList.getDeviceId(getApplicationContext())));
			params.add(new BasicNameValuePair("device_type", "android"));
			params.add(new BasicNameValuePair("PHPSESSIONID", ""
					+ UtilInList.ReadSharePrefrence(
							NotificationsSettingsActivity.this,
							Constant.SHRED_PR.KEY_SESSIONID)));

			String response = UtilInList.postData(getApplicationContext(),
					params, "" + Constant.API
							+ Constant.ACTIONS.PUSHNOTIFICATIONS_TEST);

			Log.e("Response In Activity-->", "+++++" + response);
			return response;
		}

		@Override
		protected void onPostExecute(String result1) {
			// TODO Auto-generated method stub
			super.onPostExecute(result1);

			try {
				dialog.dismiss();
			} catch (Exception e) {
				// TODO: handle exception
			}

			try {
				JSONObject result = new JSONObject(result1);
				if (result.getString("success").equals("true")) {

				} else {
					UtilInList.validateDialog(
							NotificationsSettingsActivity.this, result
									.getJSONArray("errors").getString(0),
							Constant.ERRORS.OOPS);
				}
			} catch (Exception e) {
				Log.v("", "Exception : " + e);
			}

			btnBillingIssues.setBackgroundResource(R.drawable.on);
			UtilInList.WriteSharePrefrence(NotificationsSettingsActivity.this,
					Constant.SHRED_PR.KEY_BILLING, "1");

		}

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

		action_button.setBackgroundResource(R.drawable.sign_up_action_bar);
		action_button.setVisibility(View.INVISIBLE);

		// ********** VIP Text: ******************************//
		Typeface typeAkzidgrobemedex = Typeface.createFromAsset(getAssets(),
				"helve_unbold.ttf");
		TextView txtVIP = (TextView) actionBar.getCustomView().findViewById(
				R.id.txtVIP);
		txtVIP.setTypeface(typeAkzidgrobemedex);

		if (UtilInList
				.ReadSharePrefrence(NotificationsSettingsActivity.this,
						Constant.SHRED_PR.KEY_VIP_STATUS).toString()
				.equals("vip")) {

			txtVIP.setVisibility(View.VISIBLE);

		} else {
			txtVIP.setVisibility(View.GONE);
		}
		// *****************************************************//

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
	}

}
