package co.inlist.activities;

import co.inlist.util.Constant;
import co.inlist.util.UtilInList;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VipMembershipReview extends Activity implements
		ActionBar.OnNavigationListener {

	Context context = this;
	Typeface typeAkzidgrobemedex, typehelve_unbold;
	TextView t1, t2, t3, t4, t5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vip_membership_review);

		init();
		actionBarAndButtonActions();
	}

	private void init() {
		// TODO Auto-generated method stub
		t1 = (TextView) findViewById(R.id.t1);
		t2 = (TextView) findViewById(R.id.t2);
		t3 = (TextView) findViewById(R.id.t3);
		t4 = (TextView) findViewById(R.id.t4);
		t5 = (TextView) findViewById(R.id.t5);
		
		typeAkzidgrobemedex = Typeface.createFromAsset(context.getAssets(),
				"akzidgrobemedex.ttf");
		typehelve_unbold = Typeface.createFromAsset(context.getAssets(),
				"helve_unbold.ttf");
		
		t1.setTypeface(typeAkzidgrobemedex);
		t2.setTypeface(typehelve_unbold);
		t3.setTypeface(typeAkzidgrobemedex);
		t4.setTypeface(typehelve_unbold);
		t5.setTypeface(typehelve_unbold);
	}

	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {

		case android.R.id.home:
			ProfileActivity.profObj.finish();
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

	/*
	 * moth
	 */
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

		action_button.setBackgroundResource(R.drawable.chk_out_action_btn);

		// ********** VIP Text: ******************************//
		Typeface typeAkzidgrobemedex = Typeface.createFromAsset(getAssets(),
				"helve_unbold.ttf");
		TextView txtVIP = (TextView) actionBar.getCustomView().findViewById(
				R.id.txtVIP);
		txtVIP.setTypeface(typeAkzidgrobemedex);

		if (UtilInList
				.ReadSharePrefrence(VipMembershipReview.this,
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

				ProfileActivity.profObj.finish();
				finish();
				overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
			}
		});

		relativeActionBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				ProfileActivity.profObj.finish();
				finish();
				overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
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
