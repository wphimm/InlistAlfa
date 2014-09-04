package co.inlist.activities;

import co.inlist.util.Constant;
import co.inlist.util.UtilInList;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NoCardActivity extends Activity implements
		ActionBar.OnNavigationListener {

	public static NoCardActivity objNoCard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.no_card_screen);

		objNoCard = this;
		actionBarAndButtonActions();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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

		action_button
				.setBackgroundResource(R.drawable.no_card_action_btn_onclick);

		// ********** VIP Text: ******************************//
		Typeface typeAkzidgrobemedex = Typeface.createFromAsset(getAssets(),
				"helve_unbold.ttf");
		TextView txtVIP = (TextView) actionBar.getCustomView().findViewById(
				R.id.txtVIP);
		txtVIP.setTypeface(typeAkzidgrobemedex);

		if (UtilInList
				.ReadSharePrefrence(NoCardActivity.this,
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
				startActivity(new Intent(NoCardActivity.this,
						AddCardActivity.class));
				overridePendingTransition(R.anim.enter_from_bottom,
						R.anim.hold_bottom);

			}
		});

		relativeActionBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(NoCardActivity.this,
						AddCardActivity.class));
				overridePendingTransition(R.anim.enter_from_bottom,
						R.anim.hold_bottom);

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
