package co.inlist.activities;

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
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import co.inlist.interfaces.AsyncTaskCompleteListener;
import co.inlist.serverutils.WebServiceDataPosterAsyncTask;
import co.inlist.util.Constant;
import co.inlist.util.UtilInList;

public class EditProfileActivity extends Activity implements
		ActionBar.OnNavigationListener, AsyncTaskCompleteListener {

	EditText editFirst, editLast, editEmail, editPhone;
	Button btnUpdate;
	int flagLogout = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_profile);

		init();

		actionBarAndButtonActions();

		editFirst.setText(""
				+ UtilInList.ReadSharePrefrence(getApplicationContext(),
						Constant.SHRED_PR.KEY_FIRSTNAME));
		editLast.setText(""
				+ UtilInList.ReadSharePrefrence(getApplicationContext(),
						Constant.SHRED_PR.KEY_LASTNAME));
		editEmail.setText(""
				+ UtilInList.ReadSharePrefrence(getApplicationContext(),
						Constant.SHRED_PR.KEY_EMAIL));
		editPhone.setText(""
				+ UtilInList.ReadSharePrefrence(getApplicationContext(),
						Constant.SHRED_PR.KEY_PHONE));

		btnUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editFirst.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(editLast.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(editEmail.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(editPhone.getWindowToken(), 0);

				if (isValid()) {
					if (UtilInList
							.isInternetConnectionExist(getApplicationContext())) {

						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("first_name", ""
								+ editFirst.getText().toString().trim()));
						params.add(new BasicNameValuePair("last_name", ""
								+ editLast.getText().toString().trim()));
						params.add(new BasicNameValuePair("phone", ""
								+ editPhone.getText().toString().trim()));
						params.add(new BasicNameValuePair("device_type",
								"android"));
						params.add(new BasicNameValuePair("PHPSESSIONID", ""
								+ UtilInList.ReadSharePrefrence(
										EditProfileActivity.this,
										Constant.SHRED_PR.KEY_SESSIONID)));

						flagLogout = 0;
						new WebServiceDataPosterAsyncTask(
								EditProfileActivity.this,
								params,
								Constant.API
										+ "user/small_details/save/?apiMode=VIP&json=true")
								.execute();

					} else {
						UtilInList.validateDialog(EditProfileActivity.this, ""
								+ Constant.network_error, Constant.AppName);
					}
				}
			}
		});
	}

	private void init() {
		// TODO Auto-generated method stub
		editFirst = (EditText) findViewById(R.id.editFirst);
		editLast = (EditText) findViewById(R.id.editLast);
		editEmail = (EditText) findViewById(R.id.editEmail);
		editPhone = (EditText) findViewById(R.id.editPhone);
		btnUpdate = (Button) findViewById(R.id.btnUpdate);
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

	private boolean isValid() {
		// TODO Auto-generated method stub
		if (editFirst.getText().toString().trim().length() < 2) {
			UtilInList.validateDialog(EditProfileActivity.this,
					"first name must be minimum 2 characters",
					Constant.ERRORS.OOPS);
			return false;
		}
		if (editLast.getText().toString().trim().length() < 2) {
			UtilInList.validateDialog(EditProfileActivity.this,
					"last name must be minimum 2 characters",
					Constant.ERRORS.OOPS);
			return false;
		}
		if (editEmail.getText().toString().trim().length() == 0) {
			UtilInList.validateDialog(EditProfileActivity.this,
					"please enter email", Constant.ERRORS.OOPS);
			return false;
		}
		if ((android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail.getText()
				.toString().trim()).matches()) == false) {
			UtilInList.validateDialog(EditProfileActivity.this,
					"please enter valid email", Constant.ERRORS.OOPS);
			return false;
		}
		if (editPhone.getText().toString().trim().length() < 10) {
			UtilInList
					.validateDialog(EditProfileActivity.this,
							"phone must be minimum 10 characters",
							Constant.ERRORS.OOPS);
			return false;
		}
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int arg0, long arg1) {
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
		Log.e("result", ">>" + result);

		if (flagLogout == 0) {
			try {
				if (result.getString("success").equals("true")) {

					UtilInList.validateDialog(EditProfileActivity.this, result
							.getJSONArray("messages").getString(0),
							Constant.ERRORS.OOPS);

					UtilInList.WriteSharePrefrence(EditProfileActivity.this,
							Constant.SHRED_PR.KEY_FIRSTNAME, ""
									+ editFirst.getText().toString());
					UtilInList.WriteSharePrefrence(EditProfileActivity.this,
							Constant.SHRED_PR.KEY_LASTNAME, ""
									+ editLast.getText().toString());
					UtilInList.WriteSharePrefrence(EditProfileActivity.this,
							Constant.SHRED_PR.KEY_PHONE, ""
									+ editPhone.getText().toString());

				} else {
					UtilInList.validateDialog(EditProfileActivity.this, result
							.getJSONArray("errors").getString(0),
							Constant.ERRORS.OOPS);
				}
			} catch (Exception e) {
				Log.v("", "Exception : " + e);
			}
		} else {
			try {
				if (result.getString("success").equals("true")) {

					UtilInList.WriteSharePrefrence(EditProfileActivity.this,
							Constant.SHRED_PR.KEY_LOGIN_STATUS, "false");

					UtilInList.WriteSharePrefrence(EditProfileActivity.this,
							Constant.SHRED_PR.KEY_USER_CARD_ADDED, "0");

					ProfileActivity.profObj.finish();
					finish();
					overridePendingTransition(R.anim.hold_top,
							R.anim.exit_in_left);

				} else {
					UtilInList.validateDialog(EditProfileActivity.this, result
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

		action_button.setBackgroundResource(R.drawable.logout_action_bar);

		// ********** VIP Text: ******************************//
		Typeface typeAkzidgrobemedex = Typeface.createFromAsset(getAssets(),
				"helve_unbold.ttf");
		TextView txtVIP = (TextView) actionBar.getCustomView().findViewById(
				R.id.txtVIP);
		txtVIP.setTypeface(typeAkzidgrobemedex);

		if (UtilInList
				.ReadSharePrefrence(EditProfileActivity.this,
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
				// search action
				AlertDialog.Builder alert = new AlertDialog.Builder(
						EditProfileActivity.this);
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
																	EditProfileActivity.this,
																	Constant.SHRED_PR.KEY_SESSIONID)));

									flagLogout = 1;
									new WebServiceDataPosterAsyncTask(
											EditProfileActivity.this,
											params,
											Constant.API
													+ "user/logout/?apiMode=VIP&json=true")
											.execute();

								} else {
									UtilInList.validateDialog(
											EditProfileActivity.this, ""
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
			}
		});

		relativeActionBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// search action
				AlertDialog.Builder alert = new AlertDialog.Builder(
						EditProfileActivity.this);
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
																	EditProfileActivity.this,
																	Constant.SHRED_PR.KEY_SESSIONID)));

									flagLogout = 1;
									new WebServiceDataPosterAsyncTask(
											EditProfileActivity.this,
											params,
											Constant.API
													+ "user/logout/?apiMode=VIP&json=true")
											.execute();

								} else {
									UtilInList.validateDialog(
											EditProfileActivity.this, ""
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
