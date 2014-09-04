package co.inlist.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
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
import co.inlist.util.Constant;
import co.inlist.util.MyProgressbar;
import co.inlist.util.UtilInList;

public class InviteActivity extends Activity implements
		ActionBar.OnNavigationListener {

	EditText editFirst, editLast, editEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_person_screen);

		init();
		actionBarAndButtonActions();

		editFirst.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				editFirst.setHintTextColor(getResources().getColor(
						R.color.black_shadow));
				editFirst.setHint("Invitee's First Name");
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

		editLast.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				editLast.setHintTextColor(getResources().getColor(
						R.color.black_shadow));
				editLast.setHint("Invitee's Last Name");
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

		editEmail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				editEmail.setHintTextColor(getResources().getColor(
						R.color.black_shadow));
				editEmail.setHint("Invitee's Email Address");
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

	}

	private void init() {
		// TODO Auto-generated method stub
		editFirst = (EditText) findViewById(R.id.edt_invite_first_name);
		editLast = (EditText) findViewById(R.id.edt_invite_last_name);
		editEmail = (EditText) findViewById(R.id.edt_invite_e_mail);

		editFirst.setHintTextColor(getResources()
				.getColor(R.color.black_shadow));
		editLast.setHintTextColor(getResources().getColor(R.color.black_shadow));
		editEmail.setHintTextColor(getResources()
				.getColor(R.color.black_shadow));
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

	public class InviteAsyncTask extends AsyncTask<String, String, String> {

		private MyProgressbar dialog;

		public InviteAsyncTask(Context context) {
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
							+ Constant.ACTIONS.USER_INVITE
							+ "?apiMode=VIP&json=true"
							+ "&first_name="
							+ editFirst.getText().toString().trim()
							+ "&last_name="
							+ editLast.getText().toString().trim()
							+ "&email="
							+ editEmail.getText().toString().trim()
							+ "&PHPSESSIONID="
							+ UtilInList.ReadSharePrefrence(
									InviteActivity.this,
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

			if (result != null) {
				try {
					JSONObject jObject = new JSONObject(result);

					try {
						if (jObject.getString("success").equals("true")) {

							AlertDialog.Builder alert = new AlertDialog.Builder(
									InviteActivity.this);
							alert.setTitle(Constant.AppName);
							alert.setMessage("Your invitation was successfully sent");
							alert.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Handler hn = new Handler();
											hn.postDelayed(new Runnable() {

												@Override
												public void run() {
													// TODO Auto-generated
													// method stub
													finish();
													overridePendingTransition(
															R.anim.hold_top,
															R.anim.exit_in_left);
												}
											}, 200);
										}
									});

							alert.create();
							alert.show();

						} else {
							UtilInList
									.validateDialog(InviteActivity.this,
											jObject.getJSONArray("errors")
													.getString(0),
											Constant.ERRORS.OOPS);
						}
					} catch (Exception e) {
						Log.v("", "Exception : " + e);
					}

				} catch (JSONException e) { // TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	private boolean isValid() {
		// TODO Auto-generated method stub
		if (editFirst.getText().toString().trim().length() < 2) {
			editFirst.setText("");
			editFirst.setHintTextColor(getResources().getColor(
					R.color.light_red));
			editFirst.setHint("Valid First Name");
			return false;
		}
		if (editLast.getText().toString().trim().length() < 2) {
			editLast.setText("");
			editLast.setHintTextColor(getResources()
					.getColor(R.color.light_red));
			editLast.setHint("Valid Last Name");
			return false;
		}
		if (editEmail.getText().toString().trim().length() == 0) {
			editEmail.setText("");
			editEmail.setHintTextColor(getResources().getColor(
					R.color.light_red));
			editEmail.setHint("Valid Email Address");
			return false;
		}
		if ((android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail.getText()
				.toString().trim()).matches()) == false) {
			editEmail.setText("");
			editEmail.setHintTextColor(getResources().getColor(
					R.color.light_red));
			editEmail.setHint("Valid Email Address");
			return false;
		}
		return true;
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

		action_button.setBackgroundResource(R.drawable.send_onclick);

		// ********** VIP Text: ******************************//
		Typeface typeAkzidgrobemedex = Typeface.createFromAsset(getAssets(),
				"helve_unbold.ttf");
		TextView txtVIP = (TextView) actionBar.getCustomView().findViewById(
				R.id.txtVIP);
		txtVIP.setTypeface(typeAkzidgrobemedex);

		if (UtilInList
				.ReadSharePrefrence(InviteActivity.this,
						Constant.SHRED_PR.KEY_VIP_STATUS).toString()
				.equals("vip")) {

			txtVIP.setVisibility(View.VISIBLE);

		} else {
			txtVIP.setVisibility(View.GONE);
		}
		// *****************************************************//

		action_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isValid()) {
					AlertDialog.Builder alert = new AlertDialog.Builder(
							InviteActivity.this);
					alert.setTitle(Constant.AppName);
					alert.setMessage("Are you sure you want to send invitation?");
					alert.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(
											editFirst.getWindowToken(), 0);
									imm.hideSoftInputFromWindow(
											editLast.getWindowToken(), 0);
									imm.hideSoftInputFromWindow(
											editEmail.getWindowToken(), 0);

									if (UtilInList
											.isInternetConnectionExist(getApplicationContext())) {
										new InviteAsyncTask(InviteActivity.this)
												.execute("");
									} else {
										UtilInList
												.validateDialog(
														InviteActivity.this,
														""
																+ ""
																+ Constant.network_error,
														Constant.ERRORS.OOPS);

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

			}
		});

		relativeActionBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isValid()) {
					AlertDialog.Builder alert = new AlertDialog.Builder(
							InviteActivity.this);
					alert.setTitle(Constant.AppName);
					alert.setMessage("Are you sure you want to send invitation?");
					alert.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(
											editFirst.getWindowToken(), 0);
									imm.hideSoftInputFromWindow(
											editLast.getWindowToken(), 0);
									imm.hideSoftInputFromWindow(
											editEmail.getWindowToken(), 0);

									if (UtilInList
											.isInternetConnectionExist(getApplicationContext())) {
										new InviteAsyncTask(InviteActivity.this)
												.execute("");
									} else {
										UtilInList
												.validateDialog(
														InviteActivity.this,
														""
																+ ""
																+ Constant.network_error,
														Constant.ERRORS.OOPS);

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
			}
		});

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
	}

}
