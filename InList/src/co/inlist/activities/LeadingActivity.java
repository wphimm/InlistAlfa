package co.inlist.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.inlist.fragments.LeadingFragment;
import co.inlist.util.Constant;
import co.inlist.util.GPSTracker;
import co.inlist.util.UtilInList;
import co.inlist.util.ViewPagerCustomDuration;

public class LeadingActivity extends FragmentActivity {
	// ...
	FragmentPagerAdapter adapterViewPager;
	private TextView txtSkip, txtLogin, txtSignUp;
	private LinearLayout rl_btn_register;
	private LinearLayout rl_btn_login;

	ViewPagerCustomDuration vpPager;
	int pagerPosition = 0;
	Timer timer;
	MyTimerTask myTimerTask;
	private ImageView img1;
	private ImageView img2;
	private ImageView img3;
	private ImageView img4;
	Typeface typeAkzidgrobeligex, typeAvenir,
			typeLeaguegothic_condensedregular;

	public static GPSTracker gps;
	public static LeadingActivity laObj;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_intro);

		init();

		laObj = this;

		rl_btn_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LeadingActivity.this,
						SignUpActivity.class));
				overridePendingTransition(R.anim.enter_from_bottom,
						R.anim.hold_bottom);
			}
		});

		rl_btn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UtilInList.WriteSharePrefrence(LeadingActivity.this,
						Constant.SHRED_PR.KEY_LOGIN_FROM, "0");
				startActivity(new Intent(LeadingActivity.this,
						LoginActivity.class));
				overridePendingTransition(R.anim.enter_from_bottom,
						R.anim.hold_bottom);
			}
		});

		txtSkip.setText(Html
				.fromHtml("<p><font color=\"#DFBB6A\"><u>Skip for now</u></font></p>"));

		vpPager = (ViewPagerCustomDuration) findViewById(R.id.vpPager);
		adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
		vpPager.setAdapter(adapterViewPager);

		vpPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int page) {
				// TODO Auto-generated method stub
				switch (page) {
				case 0:
					img1.setBackgroundResource(R.drawable.pageview_dot_active);
					img2.setBackgroundResource(R.drawable.pageview_dot);
					img3.setBackgroundResource(R.drawable.pageview_dot);
					img4.setBackgroundResource(R.drawable.pageview_dot);

					break;
				case 1:
					img1.setBackgroundResource(R.drawable.pageview_dot);
					img2.setBackgroundResource(R.drawable.pageview_dot_active);
					img3.setBackgroundResource(R.drawable.pageview_dot);
					img4.setBackgroundResource(R.drawable.pageview_dot);

					break;
				case 2:
					img1.setBackgroundResource(R.drawable.pageview_dot);
					img2.setBackgroundResource(R.drawable.pageview_dot);
					img3.setBackgroundResource(R.drawable.pageview_dot_active);
					img4.setBackgroundResource(R.drawable.pageview_dot);

					break;
				case 3:
					img1.setBackgroundResource(R.drawable.pageview_dot);
					img2.setBackgroundResource(R.drawable.pageview_dot);
					img3.setBackgroundResource(R.drawable.pageview_dot);
					img4.setBackgroundResource(R.drawable.pageview_dot_active);

					break;

				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		txtSkip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LeadingActivity.this,
						HomeScreenActivity.class));
				overridePendingTransition(R.anim.enter_from_bottom,
						R.anim.hold_bottom);
			}
		});

	}

	public void init() {

		txtSkip = (TextView) findViewById(R.id.txtSkip);
		txtLogin = (TextView) findViewById(R.id.txtLogin);
		txtSignUp = (TextView) findViewById(R.id.txtSignUp);
		rl_btn_register = (LinearLayout) findViewById(R.id.rl_btn_register);
		rl_btn_login = (LinearLayout) findViewById(R.id.rl_btn_login);

		img1 = (ImageView) findViewById(R.id.img1);
		img2 = (ImageView) findViewById(R.id.img2);
		img3 = (ImageView) findViewById(R.id.img3);
		img4 = (ImageView) findViewById(R.id.img4);

		typeAkzidgrobeligex = Typeface.createFromAsset(getAssets(),
				"akzidgrobeligex.ttf");
		typeLeaguegothic_condensedregular = Typeface.createFromAsset(
				getAssets(), "leaguegothic_condensedregular.otf");
		typeAvenir = Typeface.createFromAsset(getAssets(), "avenir.ttc");

		txtLogin.setTypeface(typeAvenir);
		txtSignUp.setTypeface(typeAvenir);
		txtSkip.setTypeface(typeAvenir);
	}

	public static class MyPagerAdapter extends FragmentPagerAdapter {
		private static int NUM_ITEMS = 4;

		public MyPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		// Returns total number of pages
		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		// Returns the fragment to display for that page
		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0: // Fragment # 0 - This will show FirstFragment
				return LeadingFragment.newInstance(0, "Page # 1");
			case 1: // Fragment # 0 - This will show FirstFragment different
					// title
				return LeadingFragment.newInstance(1, "Page # 2");
			case 2: // Fragment # 1 - This will show SecondFragment
				return LeadingFragment.newInstance(2, "Page # 3");
			case 3: // Fragment # 1 - This will show SecondFragment
				return LeadingFragment.newInstance(3, "Page # 4");
			default:
				return null;
			}
		}

		// Returns the page title for the top indicator
		@Override
		public CharSequence getPageTitle(int position) {
			return "Page " + position;
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			if (timer != null) {
				timer.cancel();
			}

			if (myTimerTask != null) {
				myTimerTask.cancel();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		gps = new GPSTracker(LeadingActivity.this);
		// check if GPS enabled
		if (gps.canGetLocation()) {

		} else {
			gps.showSettingsAlert();
		}
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		myTimerTask = new MyTimerTask();
		timer.schedule(myTimerTask, 2000, 2000);
	}

	class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				public void run() {
					pagerPosition++;
					if (pagerPosition == 4)
						pagerPosition = 0;

					vpPager.setScrollDurationFactor(4);
					vpPager.setCurrentItem(pagerPosition);

				}
			});
		}
	}

}