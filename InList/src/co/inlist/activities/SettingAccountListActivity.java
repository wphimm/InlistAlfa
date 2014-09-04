package co.inlist.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import co.inlist.interfaces.AsyncTaskCompleteListener;
import co.inlist.serverutils.WebServiceDataPosterAsyncTask;
import co.inlist.util.Constant;
import co.inlist.util.UtilInList;

public class SettingAccountListActivity extends Activity implements
		ActionBar.OnNavigationListener, AsyncTaskCompleteListener {
	ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settting_list_screen);

		listView = (ListView) findViewById(R.id.lst_setting);

		listView.setAdapter(new CustomAdapter());
		listView.setDividerHeight(0);
		listView.setDivider(null);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				if (position == 0) {

					startActivity(new Intent(SettingAccountListActivity.this,
							ChangePasswordActivity.class));
					overridePendingTransition(R.anim.enter_from_left,
							R.anim.hold_bottom);
				} else if (position == 1) {

					UtilInList.WriteSharePrefrence(
							SettingAccountListActivity.this,
							Constant.SHRED_PR.KEY_ADDCARD_FROM, "0");

					if (UtilInList
							.ReadSharePrefrence(
									SettingAccountListActivity.this,
									Constant.SHRED_PR.KEY_USER_CARD_ADDED)
							.toString().equals("1")) {
						startActivity(new Intent(
								SettingAccountListActivity.this,
								AddCardActivity.class));
						overridePendingTransition(R.anim.enter_from_left,
								R.anim.hold_bottom);
					} else {
						startActivity(new Intent(
								SettingAccountListActivity.this,
								NoCardActivity.class));
						overridePendingTransition(R.anim.enter_from_left,
								R.anim.hold_bottom);
					}
				} else if (position == 2) {
					startActivity(new Intent(SettingAccountListActivity.this,
							InviteActivity.class));
					overridePendingTransition(R.anim.enter_from_left,
							R.anim.hold_bottom);
				} else if (position == 3) {
					startActivity(new Intent(SettingAccountListActivity.this,
							NotificationsSettingsActivity.class));
					overridePendingTransition(R.anim.enter_from_left,
							R.anim.hold_bottom);
				} else if (position == 4) {
					UtilInList.WriteSharePrefrence(
							SettingAccountListActivity.this,
							Constant.SHRED_PR.KEY_TERMS_FROM, "0");
					startActivity(new Intent(SettingAccountListActivity.this,
							TermsConditionsActivity.class));
					overridePendingTransition(R.anim.enter_from_left,
							R.anim.hold_bottom);
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
					new PUSHNOTIFICATIONSAsyncTask(getApplicationContext())
							.execute("");
				}
			}
		}, 100);

		actionBarAndButtonActions();

	}

	class CustomAdapter extends BaseAdapter {

		String[] values = new String[] { "Change Password", "Billing Details",
				"Invite", "Notification Settings", "Terms & Conditions" };

		public CustomAdapter() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return values.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View row = convertView;
			if (row == null) {
				row = getLayoutInflater().inflate(
						R.layout.setting_activity_row, null);
			}
			TextView txt = (TextView) row
					.findViewById(R.id.txt_setting_lst_title);
			txt.setText("" + values[position]);

			View v = (View) row.findViewById(R.id.v2);
			if (position == 1) {
				v.setVisibility(View.VISIBLE);
			} else {
				v.setVisibility(View.GONE);
			}

			return row;
		}

	}

	public class PUSHNOTIFICATIONSAsyncTask extends
			AsyncTask<String, String, String> {

		public PUSHNOTIFICATIONSAsyncTask(Context context) {
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("device_id", ""
					+ UtilInList.getDeviceId(getApplicationContext())));
			params.add(new BasicNameValuePair("device_type", "android"));
			params.add(new BasicNameValuePair("PHPSESSIONID", ""
					+ UtilInList.ReadSharePrefrence(
							SettingAccountListActivity.this,
							Constant.SHRED_PR.KEY_SESSIONID)));

			String response = UtilInList.postData(getApplicationContext(),
					params, "" + Constant.API
							+ Constant.ACTIONS.PUSHNOTIFICATIONS_INFO);

			Log.e("Response In Activity-->", "+++++" + response);
			return response;
		}

		@Override
		protected void onPostExecute(String result1) {
			// TODO Auto-generated method stub
			super.onPostExecute(result1);
			// fragment_addconnection_search

			if (result1 != null) {
				try {
					JSONObject result = new JSONObject(result1);
					if (result.getString("success").equals("true")) {
						UtilInList
								.WriteSharePrefrence(
										SettingAccountListActivity.this,
										Constant.SHRED_PR.KEY_DAILY, result
												.getJSONObject("data")
												.getString("daily"));
						UtilInList.WriteSharePrefrence(
								SettingAccountListActivity.this,
								Constant.SHRED_PR.KEY_BILLING,
								result.getJSONObject("data").getString(
										"billing"));
					}
				} catch (Exception e) {
					Log.v("", "Exception : " + e);
				}
			}

			Log.i("device_id",
					"" + UtilInList.getDeviceId(getApplicationContext()));
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("device_id", ""
					+ UtilInList.getDeviceId(getApplicationContext())));
			params.add(new BasicNameValuePair("device_type", "android"));

			params.add(new BasicNameValuePair("PHPSESSIONID", ""
					+ UtilInList.ReadSharePrefrence(
							SettingAccountListActivity.this,
							Constant.SHRED_PR.KEY_SESSIONID)));

			new WebServiceDataPosterAsyncTask(SettingAccountListActivity.this,
					params, Constant.API + Constant.ACTIONS.ADD_DEVICE)
					.execute();

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
	public boolean onNavigationItemSelected(int arg0, long arg1) {
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
				.ReadSharePrefrence(SettingAccountListActivity.this,
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
		overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
	}

	@Override
	public void onTaskComplete(String result) {
		// TODO Auto-generated method stub
		Log.e("result:", ">>>>>" + result);
	}

}