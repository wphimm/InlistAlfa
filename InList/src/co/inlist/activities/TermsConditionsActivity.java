package co.inlist.activities;

import co.inlist.util.Constant;
import co.inlist.util.UtilInList;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class TermsConditionsActivity extends Activity implements
		ActionBar.OnNavigationListener {

	WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_conditions);

		actionBarAndButtonActions();

		webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(Constant.TERMS_CONDITIONS);
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
					.ReadSharePrefrence(TermsConditionsActivity.this,
							Constant.SHRED_PR.KEY_TERMS_FROM).toString()
					.equals("1")) {
				finish();
				overridePendingTransition(R.anim.hold_top,
						R.anim.exit_in_bottom);
			} else {
				finish();
				overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
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
				.ReadSharePrefrence(TermsConditionsActivity.this,
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
		if (UtilInList
				.ReadSharePrefrence(TermsConditionsActivity.this,
						Constant.SHRED_PR.KEY_TERMS_FROM).toString()
				.equals("1")) {
			finish();
			overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
		} else {
			finish();
			overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
		}
	}
}