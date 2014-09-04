package co.inlist.activities;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.inlist.util.Constant;
import co.inlist.util.UtilInList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class GalleryActivity extends Activity implements
		ActionBar.OnNavigationListener {

	DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	ViewPager pager;
	Context context = this;

	LinearLayout linearFooter;
	private ArrayList<HashMap<String, String>> localGallery = new ArrayList<HashMap<String, String>>();

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallary);

		options = new DisplayImageOptions.Builder().showStubImage(0)
				.showImageForEmptyUri(0).cacheInMemory().cacheOnDisc()
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		getData();
		localGallery = EventDetailsActivity.edObj.gallery;

		linearFooter = (LinearLayout) findViewById(R.id.footer);
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(localGallery));

		for (int i = 0; i < localGallery.size(); i++) {
			ImageView image = new ImageView(GalleryActivity.this);
			if (i == 0) {
				image.setBackgroundResource(R.drawable.pageview_dot_active);
			} else {
				image.setBackgroundResource(R.drawable.pageview_dot);
			}
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(10, 0, 0, 0);
			image.setLayoutParams(lp);
			linearFooter.addView(image);
		}

		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int pos) {
				// TODO Auto-generated method stub
				linearFooter.removeAllViews();
				for (int i = 0; i < localGallery.size(); i++) {
					ImageView image = new ImageView(GalleryActivity.this);
					if (i == pos) {
						image.setBackgroundResource(R.drawable.pageview_dot_active);
					} else {
						image.setBackgroundResource(R.drawable.pageview_dot);
					}
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
					lp.setMargins(10, 0, 0, 0);
					image.setLayoutParams(lp);
					linearFooter.addView(image);
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

		actionBarAndButtonActions();
	}

	private void getData() {
		// TODO Auto-generated method stub
		String result = UtilInList.ReadSharePrefrence(GalleryActivity.this,
				Constant.SHRED_PR.KEY_RESULT_GALLERY);
		try {
			JSONObject jObject = new JSONObject(result);
			String str_temp = jObject.getString("status");
			if (str_temp.equals("success")) {
				JSONObject jObjectData = new JSONObject(
						jObject.getString("data"));
				JSONArray data = jObjectData.getJSONArray("gallery");
				Log.e("Length of json array ----->", "" + data.length());

				localGallery.clear();

				for (int i = 0; i < data.length(); i++) {
					JSONObject obj = data.getJSONObject(i);
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("thumbnail", "" + obj.getString("thumbnail"));
					map.put("source", "" + obj.getString("source"));
					map.put("description", "" + obj.getString("description"));

					localGallery.add(map);
				}
			}

		} catch (JSONException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class ImagePagerAdapter extends PagerAdapter {

		// private String[] images;
		private LayoutInflater inflater;
		ArrayList<HashMap<String, String>> locallist;

		ImagePagerAdapter(ArrayList<HashMap<String, String>> list) {

			locallist = list;
			inflater = getLayoutInflater();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return locallist.size();
		}

		@SuppressLint("NewApi")
		@Override
		public Object instantiateItem(View view, int position) {
			final View imageLayout = inflater.inflate(
					R.layout.itempagerimage_gallery, null);

			final ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);

			imageLoader.displayImage(
					"" + locallist.get(position).get("source"), imageView,
					options);

			((ViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
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

		ImageButton action_button = (ImageButton) actionBar.getCustomView()
				.findViewById(R.id.btn_action_bar);

		action_button.setBackgroundResource(R.drawable.logout_action_bar);

		action_button.setVisibility(View.INVISIBLE);

		// ********** VIP Text: ******************************//
		Typeface typeAkzidgrobemedex = Typeface.createFromAsset(getAssets(),
				"helve_unbold.ttf");
		TextView txtVIP = (TextView) actionBar.getCustomView().findViewById(
				R.id.txtVIP);
		txtVIP.setTypeface(typeAkzidgrobemedex);

		if (UtilInList
				.ReadSharePrefrence(GalleryActivity.this,
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
		finish();
		overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
	}
}
