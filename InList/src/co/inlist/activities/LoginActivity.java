package co.inlist.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import co.inlist.facebook.android.AsyncFacebookRunner;
import co.inlist.facebook.android.AsyncFacebookRunner.RequestListener;
import co.inlist.facebook.android.DialogError;
import co.inlist.facebook.android.Facebook;
import co.inlist.facebook.android.Facebook.DialogListener;
import co.inlist.facebook.android.FacebookError;
import co.inlist.interfaces.AsyncTaskCompleteListener;
import co.inlist.serverutils.WebServiceDataPosterAsyncTask;
import co.inlist.util.Constant;
import co.inlist.util.MyProgressbar;
import co.inlist.util.UtilInList;

import com.parse.ParseInstallation;

@SuppressLint("SimpleDateFormat")
public class LoginActivity extends Activity implements
		ActionBar.OnNavigationListener, AsyncTaskCompleteListener {

	private TextView txt_lgn_forgot_pwd;
	private EditText edt_lgn_e_mail;
	private EditText edt_lgn_pwd;
	private Facebook facebook = new Facebook(Constant.FB_API_KEY);
	private AsyncFacebookRunner mAsyncRunner;
	// private String FILENAME = "AndroidSSO_data";
	private SharedPreferences mPrefs;

	// private String fbId = Constant.BLANK;
	// private String fb_user_fname = Constant.BLANK;
	// private String fb_user_lname = Constant.BLANK;
	// private String fb_user_name = Constant.BLANK;
	// private String fb_user_email = Constant.BLANK;
	// private String fb_user_pic_url = Constant.BLANK;
	private RelativeLayout rl_fb_login;
	boolean flagCard = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
		init();

		actionBarAndButtonActions();

		txt_lgn_forgot_pwd.setText(Html.fromHtml("<p><u>"
				+ getString(R.string.forgot_pwd) + "</u></p>"));

		// UtilInList.makeActionBarGradiant(LoginActivity.this);

		txt_lgn_forgot_pwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LoginActivity.this,
						ForgotPassworActivity.class));
				overridePendingTransition(R.anim.enter_from_left,
						R.anim.hold_bottom);
			}
		});

		rl_fb_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (UtilInList
						.isInternetConnectionExist(getApplicationContext())) {
					loginToFacebook();
				} else {
					UtilInList.validateDialog(LoginActivity.this, ""
							+ Constant.network_error, Constant.AppName);
				}
			}
		});

		edt_lgn_e_mail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				edt_lgn_e_mail.setHintTextColor(getResources().getColor(
						R.color.white_dull));
				edt_lgn_e_mail.setHint("Email Address");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		edt_lgn_pwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				edt_lgn_pwd.setHintTextColor(getResources().getColor(
						R.color.white_dull));
				edt_lgn_pwd.setHint("Password");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		// Add Device:
		new AddDeviceAsyncTask().execute();
	}

	public void loginToFacebook() {

		mPrefs = getPreferences(MODE_PRIVATE);

		long expires = mPrefs.getLong("access_expires", 0);

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(LoginActivity.this, new String[] { "email",
					"publish_stream" }, new DialogListener() {

				@Override
				public void onCancel() {
					try {
						facebook.logout(LoginActivity.this);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onComplete(Bundle values) {
					// Function to handle complete event
					// Edit Preferences and update facebook acess_token
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString("access_token", facebook.getAccessToken());
					editor.putLong("access_expires",
							facebook.getAccessExpires());
					editor.commit();
					getProfileInformation();
				}

				@Override
				public void onError(DialogError error) {
				}

				@Override
				public void onFacebookError(FacebookError fberror) {
				}

			});

		} else {
			getProfileInformation();
		}
	}

	public void getProfileInformation() {

		mAsyncRunner.request("me", new RequestListener() {

			@Override
			public void onComplete(String response, Object state) {
				// TODO Auto-generated method stub
				try {
					JSONObject jObj = new JSONObject(response);

					Log.v("", "chk facebook response : " + jObj.toString());

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {

								flagCard = false;
								List<NameValuePair> params = new ArrayList<NameValuePair>();

								params.add(new BasicNameValuePair(
										"device_id",
										UtilInList
												.getDeviceId(LoginActivity.this)));
								params.add(new BasicNameValuePair(
										"access_token", ""
												+ facebook.getAccessToken()));
								params.add(new BasicNameValuePair(
										"device_type", "android"));
								params.add(new BasicNameValuePair(
										"PHPSESSIONID",
										""
												+ UtilInList
														.ReadSharePrefrence(
																LoginActivity.this,
																Constant.SHRED_PR.KEY_SESSIONID)));

								flagCard = false;
								new WebServiceDataPosterAsyncTask(
										LoginActivity.this, params,
										Constant.API
												+ Constant.ACTIONS.LOGIN_FB)
										.execute();

							} catch (Exception e) {

							}
						}
					});

				} catch (Exception e) {

					Log.v("", "Exception : " + e);
				}

			}

			@Override
			public void onIOException(IOException e, Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
				// TODO Auto-generated method stub

			}

		});
	}

	private void init() {
		edt_lgn_e_mail = (EditText) findViewById(R.id.edt_lgn_e_mail);
		edt_lgn_pwd = (EditText) findViewById(R.id.edt_lgn_pwd);
		txt_lgn_forgot_pwd = (TextView) findViewById(R.id.txt_lgn_forgot_pwd);
		rl_fb_login = (RelativeLayout) findViewById(R.id.rl_fb_login);
		mAsyncRunner = new AsyncFacebookRunner(facebook);

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

	@Override
	public void onTaskComplete(String result1) {
		// TODO Auto-generated method stub

		Log.e("result", ">>>" + result1.toString().length());

		if (result1.toString().length() > 0) {
			JSONObject result = null;
			try {
				result = new JSONObject(result1);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (flagCard) {
				try {
					if (result.getString("success").toString().equals("true")) {

						JSONArray data = result.getJSONArray("data");
						for (int i = 0; i < data.length(); i++) {
							JSONObject obj = data.getJSONObject(i);

							if (obj.getString("is_default").equals("1")) {

								UtilInList.WriteSharePrefrence(
										LoginActivity.this,
										Constant.SHRED_PR.KEY_USER_CARD_ADDED,
										"1");

								UtilInList.WriteSharePrefrence(
										LoginActivity.this,
										Constant.SHRED_PR.KEY_USER_CARD_ID,
										obj.getString("user_card_id"));
								UtilInList.WriteSharePrefrence(
										LoginActivity.this,
										Constant.SHRED_PR.KEY_USER_CARD_NUMBER,
										"" + obj.getString("card_number"));
								UtilInList.WriteSharePrefrence(
										LoginActivity.this,
										Constant.SHRED_PR.KEY_USER_CARD_CVV,
										"111");

								UtilInList
										.WriteSharePrefrence(
												LoginActivity.this,
												Constant.SHRED_PR.KEY_USER_CARD_HOLDER_NAME,
												"" + obj.getString("card_name"));

								String strDate = "" + obj.get("card_exp_date");
								SimpleDateFormat sdf = new SimpleDateFormat(
										"yyyy-MM-dd");
								Date date1;
								String strYear = null, strMonth = null;
								try {
									date1 = sdf.parse(strDate);
									SimpleDateFormat format = new SimpleDateFormat(
											"yyyy");
									strYear = format.format(date1);
									SimpleDateFormat format1 = new SimpleDateFormat(
											"MM");
									strMonth = format1.format(date1);

								} catch (java.text.ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								UtilInList
										.WriteSharePrefrence(
												LoginActivity.this,
												Constant.SHRED_PR.KEY_USER_CARD_EXP_MONTH,
												"" + strMonth);

								UtilInList
										.WriteSharePrefrence(
												LoginActivity.this,
												Constant.SHRED_PR.KEY_USER_CARD_EXP_YEAR,
												"" + strYear);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Push Notification Test:
				if (UtilInList
						.isInternetConnectionExist(getApplicationContext())) {
					new PushNotificationTest(LoginActivity.this).execute();
				}

			} else {
				try {
					if (result.getString("success").toString().equals("true")) {

						Log.v("",
								">>> chk this : "
										+ result.getJSONObject("data")
												.getString("last_name"));

						UtilInList.WriteSharePrefrence(LoginActivity.this,
								Constant.SHRED_PR.KEY_LOGIN_STATUS, "true");
						UtilInList.WriteSharePrefrence(
								LoginActivity.this,
								Constant.SHRED_PR.KEY_USERID,
								result.getJSONObject("data").getString(
										"user_id"));
						UtilInList
								.WriteSharePrefrence(LoginActivity.this,
										Constant.SHRED_PR.KEY_EMAIL, result
												.getJSONObject("data")
												.getString("email"));
						UtilInList.WriteSharePrefrence(
								LoginActivity.this,
								Constant.SHRED_PR.KEY_FIRSTNAME,
								result.getJSONObject("data").getString(
										"first_name"));
						UtilInList.WriteSharePrefrence(
								LoginActivity.this,
								Constant.SHRED_PR.KEY_LASTNAME,
								result.getJSONObject("data").getString(
										"last_name"));
						UtilInList
								.WriteSharePrefrence(LoginActivity.this,
										Constant.SHRED_PR.KEY_PHONE, result
												.getJSONObject("data")
												.getString("phone"));
						UtilInList.WriteSharePrefrence(
								LoginActivity.this,
								Constant.SHRED_PR.KEY_SESSIONID,
								result.getJSONObject("session")
										.getJSONObject("userInfo")
										.getString("sessionId"));
						UtilInList.WriteSharePrefrence(
								LoginActivity.this,
								Constant.SHRED_PR.KEY_VIP_STATUS,
								result.getJSONObject("session")
										.getJSONObject("userInfo")
										.getString("vip_status"));
						UtilInList.WriteSharePrefrence(LoginActivity.this,
								Constant.SHRED_PR.KEY_CURRENT_PASSWORD,
								edt_lgn_pwd.getText().toString().trim());

						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("PHPSESSIONID", ""
								+ UtilInList.ReadSharePrefrence(
										LoginActivity.this,
										Constant.SHRED_PR.KEY_SESSIONID)));

						flagCard = true;
						new WebServiceDataPosterAsyncTask(LoginActivity.this,
								params, Constant.API
										+ Constant.ACTIONS.CARD_GET).execute();

					} else {
						UtilInList.validateDialog(LoginActivity.this, result
								.getJSONArray("errors").getString(0),
								Constant.ERRORS.OOPS);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			UtilInList
					.validateDialog(
							LoginActivity.this,
							"Login failed. Please restart your application and try again",
							Constant.ERRORS.OOPS);
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

			params.add(new BasicNameValuePair("screen", "APN_USER_LOGIN"));
			params.add(new BasicNameValuePair("device_id", ""
					+ UtilInList.getDeviceId(getApplicationContext())));
			params.add(new BasicNameValuePair("device_type", "android"));
			params.add(new BasicNameValuePair("PHPSESSIONID", ""
					+ UtilInList.ReadSharePrefrence(LoginActivity.this,
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

					if (UtilInList.ReadSharePrefrence(LoginActivity.this,
							Constant.SHRED_PR.KEY_LOGIN_FROM).equals("1")) {
						finish();
						overridePendingTransition(R.anim.enter_from_bottom,
								R.anim.hold_bottom);
					} else {
						startActivity(new Intent(LoginActivity.this,
								HomeScreenActivity.class));
						LeadingActivity.laObj.finish();
						finish();
						overridePendingTransition(R.anim.enter_from_bottom,
								R.anim.hold_bottom);
					}

				} else {
					UtilInList.validateDialog(LoginActivity.this, result
							.getJSONArray("errors").getString(0),
							Constant.ERRORS.OOPS);
				}
			} catch (Exception e) {
				Log.v("", "Exception : " + e);
			}
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

		relativeActionBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edt_lgn_e_mail.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(edt_lgn_pwd.getWindowToken(), 0);

				if (UtilInList
						.isInternetConnectionExist(getApplicationContext())) {
					if (edt_lgn_e_mail.getText().toString().trim().equals("")) {
						edt_lgn_e_mail.setText("");
						edt_lgn_e_mail.setHintTextColor(getResources()
								.getColor(R.color.light_red));
						edt_lgn_e_mail.setHint("Email Invalid");
					} else if (android.util.Patterns.EMAIL_ADDRESS.matcher(
							edt_lgn_e_mail.getText().toString().trim())
							.matches() == false) {
						edt_lgn_e_mail.setText("");
						edt_lgn_e_mail.setHintTextColor(getResources()
								.getColor(R.color.light_red));
						edt_lgn_e_mail.setHint("Email Invalid");
					} else if (edt_lgn_pwd.getText().toString().trim()
							.equals("")) {
						edt_lgn_pwd.setText("");
						edt_lgn_pwd.setHintTextColor(getResources().getColor(
								R.color.light_red));
						edt_lgn_pwd.setHint("Password Incorrect");
					} else {

						List<NameValuePair> params = new ArrayList<NameValuePair>();

						params.add(new BasicNameValuePair("device_id",
								UtilInList.getDeviceId(LoginActivity.this)));
						params.add(new BasicNameValuePair("login", ""
								+ edt_lgn_e_mail.getText().toString().trim()));
						params.add(new BasicNameValuePair("password",
								edt_lgn_pwd.getText().toString().trim()));
						params.add(new BasicNameValuePair("device_type",
								"android"));
						params.add(new BasicNameValuePair("PHPSESSIONID", ""
								+ UtilInList.ReadSharePrefrence(
										LoginActivity.this,
										Constant.SHRED_PR.KEY_SESSIONID)));

						flagCard = false;
						new WebServiceDataPosterAsyncTask(LoginActivity.this,
								params, Constant.API + Constant.ACTIONS.LOGIN)
								.execute();

					}
				} else {
					UtilInList.validateDialog(LoginActivity.this, ""
							+ Constant.network_error, Constant.AppName);
				}
			}
		});

		action_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edt_lgn_e_mail.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(edt_lgn_pwd.getWindowToken(), 0);

				if (UtilInList
						.isInternetConnectionExist(getApplicationContext())) {
					if (edt_lgn_e_mail.getText().toString().trim().equals("")) {
						edt_lgn_e_mail.setText("");
						edt_lgn_e_mail.setHintTextColor(getResources()
								.getColor(R.color.light_red));
						edt_lgn_e_mail.setHint("Email Invalid");
					} else if (android.util.Patterns.EMAIL_ADDRESS.matcher(
							edt_lgn_e_mail.getText().toString().trim())
							.matches() == false) {
						edt_lgn_e_mail.setText("");
						edt_lgn_e_mail.setHintTextColor(getResources()
								.getColor(R.color.light_red));
						edt_lgn_e_mail.setHint("Email Invalid");
					} else if (edt_lgn_pwd.getText().toString().trim()
							.equals("")) {
						edt_lgn_pwd.setText("");
						edt_lgn_pwd.setHintTextColor(getResources().getColor(
								R.color.light_red));
						edt_lgn_pwd.setHint("Password Incorrect");
					} else {

						List<NameValuePair> params = new ArrayList<NameValuePair>();

						params.add(new BasicNameValuePair("device_id",
								UtilInList.getDeviceId(LoginActivity.this)));
						params.add(new BasicNameValuePair("login", ""
								+ edt_lgn_e_mail.getText().toString().trim()));
						params.add(new BasicNameValuePair("password",
								edt_lgn_pwd.getText().toString().trim()));
						params.add(new BasicNameValuePair("device_type",
								"android"));
						params.add(new BasicNameValuePair("PHPSESSIONID", ""
								+ UtilInList.ReadSharePrefrence(
										LoginActivity.this,
										Constant.SHRED_PR.KEY_SESSIONID)));

						flagCard = false;
						new WebServiceDataPosterAsyncTask(LoginActivity.this,
								params, Constant.API + Constant.ACTIONS.LOGIN)
								.execute();

					}
				} else {
					UtilInList.validateDialog(LoginActivity.this, ""
							+ Constant.network_error, Constant.AppName);
				}

			}
		});

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
					+ UtilInList.ReadSharePrefrence(LoginActivity.this,
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
							LoginActivity.this,
							Constant.SHRED_PR.KEY_SESSIONID,
							result.getJSONObject("session")
									.getJSONObject("userInfo")
									.getString("sessionId"));

					UtilInList.WriteSharePrefrence(
							LoginActivity.this,
							Constant.SHRED_PR.KEY_VIP_STATUS,
							result.getJSONObject("session")
									.getJSONObject("userInfo")
									.getString("vip_status"));

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
	}

}
