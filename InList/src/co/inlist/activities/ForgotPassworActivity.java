package co.inlist.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import co.inlist.interfaces.AsyncTaskCompleteListener;
import co.inlist.serverutils.WebServiceDataPosterAsyncTask;
import co.inlist.util.Constant;
import co.inlist.util.UtilInList;

/*
 * Forgot password class
 */
public class ForgotPassworActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener, AsyncTaskCompleteListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forget_passwod_frame_to_replace);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment_Enter_Email())
					.commit();
		}
	}

	/*
	 * Fragment where enter email id option you will find
	 */
	public static class PlaceholderFragment_Enter_Email extends Fragment {

		private EditText edt_frg_e_mail;

		private void actionBarAndButtonActions() {
			ActionBar actionBar = getActivity().getActionBar();
			// add the custom view to the action bar
			actionBar.setCustomView(R.layout.custome_action_bar);

			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
					| ActionBar.DISPLAY_SHOW_HOME);

			actionBar.setDisplayHomeAsUpEnabled(true);

			RelativeLayout relativeActionBar = (RelativeLayout) actionBar
					.getCustomView().findViewById(R.id.relativeActionBar);
			ImageButton action_button = (ImageButton) actionBar.getCustomView()
					.findViewById(R.id.btn_action_bar);

			action_button.setBackgroundResource(R.drawable.done_btn_action_bar);

			action_button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (edt_frg_e_mail.getText().toString().trim().equals("")) {
						edt_frg_e_mail.setText("");
						edt_frg_e_mail.setHintTextColor(getResources().getColor(
								R.color.light_red));
						edt_frg_e_mail.setHint("Email Invalid");
					} else if (android.util.Patterns.EMAIL_ADDRESS.matcher(
							edt_frg_e_mail.getText().toString().trim()).matches() == false) {
						edt_frg_e_mail.setText("");
						edt_frg_e_mail.setHintTextColor(getResources().getColor(
								R.color.light_red));
						edt_frg_e_mail.setHint("Email Invalid");
					}  else {
						List<NameValuePair> params = new ArrayList<NameValuePair>();

						params.add(new BasicNameValuePair("email", ""
								+ edt_frg_e_mail.getText().toString().trim()));

						params.add(new BasicNameValuePair("device_type",
								"android"));

						params.add(new BasicNameValuePair("PHPSESSIONID", ""
								+ UtilInList.ReadSharePrefrence(getActivity(),
										Constant.SHRED_PR.KEY_SESSIONID)));

						new WebServiceDataPosterAsyncTask(
								getActivity(),
								params,
								Constant.API
										+ "request_password_reset/?apiMode=VIP&json=true")
								.execute();
					}

				}
			});
			
			relativeActionBar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (edt_frg_e_mail.getText().toString().trim().equals("")) {
						edt_frg_e_mail.setText("");
						edt_frg_e_mail.setHintTextColor(getResources().getColor(
								R.color.light_red));
						edt_frg_e_mail.setHint("Email Invalid");
					} else if (android.util.Patterns.EMAIL_ADDRESS.matcher(
							edt_frg_e_mail.getText().toString().trim()).matches() == false) {
						edt_frg_e_mail.setText("");
						edt_frg_e_mail.setHintTextColor(getResources().getColor(
								R.color.light_red));
						edt_frg_e_mail.setHint("Email Invalid");
					}  else {
						List<NameValuePair> params = new ArrayList<NameValuePair>();

						params.add(new BasicNameValuePair("email", ""
								+ edt_frg_e_mail.getText().toString().trim()));

						params.add(new BasicNameValuePair("device_type",
								"android"));

						params.add(new BasicNameValuePair("PHPSESSIONID", ""
								+ UtilInList.ReadSharePrefrence(getActivity(),
										Constant.SHRED_PR.KEY_SESSIONID)));

						new WebServiceDataPosterAsyncTask(
								getActivity(),
								params,
								Constant.API
										+ "request_password_reset/?apiMode=VIP&json=true")
								.execute();
					}

				}
			});

		}

		public PlaceholderFragment_Enter_Email() {

		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// Take appropriate action for each action item click
			switch (item.getItemId()) {

			case android.R.id.home:
				getActivity().finish();
				getActivity().overridePendingTransition(R.anim.hold_top,
						R.anim.exit_in_left);
				return true;

			default:
				return super.onOptionsItemSelected(item);
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			setHasOptionsMenu(true);

			View rootView = inflater.inflate(R.layout.forgot_password_screen,
					container, false);

			actionBarAndButtonActions();

			edt_frg_e_mail = (EditText) rootView
					.findViewById(R.id.edt_frg_e_mail);
			edt_frg_e_mail.setHintTextColor(getResources().getColor(
					R.color.light_black));

			edt_frg_e_mail.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before,
						int count) {
					// TODO Auto-generated method stub
					edt_frg_e_mail.setHintTextColor(getResources().getColor(
							R.color.light_black));
					edt_frg_e_mail.setHint("Email Address");
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

			
			return rootView;
		}
	}

	public static class PlaceholderFragment_SendLink extends Fragment {

		public PlaceholderFragment_SendLink() {

		}

		private void actionBarAndButtonActions() {
			ActionBar actionBar = getActivity().getActionBar();
			// add the custom view to the action bar
			actionBar.setCustomView(R.layout.custome_action_bar);

			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
					| ActionBar.DISPLAY_SHOW_HOME);

			actionBar.setDisplayHomeAsUpEnabled(true);

			RelativeLayout relativeActionBar = (RelativeLayout) actionBar
					.getCustomView().findViewById(R.id.relativeActionBar);
			ImageButton action_button = (ImageButton) actionBar.getCustomView()
					.findViewById(R.id.btn_action_bar);

			action_button.setBackgroundResource(R.drawable.done_btn_action_bar);

			action_button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					getActivity().finish();
					getActivity().overridePendingTransition(R.anim.hold_top,
							R.anim.exit_in_left);
				}
			});
			
			relativeActionBar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					getActivity().finish();
					getActivity().overridePendingTransition(R.anim.hold_top,
							R.anim.exit_in_left);
				}
			});

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater
					.inflate(R.layout.forgot_password_screen_send_link,
							container, false);

			actionBarAndButtonActions();

			return rootView;
		}

	}

	/**
	 * On selecting action bar icons
	 * */

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

		if (result != null) {
			try {

				if (result.getString("success").equals("true")) {
					Fragment myCurrentFragment = new PlaceholderFragment_SendLink();
					android.support.v4.app.FragmentManager manager = getSupportFragmentManager();

					myCurrentFragment = new PlaceholderFragment_SendLink();
					manager.beginTransaction()
							.replace(R.id.container, myCurrentFragment)
							.commit();
				} else {
					UtilInList.validateDialog(ForgotPassworActivity.this,
							result.getJSONArray("errors").getString(0),
							Constant.ERRORS.OOPS);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
	}
}
