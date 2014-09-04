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
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import co.inlist.interfaces.AsyncTaskCompleteListener;
import co.inlist.serverutils.WebServiceDataPosterAsyncTask;
import co.inlist.util.Constant;
import co.inlist.util.UtilInList;

public class ChangePasswordActivity extends Activity implements
		ActionBar.OnNavigationListener, AsyncTaskCompleteListener {

	TextView txt_forgot_pwd;
	EditText editCurrentPassword, editNewPassword, editConfirmPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password_screen);

		init();

		actionBarAndButtonActions();

		txt_forgot_pwd.setText(Html.fromHtml("<p><u>"
				+ getString(R.string.forgot_pwd) + "</u></p>"));

		txt_forgot_pwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(ChangePasswordActivity.this,
						ForgotPassworActivity.class));
				overridePendingTransition(R.anim.enter_from_left,
						R.anim.hold_bottom);
			}
		});
	}

	private void init() {
		// TODO Auto-generated method stub
		txt_forgot_pwd = (TextView) findViewById(R.id.txt_forgot_pwd);

		editCurrentPassword = (EditText) findViewById(R.id.edt_current_pwd);
		editNewPassword = (EditText) findViewById(R.id.edt_new_pwd);
		editConfirmPassword = (EditText) findViewById(R.id.edt_confirm_pwd);
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

				editCurrentPassword.setText("");
				editNewPassword.setText("");
				editConfirmPassword.setText("");

				UtilInList.validateDialog(ChangePasswordActivity.this, result
						.getJSONArray("messages").getString(0),
						Constant.AppName);
			} else {
				UtilInList.validateDialog(ChangePasswordActivity.this, result
						.getJSONArray("errors").getString(0),
						Constant.ERRORS.OOPS);
			}
		} catch (Exception e) {
			Log.v("", "Exception : " + e);
		}

	}

	private boolean isValidate() {
		// TODO Auto-generated method stub

		if (editCurrentPassword.getText().toString().trim().length() == 0) {
			UtilInList.validateDialog(ChangePasswordActivity.this,
					Constant.ERRORS.PLZ_CUR_PWD, Constant.ERRORS.OOPS);
			return false;
		}

		if (editNewPassword.getText().toString().trim().length() == 0) {
			UtilInList.validateDialog(ChangePasswordActivity.this,
					Constant.ERRORS.PLZ_NEW_PWD, Constant.ERRORS.OOPS);
			return false;
		}
		if (editConfirmPassword.getText().toString().trim().length() == 0) {
			UtilInList.validateDialog(ChangePasswordActivity.this,
					Constant.ERRORS.PLZ_CNFRM_PWD, Constant.ERRORS.OOPS);

			return false;
		}
		if (!editNewPassword.getText().toString().trim()
				.equals("" + editConfirmPassword.getText().toString().trim())) {
			UtilInList.validateDialog(ChangePasswordActivity.this,
					Constant.ERRORS.PWD_NOT_MATCH, Constant.ERRORS.OOPS);
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

		action_button.setBackgroundResource(R.drawable.save_onclick);

		// ********** VIP Text: ******************************//
		Typeface typeAkzidgrobemedex = Typeface.createFromAsset(getAssets(),
				"helve_unbold.ttf");
		TextView txtVIP = (TextView) actionBar.getCustomView().findViewById(
				R.id.txtVIP);
		txtVIP.setTypeface(typeAkzidgrobemedex);

		if (UtilInList
				.ReadSharePrefrence(ChangePasswordActivity.this,
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

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						editCurrentPassword.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(editNewPassword.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(
						editConfirmPassword.getWindowToken(), 0);

				if (isValidate()) {
					if (UtilInList
							.isInternetConnectionExist(getApplicationContext())) {

						List<NameValuePair> params = new ArrayList<NameValuePair>();

						params.add(new BasicNameValuePair("current_password",
								""
										+ editCurrentPassword.getText()
												.toString().trim()));
						params.add(new BasicNameValuePair("password", ""
								+ editNewPassword.getText().toString().trim()));
						params.add(new BasicNameValuePair("confirm_password",
								""
										+ editConfirmPassword.getText()
												.toString().trim()));
						params.add(new BasicNameValuePair("device_type",
								"android"));
						params.add(new BasicNameValuePair("PHPSESSIONID", ""
								+ UtilInList.ReadSharePrefrence(
										ChangePasswordActivity.this,
										Constant.SHRED_PR.KEY_SESSIONID)));

						new WebServiceDataPosterAsyncTask(
								ChangePasswordActivity.this,
								params,
								Constant.API
										+ "user/login/save/?apiMode=VIP&json=true")
								.execute();

					} else {
						UtilInList.validateDialog(ChangePasswordActivity.this,
								"" + "" + Constant.network_error,
								Constant.ERRORS.OOPS);

					}
				}
			}
		});

		relativeActionBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						editCurrentPassword.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(editNewPassword.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(
						editConfirmPassword.getWindowToken(), 0);

				if (isValidate()) {
					if (UtilInList
							.isInternetConnectionExist(getApplicationContext())) {

						List<NameValuePair> params = new ArrayList<NameValuePair>();

						params.add(new BasicNameValuePair("current_password",
								""
										+ editCurrentPassword.getText()
												.toString().trim()));
						params.add(new BasicNameValuePair("password", ""
								+ editNewPassword.getText().toString().trim()));
						params.add(new BasicNameValuePair("confirm_password",
								""
										+ editConfirmPassword.getText()
												.toString().trim()));
						params.add(new BasicNameValuePair("device_type",
								"android"));
						params.add(new BasicNameValuePair("PHPSESSIONID", ""
								+ UtilInList.ReadSharePrefrence(
										ChangePasswordActivity.this,
										Constant.SHRED_PR.KEY_SESSIONID)));

						new WebServiceDataPosterAsyncTask(
								ChangePasswordActivity.this,
								params,
								Constant.API
										+ "user/login/save/?apiMode=VIP&json=true")
								.execute();

					} else {
						UtilInList.validateDialog(ChangePasswordActivity.this,
								"" + "" + Constant.network_error,
								Constant.ERRORS.OOPS);

					}
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
