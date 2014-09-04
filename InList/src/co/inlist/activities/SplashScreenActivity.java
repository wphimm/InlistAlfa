package co.inlist.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import co.inlist.util.Constant;
import co.inlist.util.GPSTracker;
import co.inlist.util.MyProgressbar;
import co.inlist.util.UtilInList;

import com.crittercism.app.Crittercism;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class SplashScreenActivity extends Activity {

	public static final int SPLASH_TIMEOUT = 2000;
	public static SplashScreenActivity objSplash;
	public GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);

		Crittercism.initialize(getApplicationContext(),
				"53f5d8ba07229a6bcc000008");

		objSplash = this;
		gps = new GPSTracker(getApplicationContext());

		try {
			Parse.initialize(this, Constant.YOUR_APP_ID,
					Constant.YOUR_CLIENT_KEY);
			PushService
					.setDefaultPushCallback(this, SplashScreenActivity.class);
			ParseInstallation.getCurrentInstallation().saveInBackground();
			ParseAnalytics.trackAppOpened(getIntent());

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		Handler hn = new Handler();
		hn.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (UtilInList
						.isInternetConnectionExist(getApplicationContext())) {
					new PrepRegAsyncTask().execute();
				} else {
					UtilInList.validateDialog(SplashScreenActivity.this, ""
							+ Constant.ERRORS.NO_INTERNET_CONNECTION,
							Constant.ERRORS.NO_INTERNET_CONNECTION_TITLE);

				}
			}
		}, 200);

	}

	public class AddDeviceAsyncTask extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub

			List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			params1.add(new BasicNameValuePair("device_type", "android"));
			params1.add(new BasicNameValuePair("device_id", ""
					+ UtilInList.getDeviceId(getApplicationContext())));
			params1.add(new BasicNameValuePair("parse_object_id", ""
					+ ParseInstallation.getCurrentInstallation().getObjectId()));
			params1.add(new BasicNameValuePair("PHPSESSIONID", ""
					+ UtilInList.ReadSharePrefrence(SplashScreenActivity.this,
							Constant.SHRED_PR.KEY_SESSIONID)));

			String response = UtilInList.postData(getApplicationContext(),
					params1, Constant.API + Constant.ACTIONS.ADD_DEVICE);
			Log.e("Response In Activity-->", response);

			return response;
		}

		@Override
		protected void onPostExecute(String result1) {
			// TODO Auto-generated method stub
			super.onPostExecute(result1);

			Log.e("result..", ">>" + result1);

			try {
				JSONObject result = new JSONObject(result1);
				String str_temp = result.getString("status");
				if (str_temp.equals("success")) {

					UtilInList.WriteSharePrefrence(
							SplashScreenActivity.this,
							Constant.SHRED_PR.KEY_SESSIONID,
							result.getJSONObject("session")
									.getJSONObject("userInfo")
									.getString("sessionId"));

					UtilInList.WriteSharePrefrence(
							SplashScreenActivity.this,
							Constant.SHRED_PR.KEY_VIP_STATUS,
							result.getJSONObject("session")
									.getJSONObject("userInfo")
									.getString("vip_status"));

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			checkPrefsAndSplash();

		}

	}

	class PrepRegAsyncTask extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			params1.add(new BasicNameValuePair("device_type", "android"));
			params1.add(new BasicNameValuePair("PHPSESSIONID", ""
					+ UtilInList.ReadSharePrefrence(SplashScreenActivity.this,
							Constant.SHRED_PR.KEY_SESSIONID)));

			String response = UtilInList.postData(getApplicationContext(),
					params1, Constant.API + Constant.ACTIONS.PREPARE_REGISTER);
			Log.e("Response In Activity-->", response);

			return response;
		}

		@Override
		protected void onPostExecute(String result1) {
			// TODO Auto-generated method stub
			super.onPostExecute(result1);

			UtilInList.WriteSharePrefrence(SplashScreenActivity.this,
					Constant.SHRED_PR.KEY_RESULT_MUSIC, "" + result1);
			Log.i("result1", ">>" + result1);

			try {
				/*
				 * Prepare registration response write in file mode private
				 */
				JSONObject result = new JSONObject(result1);
				UtilInList.writeToFile(result.getJSONObject("data").toString(),
						Constant.PREF_VAL.OFFLINE_FILE_PRE_REGISTER,
						SplashScreenActivity.this);

				String str_temp = result.getString("status");
				if (str_temp.equals("success")) {

					UtilInList.WriteSharePrefrence(
							SplashScreenActivity.this,
							Constant.SHRED_PR.KEY_SESSIONID,
							result.getJSONObject("session")
									.getJSONObject("userInfo")
									.getString("sessionId"));
					UtilInList.WriteSharePrefrence(
							SplashScreenActivity.this,
							Constant.SHRED_PR.KEY_VIP_STATUS,
							result.getJSONObject("session")
									.getJSONObject("userInfo")
									.getString("vip_status"));

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			new PartyAreaAsyncTask(getApplicationContext()).execute("");
		}

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
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			Log.e("Name Value Pair", nameValuePairs.toString());
			String response = UtilInList.postData(getApplicationContext(),
					nameValuePairs, "" + Constant.API
							+ Constant.ACTIONS.PARTY_AREA
							+ "?apiMode=VIP&json=true");
			Log.e("Response In Activity-->", response);
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// fragment_addconnection_search

			UtilInList.WriteSharePrefrence(SplashScreenActivity.this,
					Constant.SHRED_PR.KEY_RESULT_PARTY_AREA, "" + result);

			new AddDeviceAsyncTask().execute();
		}

	}

	private void checkPrefsAndSplash() {

		try {
			new Timer().schedule(new TimerTask() {
				public void run() {
					proceed();
				}
			}, SPLASH_TIMEOUT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void proceed() {
		try {
			if (this.isFinishing()) {

				return;
			}

			if (UtilInList.ifConditionDataExist(SplashScreenActivity.this)) {
				if (UtilInList.ReadSharePrefrence(SplashScreenActivity.this,
						Constant.SHRED_PR.KEY_LOGIN_STATUS).equals("true")) {
					startActivity(new Intent(SplashScreenActivity.this,
							HomeScreenActivity.class));
					overridePendingTransition(R.anim.enter_from_bottom,
							R.anim.hold_bottom);
				} else {
					startActivity(new Intent(SplashScreenActivity.this,
							LeadingActivity.class));
					overridePendingTransition(R.anim.enter_from_bottom,
							R.anim.hold_bottom);
				}
			} else {

				startActivity(new Intent(SplashScreenActivity.this,
						LeadingActivity.class));
				overridePendingTransition(R.anim.enter_from_bottom,
						R.anim.hold_bottom);

			}
			finish();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}

}
