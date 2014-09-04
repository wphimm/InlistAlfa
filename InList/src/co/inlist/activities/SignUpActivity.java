package co.inlist.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import co.inlist.facebook.android.AsyncFacebookRunner;
import co.inlist.facebook.android.AsyncFacebookRunner.RequestListener;
import co.inlist.facebook.android.DialogError;
import co.inlist.facebook.android.Facebook;
import co.inlist.facebook.android.Facebook.DialogListener;
import co.inlist.facebook.android.FacebookError;
import co.inlist.interfaces.AsyncTaskCompleteListener;
import co.inlist.serverutils.WebServiceDataPosterAsyncTask;
import co.inlist.util.Constant;
import co.inlist.util.UtilInList;

import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

public class SignUpActivity extends Activity implements
		ActionBar.OnNavigationListener, AsyncTaskCompleteListener {

	private EditText edt_su_fname;
	private EditText edt_su_lname;
	private EditText edt_su_e_mail;
	private EditText edt_su_pwd;
	private EditText edt_su_phno;
	private EditText edt_su_ans;
	private RelativeLayout rl_fb;
	private TextView txt_su_que;
	private LinearLayout linearVip;

	private Facebook facebook = new Facebook(Constant.FB_API_KEY);
	private AsyncFacebookRunner mAsyncRunner;
	private SharedPreferences mPrefs;
	private static String question_id;
	private boolean fb_regiter_flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up_screen);
		init();

		actionBarAndButtonActions();

		txt_su_que.setText(getQuestion());

		rl_fb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loginToFacebook();
			}
		});

		linearVip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(SignUpActivity.this,
						VipMemberShipActivity.class));
			}
		});

		edt_su_fname.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				edt_su_fname.setHintTextColor(getResources().getColor(
						R.color.white_dull));
				edt_su_fname.setHint("First Name");
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

		edt_su_lname.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				edt_su_lname.setHintTextColor(getResources().getColor(
						R.color.white_dull));
				edt_su_lname.setHint("Last Name");
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

		edt_su_e_mail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				edt_su_e_mail.setHintTextColor(getResources().getColor(
						R.color.white_dull));
				edt_su_e_mail.setHint("Email Address");
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

		edt_su_pwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				edt_su_pwd.setHintTextColor(getResources().getColor(
						R.color.white_dull));
				edt_su_pwd.setHint("Password");
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

		edt_su_phno.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				edt_su_phno.setHintTextColor(getResources().getColor(
						R.color.white_dull));
				edt_su_phno.setHint("Phone Number");
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

		edt_su_ans.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				edt_su_ans.setHintTextColor(getResources().getColor(
						R.color.white_dull));
				edt_su_ans.setHint("Please Enter Answer");
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

		edt_su_fname.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_NEXT
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					edt_su_lname.requestFocus();
					return true;
				}
				return false;
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
			facebook.authorize(SignUpActivity.this, new String[] { "email",
					"publish_stream" }, new DialogListener() {

				@Override
				public void onCancel() {

					try {
						facebook.logout(SignUpActivity.this);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onComplete(Bundle values) {
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
					final JSONObject jObj = new JSONObject(response);

					Log.v("",
							"chk facebook response : "
									+ jObj.getString("email"));

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								edt_su_e_mail.setText(jObj.getString("email"));

								edt_su_fname.setText(jObj
										.getString("first_name"));
								edt_su_lname.setText(jObj
										.getString("last_name"));

								fb_regiter_flag = true;
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
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

		edt_su_fname = (EditText) findViewById(R.id.edt_su_fname);
		edt_su_lname = (EditText) findViewById(R.id.edt_su_lname);
		edt_su_e_mail = (EditText) findViewById(R.id.edt_su_e_mail);
		edt_su_pwd = (EditText) findViewById(R.id.edt_su_pwd);
		edt_su_phno = (EditText) findViewById(R.id.edt_su_phno);
		edt_su_ans = (EditText) findViewById(R.id.edt_su_ans);

		txt_su_que = (TextView) findViewById(R.id.txt_su_que);

		rl_fb = (RelativeLayout) findViewById(R.id.rl_fb);
		linearVip = (LinearLayout) findViewById(R.id.linearVip);

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
		JSONObject result = null;
		try {
			result = new JSONObject(result1);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Log.v("", ">>>>> chk register response : " + result.toString());
			if (result.getString("success").toString().equals("true")) {
				UtilInList.WriteSharePrefrence(SignUpActivity.this,
						Constant.SHRED_PR.KEY_LOGIN_STATUS, "true");
				UtilInList.WriteSharePrefrence(SignUpActivity.this,
						Constant.SHRED_PR.KEY_USERID,
						result.getJSONObject("userInfoSet")
								.getString("user_id"));
				UtilInList.WriteSharePrefrence(SignUpActivity.this,
						Constant.SHRED_PR.KEY_EMAIL, edt_su_e_mail.getText()
								.toString().trim());
				UtilInList.WriteSharePrefrence(SignUpActivity.this,
						Constant.SHRED_PR.KEY_FIRSTNAME, edt_su_fname.getText()
								.toString().trim());
				UtilInList.WriteSharePrefrence(SignUpActivity.this,
						Constant.SHRED_PR.KEY_LASTNAME, edt_su_lname.getText()
								.toString().trim());

				AlertDialog.Builder alert = new AlertDialog.Builder(
						SignUpActivity.this);
				alert.setTitle(Constant.AppName);
				alert.setMessage("To activate your account, please click the link in the activation email which has been sent to you.");
				alert.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent i = new Intent(getApplicationContext(),
										HomeScreenActivity.class);
								i.setAction(Intent.ACTION_SEND);
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(i);
								finish();
								overridePendingTransition(
										R.anim.enter_from_bottom,
										R.anim.hold_bottom);
							}
						});

				alert.create();
				alert.show();

			} else {
				UtilInList.validateDialog(SignUpActivity.this, result
						.getJSONArray("errors").getString(0).toString(),
						Constant.ERRORS.OOPS);
			}

		} catch (Exception e) {
			Log.v("", "Excption : " + e);
		}
	}

	public String getQuestion() {
		String prepare_reg_data = UtilInList.readFromFile(
				Constant.PREF_VAL.OFFLINE_FILE_PRE_REGISTER,
				SignUpActivity.this);
		String question = "";

		try {
			JSONObject jObj = new JSONObject(prepare_reg_data)
					.getJSONObject("membership_question");

			question = jObj.getString("membership_question_text").toString();

			question_id = jObj.getString("membership_question_id").toString();

			Log.v("", ">>> Que_id : " + question_id);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return question;
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

		action_button.setBackgroundResource(R.drawable.sign_up_action_bar);

		action_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edt_su_fname.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(edt_su_lname.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(edt_su_e_mail.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(edt_su_pwd.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(edt_su_phno.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(edt_su_ans.getWindowToken(), 0);

				if (edt_su_fname.getText().toString().trim().equals("")) {
					edt_su_fname.setText("");
					edt_su_fname.setHintTextColor(getResources().getColor(
							R.color.light_red));
					edt_su_fname.setHint("First Name");
				} else if (edt_su_lname.getText().toString().trim().equals("")) {
					edt_su_lname.setText("");
					edt_su_lname.setHintTextColor(getResources().getColor(
							R.color.light_red));
					edt_su_lname.setHint("Last Name");
				} else if (edt_su_e_mail.getText().toString().trim().equals("")) {
					edt_su_e_mail.setText("");
					edt_su_e_mail.setHintTextColor(getResources().getColor(
							R.color.light_red));
					edt_su_e_mail.setHint("Email Invalid");
				} else if (android.util.Patterns.EMAIL_ADDRESS.matcher(
						edt_su_e_mail.getText().toString().trim()).matches() == false) {
					edt_su_e_mail.setText("");
					edt_su_e_mail.setHintTextColor(getResources().getColor(
							R.color.light_red));
					edt_su_e_mail.setHint("Email Invalid");
				} else if (edt_su_pwd.getText().toString().trim().equals("")) {
					edt_su_pwd.setText("");
					edt_su_pwd.setHintTextColor(getResources().getColor(
							R.color.light_red));
					edt_su_pwd.setHint("Password Incorrect");
				} else if (edt_su_phno.getText().toString().trim().equals("")) {
					edt_su_phno.setText("");
					edt_su_phno.setHintTextColor(getResources().getColor(
							R.color.light_red));
					edt_su_phno.setHint("Phone Number Incorrect");
				} else if (edt_su_ans.getText().toString().trim().equals("")) {
					edt_su_ans.setText("");
					edt_su_ans.setHintTextColor(getResources().getColor(
							R.color.light_red));
					edt_su_ans.setHint("Please Enter Answer");

				} else {

					if (fb_regiter_flag) {

						List<NameValuePair> params = new ArrayList<NameValuePair>();

						params.add(new BasicNameValuePair("device_id",
								UtilInList.getDeviceId(SignUpActivity.this)));
						params.add(new BasicNameValuePair("email",
								edt_su_e_mail.getText().toString().trim()));
						params.add(new BasicNameValuePair("password",
								edt_su_pwd.getText().toString().trim()));
						params.add(new BasicNameValuePair("first_name",
								edt_su_fname.getText().toString().trim()));
						params.add(new BasicNameValuePair("last_name",
								edt_su_lname.getText().toString().trim()));
						params.add(new BasicNameValuePair("phone", edt_su_phno
								.getText().toString().trim()));
						params.add(new BasicNameValuePair(
								"membership_question_id", question_id));
						params.add(new BasicNameValuePair(
								"membership_question_answer", edt_su_ans
										.getText().toString().trim()
										.replace(" ", "%20")));
						params.add(new BasicNameValuePair("access_token", ""
								+ facebook.getAccessToken().toString().trim()));
						params.add(new BasicNameValuePair("device_type",
								"android"));
						params.add(new BasicNameValuePair("PHPSESSIONID", ""
								+ UtilInList.ReadSharePrefrence(
										SignUpActivity.this,
										Constant.SHRED_PR.KEY_SESSIONID)));

						new WebServiceDataPosterAsyncTask(SignUpActivity.this,
								params, Constant.API
										+ Constant.ACTIONS.REGISTER_FB)
								.execute();

					} else {

						List<NameValuePair> params = new ArrayList<NameValuePair>();

						params.add(new BasicNameValuePair("device_id",
								UtilInList.getDeviceId(SignUpActivity.this)));
						params.add(new BasicNameValuePair("email",
								edt_su_e_mail.getText().toString().trim()));
						params.add(new BasicNameValuePair("password",
								edt_su_pwd.getText().toString().trim()));
						params.add(new BasicNameValuePair("first_name",
								edt_su_fname.getText().toString().trim()));
						params.add(new BasicNameValuePair("last_name",
								edt_su_lname.getText().toString().trim()));
						params.add(new BasicNameValuePair("phone", edt_su_phno
								.getText().toString().trim()));
						params.add(new BasicNameValuePair(
								"membership_question_id", question_id));
						params.add(new BasicNameValuePair(
								"membership_question_answer", edt_su_ans
										.getText().toString().trim()
										.replace(" ", "%20")));
						params.add(new BasicNameValuePair("device_type",
								"android"));
						params.add(new BasicNameValuePair("PHPSESSIONID", ""
								+ UtilInList.ReadSharePrefrence(
										SignUpActivity.this,
										Constant.SHRED_PR.KEY_SESSIONID)));

						new WebServiceDataPosterAsyncTask(SignUpActivity.this,
								params, Constant.API
										+ Constant.ACTIONS.REGISTRATION)
								.execute();

					}

				}

			}
		});

		relativeActionBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edt_su_fname.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(edt_su_lname.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(edt_su_e_mail.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(edt_su_pwd.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(edt_su_phno.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(edt_su_ans.getWindowToken(), 0);

				if (UtilInList
						.isInternetConnectionExist(getApplicationContext())) {
					if (edt_su_fname.getText().toString().trim().equals("")) {
						edt_su_fname.setText("");
						edt_su_fname.setHintTextColor(getResources().getColor(
								R.color.light_red));
						edt_su_fname.setHint("First Name");
					} else if (edt_su_lname.getText().toString().trim()
							.equals("")) {
						edt_su_lname.setText("");
						edt_su_lname.setHintTextColor(getResources().getColor(
								R.color.light_red));
						edt_su_lname.setHint("Last Name");
					} else if (edt_su_e_mail.getText().toString().trim()
							.equals("")) {
						edt_su_e_mail.setText("");
						edt_su_e_mail.setHintTextColor(getResources().getColor(
								R.color.light_red));
						edt_su_e_mail.setHint("Email Invalid");
					} else if (android.util.Patterns.EMAIL_ADDRESS.matcher(
							edt_su_e_mail.getText().toString().trim())
							.matches() == false) {
						edt_su_e_mail.setText("");
						edt_su_e_mail.setHintTextColor(getResources().getColor(
								R.color.light_red));
						edt_su_e_mail.setHint("Email Invalid");
					} else if (edt_su_pwd.getText().toString().trim()
							.equals("")) {
						edt_su_pwd.setText("");
						edt_su_pwd.setHintTextColor(getResources().getColor(
								R.color.light_red));
						edt_su_pwd.setHint("Password Incorrect");
					} else if (edt_su_phno.getText().toString().trim()
							.equals("")) {
						edt_su_phno.setText("");
						edt_su_phno.setHintTextColor(getResources().getColor(
								R.color.light_red));
						edt_su_phno.setHint("Phone Number Incorrect");
					} else if (edt_su_ans.getText().toString().trim()
							.equals("")) {
						edt_su_ans.setText("");
						edt_su_ans.setHintTextColor(getResources().getColor(
								R.color.light_red));
						edt_su_ans.setHint("Please Enter Answer");

					} else {

						if (fb_regiter_flag) {

							List<NameValuePair> params = new ArrayList<NameValuePair>();

							params.add(new BasicNameValuePair("device_id",
									UtilInList.getDeviceId(SignUpActivity.this)));
							params.add(new BasicNameValuePair("email",
									edt_su_e_mail.getText().toString().trim()));
							params.add(new BasicNameValuePair("password",
									edt_su_pwd.getText().toString().trim()));
							params.add(new BasicNameValuePair("first_name",
									edt_su_fname.getText().toString().trim()));
							params.add(new BasicNameValuePair("last_name",
									edt_su_lname.getText().toString().trim()));
							params.add(new BasicNameValuePair("phone",
									edt_su_phno.getText().toString().trim()));
							params.add(new BasicNameValuePair(
									"membership_question_id", question_id));
							params.add(new BasicNameValuePair(
									"membership_question_answer", edt_su_ans
											.getText().toString().trim()
											.replace(" ", "%20")));
							params.add(new BasicNameValuePair("access_token",
									""
											+ facebook.getAccessToken()
													.toString().trim()));
							params.add(new BasicNameValuePair("device_type",
									"android"));
							params.add(new BasicNameValuePair(
									"PHPSESSIONID",
									""
											+ UtilInList
													.ReadSharePrefrence(
															SignUpActivity.this,
															Constant.SHRED_PR.KEY_SESSIONID)));

							new WebServiceDataPosterAsyncTask(
									SignUpActivity.this, params, Constant.API
											+ Constant.ACTIONS.REGISTER_FB)
									.execute();

						} else {

							List<NameValuePair> params = new ArrayList<NameValuePair>();

							params.add(new BasicNameValuePair("device_id",
									UtilInList.getDeviceId(SignUpActivity.this)));
							params.add(new BasicNameValuePair("email",
									edt_su_e_mail.getText().toString().trim()));
							params.add(new BasicNameValuePair("password",
									edt_su_pwd.getText().toString().trim()));
							params.add(new BasicNameValuePair("first_name",
									edt_su_fname.getText().toString().trim()));
							params.add(new BasicNameValuePair("last_name",
									edt_su_lname.getText().toString().trim()));
							params.add(new BasicNameValuePair("phone",
									edt_su_phno.getText().toString().trim()));
							params.add(new BasicNameValuePair(
									"membership_question_id", question_id));
							params.add(new BasicNameValuePair(
									"membership_question_answer", edt_su_ans
											.getText().toString().trim()
											.replace(" ", "%20")));
							params.add(new BasicNameValuePair("device_type",
									"android"));
							params.add(new BasicNameValuePair(
									"PHPSESSIONID",
									""
											+ UtilInList
													.ReadSharePrefrence(
															SignUpActivity.this,
															Constant.SHRED_PR.KEY_SESSIONID)));

							new WebServiceDataPosterAsyncTask(
									SignUpActivity.this, params, Constant.API
											+ Constant.ACTIONS.REGISTRATION)
									.execute();

						}
					}

				} else {
					UtilInList.validateDialog(SignUpActivity.this, ""
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
					+ UtilInList.ReadSharePrefrence(SignUpActivity.this,
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
							SignUpActivity.this,
							Constant.SHRED_PR.KEY_SESSIONID,
							result.getJSONObject("session")
									.getJSONObject("userInfo")
									.getString("sessionId"));

					UtilInList.WriteSharePrefrence(
							SignUpActivity.this,
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
