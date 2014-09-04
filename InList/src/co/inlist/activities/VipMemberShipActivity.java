package co.inlist.activities;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import co.inlist.adapter.HorizontalListAdapter;
import co.inlist.facebook.android.AsyncFacebookRunner;
import co.inlist.facebook.android.AsyncFacebookRunner.RequestListener;
import co.inlist.facebook.android.DialogError;
import co.inlist.facebook.android.Facebook;
import co.inlist.facebook.android.Facebook.DialogListener;
import co.inlist.facebook.android.FacebookError;
import co.inlist.util.Constant;
import co.inlist.util.HorizontalListView;
import co.inlist.util.MyProgressbar;
import co.inlist.util.UtilInList;

import com.parse.entity.mime.HttpMultipartMode;
import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.FileBody;
import com.parse.entity.mime.content.StringBody;

public class VipMemberShipActivity extends Activity implements
		ActionBar.OnNavigationListener {

	ImageView img1, img2, img3, img4, imgProfile;
	TextView txtIncome1, txtIncome2, txtIncome3, txtIncome4;
	int height, width;
	public HorizontalListView horizontalList;
	EditText editInviteCode, editOccupation, editMostFrequentedClubs,
			editOtherClub;

	Uri imageUri = null;
	static Cursor cursor = null;
	static String camera_pathname = "";
	Bitmap bmp;

	int selectedIncomePosition = -1;
	int selectedMusicTypePosition = -1;

	ArrayList<HashMap<String, String>> listMusic = new ArrayList<HashMap<String, String>>();

	// Facebook:
	private Facebook facebook = new Facebook(Constant.FB_API_KEY);
	private AsyncFacebookRunner mAsyncRunner;
	private SharedPreferences mPrefs;
	boolean flagFB = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vip_membership_screen);

		getData();
		init();
		actionBarAndButtonActions();

		String fileName = "Camera_Example.jpg";
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, fileName);
		imageUri = this.getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		horizontalList.setAdapter(new HorizontalListAdapter(listMusic,
				getApplicationContext(), VipMemberShipActivity.this,
				horizontalList, -1));

		Handler hn = new Handler();
		hn.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				alert();
			}
		}, 200);

		horizontalList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				selectedMusicTypePosition = position;
				horizontalList.setAdapter(new HorizontalListAdapter(listMusic,
						getApplicationContext(), VipMemberShipActivity.this,
						horizontalList, position));
				if (position > 0)
					horizontalList.setSelection(position - 1);
			}
		});

		ViewTreeObserver vto = img1.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				img1.getViewTreeObserver().removeOnPreDrawListener(this);
				height = img1.getMeasuredHeight();
				width = img1.getMeasuredWidth();
				return true;
			}
		});

		img1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectedIncomePosition = 0;
				img1.setBackgroundResource(R.drawable.circle_selected);
				img2.setBackgroundResource(R.drawable.circle_notselected);
				img3.setBackgroundResource(R.drawable.circle_notselected);
				img4.setBackgroundResource(R.drawable.circle_notselected);

				img1.getLayoutParams().width = (int) (1.25 * width);
				img1.getLayoutParams().height = (int) (1.25 * height);

				img2.getLayoutParams().width = width;
				img2.getLayoutParams().height = height;
				img3.getLayoutParams().width = width;
				img3.getLayoutParams().height = height;
				img4.getLayoutParams().width = width;
				img4.getLayoutParams().height = height;

				txtIncome1.setTextColor(getResources().getColor(
						R.color.light_yellow));
				txtIncome2.setTextColor(getResources().getColor(R.color.black));
				txtIncome3.setTextColor(getResources().getColor(R.color.black));
				txtIncome4.setTextColor(getResources().getColor(R.color.black));

				resetText();
			}
		});

		img2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectedIncomePosition = 1;
				img2.setBackgroundResource(R.drawable.circle_selected);
				img1.setBackgroundResource(R.drawable.circle_notselected);
				img3.setBackgroundResource(R.drawable.circle_notselected);
				img4.setBackgroundResource(R.drawable.circle_notselected);

				img2.getLayoutParams().width = (int) (1.25 * width);
				img2.getLayoutParams().height = (int) (1.25 * height);

				img1.getLayoutParams().width = width;
				img1.getLayoutParams().height = height;
				img3.getLayoutParams().width = width;
				img3.getLayoutParams().height = height;
				img4.getLayoutParams().width = width;
				img4.getLayoutParams().height = height;

				txtIncome1.setTextColor(getResources().getColor(R.color.black));
				txtIncome2.setTextColor(getResources().getColor(
						R.color.light_yellow));
				txtIncome3.setTextColor(getResources().getColor(R.color.black));
				txtIncome4.setTextColor(getResources().getColor(R.color.black));

				resetText();
			}
		});
		img3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectedIncomePosition = 2;
				img3.setBackgroundResource(R.drawable.circle_selected);
				img2.setBackgroundResource(R.drawable.circle_notselected);
				img1.setBackgroundResource(R.drawable.circle_notselected);
				img4.setBackgroundResource(R.drawable.circle_notselected);

				img3.getLayoutParams().width = (int) (1.25 * width);
				img3.getLayoutParams().height = (int) (1.25 * height);

				img2.getLayoutParams().width = width;
				img2.getLayoutParams().height = height;
				img1.getLayoutParams().width = width;
				img1.getLayoutParams().height = height;
				img4.getLayoutParams().width = width;
				img4.getLayoutParams().height = height;

				txtIncome1.setTextColor(getResources().getColor(R.color.black));
				txtIncome2.setTextColor(getResources().getColor(R.color.black));
				txtIncome3.setTextColor(getResources().getColor(
						R.color.light_yellow));
				txtIncome4.setTextColor(getResources().getColor(R.color.black));

				resetText();
			}
		});
		img4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectedIncomePosition = 3;
				img4.setBackgroundResource(R.drawable.circle_selected);
				img2.setBackgroundResource(R.drawable.circle_notselected);
				img3.setBackgroundResource(R.drawable.circle_notselected);
				img1.setBackgroundResource(R.drawable.circle_notselected);

				img4.getLayoutParams().width = (int) (1.25 * width);
				img4.getLayoutParams().height = (int) (1.25 * height);

				img2.getLayoutParams().width = width;
				img2.getLayoutParams().height = height;
				img3.getLayoutParams().width = width;
				img3.getLayoutParams().height = height;
				img1.getLayoutParams().width = width;
				img1.getLayoutParams().height = height;

				txtIncome1.setTextColor(getResources().getColor(R.color.black));
				txtIncome2.setTextColor(getResources().getColor(R.color.black));
				txtIncome3.setTextColor(getResources().getColor(R.color.black));
				txtIncome4.setTextColor(getResources().getColor(
						R.color.light_yellow));

				resetText();
			}
		});

		imgProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				registerForContextMenu(v);
				v.showContextMenu();
			}
		});

	}

	private void alert() {
		// TODO Auto-generated method stub
		AlertDialog.Builder alert = new AlertDialog.Builder(
				VipMemberShipActivity.this);
		alert.setTitle(Constant.AppName);
		alert.setMessage("Optional: We suggest linking your Facebook to expedite your VIP application. We will not ask permission to post to your wall, we will only receive your publication information");
		alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (UtilInList
						.isInternetConnectionExist(getApplicationContext())) {
					loginToFacebook();
				} else {
					UtilInList.validateDialog(VipMemberShipActivity.this, ""
							+ Constant.network_error, Constant.AppName);
				}
			}
		});
		alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		alert.create();
		alert.show();
	}

	private void getData() {
		// TODO Auto-generated method stub
		String result1 = UtilInList.ReadSharePrefrence(
				VipMemberShipActivity.this, Constant.SHRED_PR.KEY_RESULT_MUSIC);
		Log.i("result1", ">>" + result1);

		try {
			/*
			 * Prepare registration response write in file mode private
			 */
			JSONObject result = new JSONObject(result1);
			String str_temp = result.getString("status");
			if (str_temp.equals("success")) {
				JSONObject jObjectData = new JSONObject(
						result.getString("data"));
				JSONArray data = jObjectData.getJSONArray("music_types");
				Log.e("Length of json array ----->", "" + data.length());
				listMusic.clear();
				for (int i = 0; i < data.length(); i++) {
					JSONObject obj = data.getJSONObject(i);
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("music_type_id",
							"" + obj.getString("music_type_id"));
					map.put("title", "" + obj.getString("title"));
					listMusic.add(map);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void resetText() {
		// TODO Auto-generated method stub
		txtIncome1.setText(getResources().getString(R.string.income1));
		txtIncome2.setText(getResources().getString(R.string.income2));
		txtIncome3.setText(getResources().getString(R.string.income3));
		txtIncome4.setText(getResources().getString(R.string.income4));
	}

	private void init() {
		// TODO Auto-generated method stub
		imgProfile = (ImageView) findViewById(R.id.imgprofile);
		img1 = (ImageView) findViewById(R.id.img1);
		img2 = (ImageView) findViewById(R.id.img2);
		img3 = (ImageView) findViewById(R.id.img3);
		img4 = (ImageView) findViewById(R.id.img4);
		txtIncome1 = (TextView) findViewById(R.id.txtIncome1);
		txtIncome2 = (TextView) findViewById(R.id.txtIncome2);
		txtIncome3 = (TextView) findViewById(R.id.txtIncome3);
		txtIncome4 = (TextView) findViewById(R.id.txtIncome4);

		editInviteCode = (EditText) findViewById(R.id.editInviteCode);
		editMostFrequentedClubs = (EditText) findViewById(R.id.editMostFrequentedClubs);
		editOccupation = (EditText) findViewById(R.id.editOccupation);
		editOtherClub = (EditText) findViewById(R.id.editOtherClub);

		horizontalList = (HorizontalListView) findViewById(R.id.listview);
		mAsyncRunner = new AsyncFacebookRunner(facebook);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int rowWidth = width / listMusic.size();

		android.view.ViewGroup.LayoutParams rel_btn1 = horizontalList
				.getLayoutParams();
		rel_btn1.height = rowWidth;
		rel_btn1.width = LayoutParams.MATCH_PARENT;
		horizontalList.setLayoutParams(rel_btn1);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		String[] menuItems = new String[] { "Photo Library", "Camera", "Cancel" };
		menu.add(Menu.NONE, 0, 0, menuItems[0]);
		menu.add(Menu.NONE, 1, 1, menuItems[1]);
		menu.add(Menu.NONE, 2, 2, menuItems[2]);

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	public boolean onContextItemSelected(MenuItem item) {

		int menuItemIndex = item.getItemId();
		switch (menuItemIndex) {
		case 0: {
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, 2);
			break;
		}
		case 1: {
			if (imageUri != null) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
				startActivityForResult(intent, 1);
			}
		}
			break;
		case 2:
			break;
		}

		return true;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		flagFB = false;
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {

				String imageId = convertImageUriToFile(imageUri,
						VipMemberShipActivity.this);
				new LoadImagesFromSDCard().execute("" + imageId);

			}
		} else if (requestCode == 2) {
			camera_pathname = null;
			if (resultCode == Activity.RESULT_OK) {
				Uri uri = null;
				if (data != null) {
					try {
						uri = data.getData();

						String imageId = convertImageUriToFile(uri,
								VipMemberShipActivity.this);
						new LoadImagesFromSDCard().execute("" + imageId);

					} catch (Exception e) {
						// TODO: handle exception
						Log.i("Exception", "" + e);
					}
				}

			}
		}
	}

	@SuppressWarnings("deprecation")
	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
		Cursor cursor = this.managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/************ Convert Image Uri path to physical path **************/

	@SuppressWarnings("deprecation")
	public static String convertImageUriToFile(Uri imageUri, Activity activity) {

		int imageID = 0;
		try {

			/*********** Which columns values want to get *******/
			String[] proj = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID,
					MediaStore.Images.Thumbnails._ID,
					MediaStore.Images.ImageColumns.ORIENTATION };

			cursor = activity.managedQuery(

			imageUri, // Get data for specific image URI
					proj, // Which columns to return
					null, // WHERE clause; which rows to return (all rows)
					null, // WHERE clause selection arguments (none)
					null // Order-by clause (ascending by name)

					);

			// Get Query Data
			int columnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
			int columnIndexThumb = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
			int file_ColumnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

			int size = cursor.getCount();

			/******* If size is 0, there are no images on the SD Card. *****/
			if (size == 0) {

			} else {
				@SuppressWarnings("unused")
				int thumbID = 0;
				if (cursor.moveToFirst()) {
					/**************** Captured image details ************/
					/***** Used to show image on view in LoadImagesFromSDCard class ******/
					imageID = cursor.getInt(columnIndex);
					thumbID = cursor.getInt(columnIndexThumb);
					String Path = cursor.getString(file_ColumnIndex);
					camera_pathname = Path;
				}
			}
		} finally {
		}

		return "" + imageID;
	}

	public class LoadImagesFromSDCard extends AsyncTask<String, Void, Void> {

		private ProgressDialog Dialog = new ProgressDialog(
				VipMemberShipActivity.this);

		protected void onPreExecute() {
			/****** NOTE: You can call UI Element here. *****/

			// Progress Dialog
			Dialog.setMessage(" Loading image from Sdcard..");
			Dialog.show();
		}

		// Call after onPreExecute method
		protected Void doInBackground(String... urls) {

			Bitmap bitmap = null;
			Bitmap newBitmap = null;
			Uri uri = null;

			try {

				uri = Uri.withAppendedPath(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ""
								+ urls[0]);

				/************** Decode an input stream into a bitmap. *********/
				bitmap = BitmapFactory.decodeStream(VipMemberShipActivity.this
						.getContentResolver().openInputStream(uri));

				if (bitmap != null) {
					/********* Creates a new bitmap, scaled from an existing bitmap. ***********/
					newBitmap = Bitmap
							.createScaledBitmap(bitmap, 110, 95, true);
					bitmap.recycle();
					if (newBitmap != null) {
						bmp = newBitmap;
					}
				}
			} catch (IOException e) {
				// Error fetching image, try to recover

				/********* Cancel execution of this task. **********/
				cancel(true);
			}
			return null;
		}

		protected void onPostExecute(Void unused) {
			Dialog.dismiss();
			if (bmp != null) {
				Bitmap newbm = decodeFile(camera_pathname);
				newbm = Bitmap.createScaledBitmap(newbm, 110, 95, true);
				imgProfile.setVisibility(View.VISIBLE);
				imgProfile.setImageBitmap(newbm);
			}
		}

		public Bitmap decodeFile(String path) {// you can provide file path here
			int orientation;
			try {
				if (path == null) {
					return null;
				}
				// decode image size
				BitmapFactory.Options o = new BitmapFactory.Options();
				o.inJustDecodeBounds = true;
				// Find the correct scale value. It should be the power of 2.
				final int REQUIRED_SIZE = 70;
				int width_tmp = o.outWidth, height_tmp = o.outHeight;
				int scale = 0;
				while (true) {
					if (width_tmp / 2 < REQUIRED_SIZE
							|| height_tmp / 2 < REQUIRED_SIZE)
						break;
					width_tmp /= 2;
					height_tmp /= 2;
					scale++;
				}
				// decode with inSampleSize
				BitmapFactory.Options o2 = new BitmapFactory.Options();
				o2.inSampleSize = scale;
				Bitmap bm = BitmapFactory.decodeFile(path, o2);
				Bitmap bitmap = bm;

				ExifInterface exif = new ExifInterface(path);

				orientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION, 1);

				Log.e("ExifInteface .........", "rotation =" + orientation);

				// exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

				Log.e("orientation", "" + orientation);
				Matrix m = new Matrix();

				if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
					m.postRotate(180);
					// m.postScale((float) bm.getWidth(), (float)
					// bm.getHeight());
					// if(m.preRotate(90)){
					Log.e("in orientation", "" + orientation);
					bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
							bm.getHeight(), m, true);
					return bitmap;
				} else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
					m.postRotate(90);
					Log.e("in orientation", "" + orientation);
					bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
							bm.getHeight(), m, true);
					return bitmap;
				} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
					m.postRotate(270);
					Log.e("in orientation", "" + orientation);
					bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
							bm.getHeight(), m, true);
					return bitmap;
				}
				return bitmap;
			} catch (Exception e) {
				return null;
			}

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
			if (UtilInList
					.ReadSharePrefrence(VipMemberShipActivity.this,
							Constant.SHRED_PR.KEY_ADDCARD_FROM).toString()
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

	private boolean isValidate() {
		// TODO Auto-generated method stub
		if (camera_pathname.length() == 0) {
			UtilInList.validateDialog(VipMemberShipActivity.this,
					Constant.ERRORS.PLZ_PHOTO, Constant.ERRORS.OOPS);
			return false;
		}
		if (selectedIncomePosition == -1) {
			UtilInList.validateDialog(VipMemberShipActivity.this,
					Constant.ERRORS.PLZ_ANNUAL_INCOME, Constant.ERRORS.OOPS);
			return false;
		}

		if (selectedMusicTypePosition == -1) {
			UtilInList.validateDialog(VipMemberShipActivity.this,
					Constant.ERRORS.PLZ_MUSIC_TYPE, Constant.ERRORS.OOPS);
			return false;
		}

		if (editMostFrequentedClubs.getText().toString().trim().length() == 0) {
			UtilInList.validateDialog(VipMemberShipActivity.this,
					Constant.ERRORS.PLZ_ENTER_MFC, Constant.ERRORS.OOPS);
			return false;
		}

		if (editOtherClub.getText().toString().trim().length() == 0) {
			UtilInList.validateDialog(VipMemberShipActivity.this,
					Constant.ERRORS.PLZ_ENTER_OCM, Constant.ERRORS.OOPS);
			return false;
		}

		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int arg0, long arg1) {
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

		if (UtilInList
				.ReadSharePrefrence(VipMemberShipActivity.this,
						Constant.SHRED_PR.KEY_ADDCARD_FROM).toString()
				.equals("1")) {
			action_button.setBackgroundResource(R.drawable.continue_actionbar);
		} else {
			action_button.setBackgroundResource(R.drawable.submit_onclick);
		}

		// ********** VIP Text: ******************************//
		Typeface typeAkzidgrobemedex = Typeface.createFromAsset(getAssets(),
				"helve_unbold.ttf");
		TextView txtVIP = (TextView) actionBar.getCustomView().findViewById(
				R.id.txtVIP);
		txtVIP.setTypeface(typeAkzidgrobemedex);

		if (UtilInList
				.ReadSharePrefrence(VipMemberShipActivity.this,
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

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editInviteCode.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(
						editMostFrequentedClubs.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(editOccupation.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(editOtherClub.getWindowToken(), 0);

				if (isValidate()) {
					if (UtilInList
							.isInternetConnectionExist(getApplicationContext())) {

						new UploadImage(VipMemberShipActivity.this).execute();

					} else {
						UtilInList.validateDialog(VipMemberShipActivity.this,
								"" + "" + Constant.network_error,
								Constant.ERRORS.OOPS);

					}
				}

			}
		});
	}

	public class UploadImage extends AsyncTask<String, String, String> {

		private MyProgressbar dialog;
		Context context;

		public UploadImage(Context context) {
			dialog = new MyProgressbar(context);
			this.context = context;
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
			String response = "";
			try {

				HttpClient client = UtilInList.getNewHttpClient();
				HttpResponse response1 = null;

				HttpPost poster = new HttpPost("" + Constant.API
						+ Constant.ACTIONS.REQUEST_VIP);

				FileBody fbody = null;
				MultipartEntity entity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);

				Log.i("filePath", "" + camera_pathname);
				if (flagFB) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
					byte[] imageBytes = baos.toByteArray();
					String encodedImage = Base64.encodeToString(imageBytes,
							Base64.DEFAULT);
					entity.addPart("photo", new StringBody(encodedImage));
				} else {
					File image = new File(camera_pathname);
					fbody = new FileBody(image, "image/jpeg");
					entity.addPart("photo", fbody);
				}

				entity.addPart(
						"user_id",
						new StringBody(""
								+ UtilInList.ReadSharePrefrence(
										VipMemberShipActivity.this,
										Constant.SHRED_PR.KEY_USERID)));
				entity.addPart("income_bracket_id", new StringBody(""
						+ (selectedIncomePosition + 1)));
				entity.addPart(
						"music_type_id",
						new StringBody(""
								+ listMusic.get(selectedMusicTypePosition).get(
										"music_type_id")));
				entity.addPart("invitation_code", new StringBody(""
						+ editInviteCode.getText().toString().trim()));
				entity.addPart("affiliation", new StringBody(""
						+ editOccupation.getText().toString().trim()));
				entity.addPart("favorite_clubs", new StringBody(""
						+ editMostFrequentedClubs.getText().toString().trim()));
				entity.addPart("other_memberships", new StringBody(""
						+ editOtherClub.getText().toString().trim()));
				entity.addPart("device_type", new StringBody("android"));
				entity.addPart(
						"PHPSESSIONID",
						new StringBody(""
								+ UtilInList.ReadSharePrefrence(
										VipMemberShipActivity.this,
										Constant.SHRED_PR.KEY_SESSIONID)));

				// ************* Common Data: *****************//
				entity.addPart("common_appVersion", new StringBody(""
						+ UtilInList.getCommon_appVersion(context)));
				entity.addPart("common_deviceId", new StringBody(""
						+ UtilInList.getDeviceId(context)));
				try {
					entity.addPart("common_locationLatitude", new StringBody(""
							+ SplashScreenActivity.objSplash.gps.getLatitude()));
					entity.addPart(
							"common_locationLongitude",
							new StringBody(""
									+ SplashScreenActivity.objSplash.gps
											.getLongitude()));

				} catch (Exception e) {
					// TODO: handle exception
				}
				// ******************************************* //

				poster.setEntity(entity);
				response1 = client.execute(poster);

				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response1.getEntity().getContent()));
				String line = null;
				while ((line = rd.readLine()) != null) {
					response += line;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d("Resp Upload", "" + response);

			return response;
		}

		@Override
		protected void onPostExecute(String result1) {
			// TODO Auto-generated method stub
			super.onPostExecute(result1);

			try {
				dialog.dismiss();
			} catch (Exception e) {
				// TODO: handle exception
			}

			JSONObject result = null;
			try {
				result = new JSONObject(result1);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				if (result.getString("success").equals("true")) {

					if (UtilInList
							.ReadSharePrefrence(VipMemberShipActivity.this,
									Constant.SHRED_PR.KEY_ADDCARD_FROM)
							.toString().equals("1")) {

						if (UtilInList
								.ReadSharePrefrence(VipMemberShipActivity.this,
										Constant.SHRED_PR.KEY_USER_CARD_ADDED)
								.toString().equals("1")
								|| UtilInList
										.ReadSharePrefrence(
												VipMemberShipActivity.this,
												Constant.SHRED_PR.KEY_CARD_REQUIRED)
										.toString().equals("0")) {

							// Card already added or card not required:

							startActivity(new Intent(
									VipMemberShipActivity.this,
									CompletePurchaseActivity.class));
							overridePendingTransition(R.anim.enter_from_left,
									R.anim.hold_bottom);
							finish();

						} else {

							// Card required:

							UtilInList.WriteSharePrefrence(
									VipMemberShipActivity.this,
									Constant.SHRED_PR.KEY_ADDCARD_FROM, "1");
							startActivity(new Intent(
									VipMemberShipActivity.this,
									AddCardActivity.class));
							overridePendingTransition(R.anim.enter_from_bottom,
									R.anim.hold_bottom);
							finish();

						}

					} else {
						startActivity(new Intent(VipMemberShipActivity.this,
								VipMembershipReview.class));
						overridePendingTransition(R.anim.enter_from_left,
								R.anim.hold_bottom);
						finish();
					}

				} else {

					UtilInList.validateDialog(VipMemberShipActivity.this,
							result.getJSONArray("errors").getString(0),
							Constant.ERRORS.OOPS);

				}
			} catch (Exception e) {
				Log.v("", "Exception : " + e);
			}
		}

	}

	public void loginToFacebook() {

		mPrefs = getPreferences(MODE_PRIVATE);

		long expires = mPrefs.getLong("access_expires", 0);

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(VipMemberShipActivity.this, new String[] {
					"email", "publish_stream" }, new DialogListener() {

				@Override
				public void onCancel() {
					try {
						facebook.logout(VipMemberShipActivity.this);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onComplete(Bundle values) {
					// Function to handle complete event
					// Edit Preferences and update facebook acess_token
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString("access_token", facebook.getAccessToken());
					editor.putLong("access_expires",
							facebook.getAccessExpires());
					editor.commit();
					getProfileInformation();
				}

				@Override
				public void onError(DialogError error) {
				}

				@Override
				public void onFacebookError(FacebookError fberror) {
				}

			});

		} else {
			getProfileInformation();
		}
	}

	public void getProfileInformation() {

		mAsyncRunner.request("me", new RequestListener() {

			@Override
			public void onComplete(String response, Object state) {
				// TODO Auto-generated method stub
				try {
					final JSONObject jObj = new JSONObject(response);

					Log.v("", "chk facebook response : " + jObj.toString());

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								String fbId = jObj.getString("id");
								new SetImage(VipMemberShipActivity.this, fbId)
										.execute();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});

				} catch (Exception e) {

					Log.v("", "Exception : " + e);
				}

			}

			@Override
			public void onIOException(IOException e, Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
				// TODO Auto-generated method stub

			}

		});
	}

	class SetImage extends AsyncTask<Void, Bitmap, Bitmap> {

		private MyProgressbar dialog;
		Context context;
		String fbId;

		public SetImage(Context context, String fbId) {
			// TODO Auto-generated constructor stub
			dialog = new MyProgressbar(context);
			this.context = context;
			this.fbId = fbId;
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
		protected Bitmap doInBackground(Void... params) {
			// TODO Auto-generated method stub
			camera_pathname = "https://graph.facebook.com/" + fbId
					+ "/picture?type=large";
			flagFB = true;
			return UtilInList.getBitmapFromURL(camera_pathname);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				dialog.dismiss();
			} catch (Exception e) {
				// TODO: handle exception
			}

			bmp = result;
			imgProfile.setImageBitmap(bmp);

		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (UtilInList
				.ReadSharePrefrence(VipMemberShipActivity.this,
						Constant.SHRED_PR.KEY_ADDCARD_FROM).toString()
				.equals("1")) {
			finish();
			overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);

		} else {
			finish();
			overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
		}
	}

}
