package co.inlist.activities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import twitter4j.StatusUpdate;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import co.inlist.facebook.android.DialogError;
import co.inlist.facebook.android.Facebook;
import co.inlist.facebook.android.Facebook.DialogListener;
import co.inlist.facebook.android.FacebookError;
import co.inlist.twitter.android.TwitterApp;
import co.inlist.twitter.android.TwitterApp.TwDialogListener;
import co.inlist.util.Constant;
import co.inlist.util.UtilInList;

public class SharingActivity extends Activity {

	EditText editShare;
	Button btnClose, btnShare;
	String sharingString = "", shareURL = "";
	int flagFB;
	Context context = this;

	// FaceBook
	private static String APP_ID = Constant.FB_API_KEY;
	// Instance of Facebook Class
	private Facebook facebook = new Facebook(APP_ID);
	String FILENAME = "AndroidSSO_data";
	private SharedPreferences mPrefs;
	// Twitter
	private TwitterApp mTwitter;
	// Client keys
	private static final String CONSUMER_KEY = Constant.CONSUMER_KEY;
	private static final String CONSUMER_SECRET = Constant.CONSUMER_SECRET;

	private enum FROM {
		TWITTER_POST, TWITTER_LOGIN
	};

	private enum MESSAGE {
		SUCCESS, DUPLICATE, FAILED, CANCELLED
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sharing);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			flagFB = extras.getInt("flagFB");
			sharingString = extras.getString("sharingString");
			shareURL = extras.getString("shareURL");
		}

		// Twitter
		mTwitter = new TwitterApp(SharingActivity.this, CONSUMER_KEY,
				CONSUMER_SECRET);

		btnClose = (Button) findViewById(R.id.btnClose);
		btnShare = (Button) findViewById(R.id.btnShare);
		editShare = (EditText) findViewById(R.id.editShare);
		editShare.setText("" + sharingString);

		btnShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sharingString = editShare.getText().toString().trim();
				if (UtilInList
						.isInternetConnectionExist(getApplicationContext())) {
					if (flagFB == 0) {
						loginToFacebook();
					} else {
						loginToTwitter();
					}
				} else {
					UtilInList.validateDialog(SharingActivity.this, "" + ""
							+ Constant.network_error, Constant.ERRORS.OOPS);
				}
			}
		});

		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editShare.getWindowToken(), 0);

				finish();
				overridePendingTransition(R.anim.hold_top,
						R.anim.exit_in_bottom);
			}
		});

	}

	public void loginToFacebook() {

		mPrefs = PreferenceManager
				.getDefaultSharedPreferences(SharingActivity.this);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);

		if (access_token != null) {
			facebook.setAccessToken(access_token);
			Log.d("access_token", "here..");
			postOnWall();
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(SharingActivity.this, new String[] { "email",
					"publish_stream", "status_update", "publish_actions" },
					new DialogListener() {

						public void onCancel() {
							// Function to handle cancel event

						}

						public void onComplete(Bundle values) {
							// Function to handle complete event
							// Edit Preferences and update facebook acess_token
							SharedPreferences.Editor editor = mPrefs.edit();
							editor.putString("access_token",
									facebook.getAccessToken());
							editor.putLong("access_expires",
									facebook.getAccessExpires());
							editor.commit();
							Log.d("complete", "here..");
							postOnWall();

						}

						public void onError(DialogError error) {
							// Function to handle error
							/*
							 * if (progressDialog.isShowing()) {
							 * progressDialog.dismiss(); }
							 */
						}

						public void onFacebookError(FacebookError fberror) {
							// Function to handle Facebook errors
							/*
							 * if (progressDialog.isShowing()) {
							 * progressDialog.dismiss(); }
							 */
						}

					});
		}
	}

	public void postOnWall() {
		new post_fb().execute();
	}

	class post_fb extends AsyncTask<String, String, String> {

		@Override
		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			if (response == null || response.equals("")
					|| response.equals("false")) {
				Log.v("Error", "Blank response");
				UtilInList
						.validateDialog(
								SharingActivity.this,
								"There is some server issue to share App in your Facebook account.",
								Constant.ERRORS.OOPS);

			} else {
				AlertDialog.Builder alert = new AlertDialog.Builder(context);
				alert.setTitle(Constant.AppName);
				alert.setMessage("Event share successfully on your Facebook account.");
				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Handler hn = new Handler();
								hn.postDelayed(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										finish();
										overridePendingTransition(
												R.anim.hold_top,
												R.anim.exit_in_bottom);
									}
								}, 200);
							}
						});
				alert.create();
				alert.show();
			}

		}

		@Override
		protected String doInBackground(String... params) {
			Log.d("Tests", "Testing graph API wall post");
			String response = null;
			try {

				StringBuffer msg = new StringBuffer("");
				msg.append("" + sharingString);

				response = facebook.request("me");

				Bundle parameters = new Bundle();
				parameters.putString("message", msg.toString());
				parameters.putString("picture", "" + shareURL);
				response = facebook.request("me/feed", parameters, "POST");
				Log.d("Tests", "got response: " + response);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	private void loginToTwitter() {
		// TODO Auto-generated method stub
		mTwitter.setListener(mTwLoginDialogListener);
		mTwitter.resetAccessToken();
		mTwitter.authorize();
	}

	private void postAsToast(FROM twitterPost, MESSAGE success) {
		switch (twitterPost) {
		case TWITTER_LOGIN:
			switch (success) {
			case SUCCESS:
				UtilInList.validateDialog(SharingActivity.this,
						"Login Successful", Constant.AppName);
				break;
			case FAILED:
				UtilInList.validateDialog(SharingActivity.this, "Login Failed",
						Constant.ERRORS.OOPS);
			default:
				break;
			}
			break;
		case TWITTER_POST:
			switch (success) {
			case SUCCESS:
				AlertDialog.Builder alert = new AlertDialog.Builder(context);
				alert.setTitle(Constant.AppName);
				alert.setMessage("Event share successfully on your Twitter account.");
				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Handler hn = new Handler();
								hn.postDelayed(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										finish();
										overridePendingTransition(
												R.anim.hold_top,
												R.anim.exit_in_bottom);
									}
								}, 200);
							}
						});
				alert.create();
				alert.show();
				break;
			case FAILED:
				UtilInList.validateDialog(SharingActivity.this,
						"Posting Failed", Constant.ERRORS.OOPS);
				break;
			case DUPLICATE:
				UtilInList.validateDialog(SharingActivity.this,
						"Posting Failed because of duplicate message.",
						Constant.ERRORS.OOPS);
			default:
				break;
			}
			break;
		}
	}

	private TwDialogListener mTwLoginDialogListener = new TwDialogListener() {

		public void onError(String value) {

			postAsToast(FROM.TWITTER_LOGIN, MESSAGE.FAILED);
			Log.e("TWITTER", value);
			mTwitter.resetAccessToken();

		}

		public void onComplete(String value) {

			new Post_Twitter().execute();

		}
	};

	class Post_Twitter extends AsyncTask<String, String, String> {

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			postAsToast(FROM.TWITTER_POST, MESSAGE.SUCCESS);
			mTwitter.resetAccessToken();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String post = "" + sharingString;
			if (post.length() > 100) {
				post = post.substring(0, Math.min(post.length(), 97)) + "...";
			}
			Log.e("post", ">>" + post + "\n" + post.length());

			Bitmap bmp = UtilInList.getBitmapFromURL("" + shareURL);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
			StatusUpdate status = new StatusUpdate(post);
			status.setMedia("event", bis);

			try {
				mTwitter.updateStatus(status);
				Log.d("twitter post..", "success");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
	}

}
