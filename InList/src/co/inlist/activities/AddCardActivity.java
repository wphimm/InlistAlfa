package co.inlist.activities;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import co.inlist.interfaces.AsyncTaskCompleteListener;
import co.inlist.serverutils.WebServiceDataPosterAsyncTask;
import co.inlist.util.Constant;
import co.inlist.util.UtilInList;

public class AddCardActivity extends Activity implements
		ActionBar.OnNavigationListener, AsyncTaskCompleteListener {

	private int MY_SCAN_REQUEST_CODE = 100; // arbitrary int

	private EditText edt_card_num;
	private EditText edt_card_name;
	private EditText edt_card_num_cvv;
	private Spinner sp_month;
	private Spinner sp_year;
	private String selected_month, selected_year;
	private LinearLayout linearScan;
	RelativeLayout relativeScanYourCard;
	boolean flagCardDelete = false;

	String strTemp;
	int keyDel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_card_screen);

		init();

		actionBarAndButtonActions();

		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		List<String> list = new ArrayList<String>();

		list.add(0, "Year");
		for (int i = 0; i < 15; i++) {
			list.add(String.valueOf(year + i));
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_year.setAdapter(dataAdapter);

		if (UtilInList
				.ReadSharePrefrence(AddCardActivity.this,
						Constant.SHRED_PR.KEY_USER_CARD_ADDED).toString()
				.equals("1")) {

			String strCardNum = ""
					+ UtilInList.ReadSharePrefrence(AddCardActivity.this,
							Constant.SHRED_PR.KEY_USER_CARD_NUMBER).toString();
			edt_card_num.setText("" + strCardNum);

			try {
				String subString = strCardNum.substring(
						strCardNum.length() - 4, strCardNum.length());
				edt_card_num.setText("**** **** **** " + subString);

			} catch (Exception e) {
				// TODO: handle exception
			}

			edt_card_num_cvv.setText(""
					+ UtilInList.ReadSharePrefrence(AddCardActivity.this,
							Constant.SHRED_PR.KEY_USER_CARD_CVV).toString());
			edt_card_name.setText(""
					+ UtilInList.ReadSharePrefrence(AddCardActivity.this,
							Constant.SHRED_PR.KEY_USER_CARD_HOLDER_NAME)
							.toString());

			sp_month.setSelection(Integer.parseInt(UtilInList
					.ReadSharePrefrence(AddCardActivity.this,
							Constant.SHRED_PR.KEY_USER_CARD_EXP_MONTH)
					.toString()));

			int yearPos = 0;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i)
						.toString()
						.equals(UtilInList.ReadSharePrefrence(
								AddCardActivity.this,
								Constant.SHRED_PR.KEY_USER_CARD_EXP_YEAR)
								.toString())) {
					yearPos = i;

				}
			}
			sp_year.setSelection(yearPos);

			linearScan.setVisibility(View.GONE);
		} else {
			edt_card_num.setText("");
			edt_card_num_cvv.setText("");
			edt_card_name.setText("");

			linearScan.setVisibility(View.VISIBLE);
		}

		relativeScanYourCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onScanPress();
			}
		});

		sp_month.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				selected_month = String.valueOf(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		// final String selected;
		sp_year.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				selected_year = parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		edt_card_num.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (edt_card_num.length() < 19) {
					boolean flag = true;
					String eachBlock[] = edt_card_num.getText().toString()
							.split(" ");
					for (int i = 0; i < eachBlock.length; i++) {
						if (eachBlock[i].length() > 4) {
							flag = false;
						}
					}
					if (flag) {

						edt_card_num.setOnKeyListener(new OnKeyListener() {

							@Override
							public boolean onKey(View v, int keyCode,
									KeyEvent event) {

								Log.e("keyCode", ">>" + keyCode + ">>"
										+ KeyEvent.KEYCODE_DEL);
								if (keyCode == KeyEvent.KEYCODE_DEL)
									keyDel = 1;
								return false;
							}
						});

						Log.e("keyDel", "" + keyDel);
						if (keyDel == 0) {

							if (((edt_card_num.getText().length() + 1) % 5) == 0) {

								if (edt_card_num.getText().toString()
										.split(" ").length <= 3) {
									edt_card_num.setText(edt_card_num.getText()
											+ " ");
									edt_card_num.setSelection(edt_card_num
											.getText().length());
								}
							}
							strTemp = edt_card_num.getText().toString();
						} else {
							strTemp = edt_card_num.getText().toString();
							keyDel = 0;
						}

					} else {
						edt_card_num.setText(strTemp);
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		edt_card_num.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_NEXT
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					edt_card_num_cvv.requestFocus();
					return true;
				}
				return false;
			}
		});

	}

	/*
	 * Initialize all UI Views and objects
	 */
	private void init() {

		linearScan = (LinearLayout) findViewById(R.id.linearScan);
		relativeScanYourCard = (RelativeLayout) findViewById(R.id.rl_scan_card);

		edt_card_num = (EditText) findViewById(R.id.edt_card_num);
		edt_card_num_cvv = (EditText) findViewById(R.id.edt_card_num_cvv);
		edt_card_name = (EditText) findViewById(R.id.edt_card_name);
		sp_month = (Spinner) findViewById(R.id.sp_month);
		sp_year = (Spinner) findViewById(R.id.sp_year);

	}

	public void onScanPress() {
		// This method is set up as an onClick handler in the layout xml
		// e.g. android:onClick="onScanPress"

		Intent scanIntent = new Intent(this, CardIOActivity.class);

		// required for authentication with card.io
		scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN,
				Constant.MY_CARDIO_APP_TOKEN);

		// customize these values to suit your needs.
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false); // default:
																			// true
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default:
																		// false
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default:
																				// false

		// hides the manual entry button
		// if set, developers should provide their own manual entry mechanism in
		// the app
		scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default:
																				// false

		// MY_SCAN_REQUEST_CODE is arbitrary and is only used within this
		// activity.
		startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		String resultStr;
		if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
			CreditCard scanResult = data
					.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

			resultStr = "Card Number: " + scanResult.getRedactedCardNumber()
					+ "\n";

			edt_card_num.setText("" + scanResult.getRedactedCardNumber());

			String strCardNum = "" + scanResult.cardNumber;
			edt_card_num.setText("" + strCardNum);

			try {
				String newCardNum = "";
				for (int i = 0; i < strCardNum.length(); i++) {
					if (i != 0 && i % 4 == 0) {
						newCardNum += " ";
					}
					newCardNum += strCardNum.substring(i, i + 1);
				}
				edt_card_num.setText("" + newCardNum);
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (scanResult.isExpiryValid()) {
				resultStr += "Expiration Date: " + scanResult.expiryMonth + "/"
						+ scanResult.expiryYear + "\n";

				sp_month.setSelection(scanResult.expiryMonth);

				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				List<String> list = new ArrayList<String>();

				list.add(0, "Year");
				for (int i = 0; i < 15; i++) {
					list.add(String.valueOf(year + i));
				}

				int yearPos = 0;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).toString()
							.equals("" + scanResult.expiryYear)) {
						yearPos = i;
					}
				}
				sp_year.setSelection(yearPos);

			}

			if (scanResult.cvv != null) {
				edt_card_num_cvv.setText("" + scanResult.cvv);
			}

		} else {
			resultStr = "Scan was canceled.";
		}

		Log.i("" + Constant.AppName, "" + resultStr);

	}

	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {

		case android.R.id.home:
			if (UtilInList
					.ReadSharePrefrence(AddCardActivity.this,
							Constant.SHRED_PR.KEY_USER_CARD_ADDED).toString()
					.equals("1")
					&& UtilInList
							.ReadSharePrefrence(AddCardActivity.this,
									Constant.SHRED_PR.KEY_ADDCARD_FROM)
							.toString().equals("1")) {
				finish();
				overridePendingTransition(R.anim.hold_top,
						R.anim.exit_in_bottom);
			} else {
				try {
					if (NoCardActivity.objNoCard != null) {
						NoCardActivity.objNoCard.finish();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				finish();
				overridePendingTransition(R.anim.hold_top,
						R.anim.exit_in_bottom);
			}
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
		if (flagCardDelete) {
			try {
				if (result.getString("success").equals("true")) {

					linearScan.setVisibility(View.VISIBLE);

					edt_card_name.setText("");
					edt_card_num.setText("");
					edt_card_num_cvv.setText("");
					sp_month.setSelection(0);
					sp_year.setSelection(0);

					UtilInList.WriteSharePrefrence(AddCardActivity.this,
							Constant.SHRED_PR.KEY_USER_CARD_ID, "0");

					UtilInList.WriteSharePrefrence(AddCardActivity.this,
							Constant.SHRED_PR.KEY_USER_CARD_ADDED, "0");

					UtilInList.validateDialog(AddCardActivity.this, result
							.getJSONArray("messages").getString(0),
							Constant.ERRORS.OOPS);

					actionBarAndButtonActions();

				} else {
					UtilInList.validateDialog(AddCardActivity.this, result
							.getJSONArray("errors").getString(0),
							Constant.ERRORS.OOPS);
				}
			} catch (Exception e) {
				Log.v("", "Exception : " + e);
			}
		} else {
			try {
				if (result.getString("success").equals("true")) {

					linearScan.setVisibility(View.GONE);

					UtilInList.WriteSharePrefrence(
							AddCardActivity.this,
							Constant.SHRED_PR.KEY_USER_CARD_ID,
							result.getJSONObject("data").getString(
									"user_card_id"));
					UtilInList.WriteSharePrefrence(AddCardActivity.this,
							Constant.SHRED_PR.KEY_USER_CARD_NUMBER, ""
									+ edt_card_num.getText().toString().trim()
											.replace(" ", ""));
					UtilInList.WriteSharePrefrence(AddCardActivity.this,
							Constant.SHRED_PR.KEY_USER_CARD_CVV, ""
									+ edt_card_num_cvv.getText().toString()
											.trim());

					UtilInList
							.WriteSharePrefrence(
									AddCardActivity.this,
									Constant.SHRED_PR.KEY_USER_CARD_HOLDER_NAME,
									""
											+ edt_card_name.getText()
													.toString().trim());

					UtilInList.WriteSharePrefrence(AddCardActivity.this,
							Constant.SHRED_PR.KEY_USER_CARD_EXP_MONTH, ""
									+ selected_month);

					UtilInList.WriteSharePrefrence(AddCardActivity.this,
							Constant.SHRED_PR.KEY_USER_CARD_EXP_YEAR, ""
									+ selected_year);

					UtilInList.WriteSharePrefrence(AddCardActivity.this,
							Constant.SHRED_PR.KEY_USER_CARD_ADDED, "1");

					if (UtilInList
							.ReadSharePrefrence(AddCardActivity.this,
									Constant.SHRED_PR.KEY_ADDCARD_FROM)
							.toString().equals("1")) {

						startActivity(new Intent(AddCardActivity.this,
								CompletePurchaseActivity.class));
						overridePendingTransition(R.anim.hold_top,
								R.anim.exit_in_bottom);
						finish();

					} else {
						UtilInList.validateDialog(AddCardActivity.this, result
								.getJSONArray("messages").getString(0),
								Constant.AppName);

						actionBarAndButtonActions();
					}

				} else {
					UtilInList.validateDialog(AddCardActivity.this, result
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

		if (UtilInList
				.ReadSharePrefrence(AddCardActivity.this,
						Constant.SHRED_PR.KEY_USER_CARD_ADDED).toString()
				.equals("1")) {
			action_button.setBackgroundResource(R.drawable.delete_card_onclick);
		} else {

			if (UtilInList
					.ReadSharePrefrence(AddCardActivity.this,
							Constant.SHRED_PR.KEY_ADDCARD_FROM).toString()
					.equals("1")) {
				action_button
						.setBackgroundResource(R.drawable.review_action_bar);
			} else {
				action_button.setBackgroundResource(R.drawable.save_onclick);
			}

		}

		// ********** VIP Text: ******************************//
		Typeface typeAkzidgrobemedex = Typeface.createFromAsset(getAssets(),
				"helve_unbold.ttf");
		TextView txtVIP = (TextView) actionBar.getCustomView().findViewById(
				R.id.txtVIP);
		txtVIP.setTypeface(typeAkzidgrobemedex);

		if (UtilInList
				.ReadSharePrefrence(AddCardActivity.this,
						Constant.SHRED_PR.KEY_VIP_STATUS).toString()
				.equals("vip")) {

			txtVIP.setVisibility(View.VISIBLE);

		} else {
			txtVIP.setVisibility(View.GONE);
		}
		// *****************************************************//

		relativeActionBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (UtilInList
						.ReadSharePrefrence(AddCardActivity.this,
								Constant.SHRED_PR.KEY_USER_CARD_ADDED)
						.toString().equals("1")) {

					flagCardDelete = true;

					List<NameValuePair> params = new ArrayList<NameValuePair>();

					params.add(new BasicNameValuePair("user_card_id", ""
							+ UtilInList.ReadSharePrefrence(
									AddCardActivity.this,
									Constant.SHRED_PR.KEY_USER_CARD_ID)
									.toString()));
					params.add(new BasicNameValuePair("PHPSESSIONID", ""
							+ UtilInList.ReadSharePrefrence(
									AddCardActivity.this,
									Constant.SHRED_PR.KEY_SESSIONID)));

					new WebServiceDataPosterAsyncTask(AddCardActivity.this,
							params, Constant.API + Constant.ACTIONS.REMOVE_CARD)
							.execute();

				} else {

					if (edt_card_num.getText().toString().equals("")) {

						UtilInList.validateDialog(AddCardActivity.this,
								Constant.ERRORS.PLZ_CARD_NUMBER,
								Constant.ERRORS.OOPS);
					} else if (edt_card_name.getText().toString().equals("")) {
						UtilInList.validateDialog(AddCardActivity.this,
								Constant.ERRORS.PLZ_CARD_NAME,
								Constant.ERRORS.OOPS);
					} else if (selected_month.equals("Month")) {
						UtilInList.validateDialog(AddCardActivity.this,
								Constant.ERRORS.PLZ_CARD_MONTH,
								Constant.ERRORS.OOPS);
					} else if (selected_year.equals("Year")) {
						UtilInList.validateDialog(AddCardActivity.this,
								Constant.ERRORS.PLZ_CARD_YEAR,
								Constant.ERRORS.OOPS);
					} else {

						flagCardDelete = false;

						List<NameValuePair> params = new ArrayList<NameValuePair>();

						params.add(new BasicNameValuePair("user_card_id", "0"));
						params.add(new BasicNameValuePair("card_type", "visa"));
						params.add(new BasicNameValuePair("card_number",
								edt_card_num.getText().toString().trim()
										.replace(" ", "")));
						params.add(new BasicNameValuePair("card_name",
								edt_card_name.getText().toString().trim()));
						params.add(new BasicNameValuePair("card_exp_year",
								selected_year));
						params.add(new BasicNameValuePair("card_exp_month",
								selected_month));
						params.add(new BasicNameValuePair("set_default", "1"));
						params.add(new BasicNameValuePair("PHPSESSIONID", ""
								+ UtilInList.ReadSharePrefrence(
										AddCardActivity.this,
										Constant.SHRED_PR.KEY_SESSIONID)));

						new WebServiceDataPosterAsyncTask(AddCardActivity.this,
								params, Constant.API
										+ Constant.ACTIONS.ADD_CARD).execute();

					}
				}

			}
		});

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (UtilInList
				.ReadSharePrefrence(AddCardActivity.this,
						Constant.SHRED_PR.KEY_USER_CARD_ADDED).toString()
				.equals("1")
				&& UtilInList
						.ReadSharePrefrence(AddCardActivity.this,
								Constant.SHRED_PR.KEY_ADDCARD_FROM).toString()
						.equals("1")) {
			finish();
			overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
		} else {
			try {
				if (NoCardActivity.objNoCard != null) {
					NoCardActivity.objNoCard.finish();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			finish();
			overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
		}
	}
}
