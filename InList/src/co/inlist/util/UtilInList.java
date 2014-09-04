package co.inlist.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import co.inlist.activities.R;
import co.inlist.activities.SplashScreenActivity;

@SuppressLint("SimpleDateFormat")
public class UtilInList {

	public static void writeToFile(String data, String file_name,
			Context context) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					context.openFileOutput(file_name, Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	public static void makeActionBarGradiant(Context context) {
		// get the action bar
		ActionBar actionBar = ((Activity) context).getActionBar();

		// Enabling Back navigation on Action Bar icon
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.actionbar_gradiant));
	}

	public static void makeActionBarFullBlack(Context context) {
		// get the action bar
		ActionBar actionBar = ((Activity) context).getActionBar();

		// Enabling Back navigation on Action Bar icon
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.actionbar_full_black));
	}

	public static String readFromFile(String file_name, Context context) {

		String ret = "";

		try {
			InputStream inputStream = context.openFileInput(file_name);

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}

		return ret;
	}

	public static String getDeviceId(Context mContext) {
		return Secure.getString(mContext.getContentResolver(),
				Secure.ANDROID_ID);
	}

	public static String getCommon_appVersion(Context mContext) {
		String common_appVersion = "android/"
				+ android.os.Build.VERSION.RELEASE + "/1.0.0";
		Log.e("common_appVersion", "" + common_appVersion);
		return common_appVersion;
	}

	public static void makeTextViewResizable(final TextView tv,
			final int maxLine, final String expandText, final boolean viewMore) {

		if (tv.getTag() == null) {
			tv.setTag(tv.getText());
		}
		ViewTreeObserver vto = tv.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressWarnings("deprecation")
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onGlobalLayout() {

				try {
					ViewTreeObserver obs = tv.getViewTreeObserver();
					// obs.removeGlobalOnLayoutListener(this);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						obs.removeOnGlobalLayoutListener(this);
					} else {
						obs.removeGlobalOnLayoutListener(this);
					}

					String temp;
					if (viewMore) {
						temp = "... " + expandText;
					} else {
						temp = " " + expandText;
					}

					if (maxLine == 0) {
						int lineEndIndex = tv.getLayout().getLineEnd(0);

						String text = tv.getText().subSequence(0,
								lineEndIndex - temp.length() + 1)
								+ temp;

						tv.setText(text);
						tv.setMovementMethod(LinkMovementMethod.getInstance());
						tv.setText(
								addClickablePartTextViewResizable(
										Html.fromHtml(text), tv, maxLine,
										expandText, viewMore),
								BufferType.SPANNABLE);
					} else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
						int lineEndIndex = tv.getLayout().getLineEnd(
								maxLine - 1);

						String text = tv.getText().subSequence(0,
								lineEndIndex - temp.length() + 1)
								+ temp;

						tv.setText(text);
						tv.setMovementMethod(LinkMovementMethod.getInstance());
						tv.setText(
								addClickablePartTextViewResizable(
										Html.fromHtml(text), tv, maxLine,
										expandText, viewMore),
								BufferType.SPANNABLE);
					} else {
						int lineEndIndex = tv.getLayout().getLineEnd(
								tv.getLayout().getLineCount() - 1);

						String text = tv.getText().subSequence(0, lineEndIndex)
								+ temp;

						tv.setText(text);
						tv.setMovementMethod(LinkMovementMethod.getInstance());
						tv.setText(
								addClickablePartTextViewResizable(
										Html.fromHtml(text), tv, lineEndIndex,
										expandText, viewMore),
								BufferType.SPANNABLE);

					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		});

	}

	private static SpannableStringBuilder addClickablePartTextViewResizable(
			final Spanned strSpanned, final TextView tv, final int maxLine,
			final String spanableText, final boolean viewMore) {

		String str = strSpanned.toString();
		SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

		if (str.contains(spanableText)) {
			ssb.setSpan(new ClickableSpan() {

				@Override
				public void onClick(View widget) {

					if (viewMore) {
						tv.setLayoutParams(tv.getLayoutParams());
						tv.setText(tv.getTag().toString(), BufferType.SPANNABLE);
						tv.invalidate();
						makeTextViewResizable(tv, 100, "LESS", false);
					} else {
						tv.setLayoutParams(tv.getLayoutParams());
						tv.setText(tv.getTag().toString(), BufferType.SPANNABLE);
						tv.invalidate();
						makeTextViewResizable(tv, 3, "MORE", true);
					}

				}
			}, str.indexOf(spanableText), str.indexOf(spanableText)
					+ spanableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			ssb.setSpan(new NonUnderlinedClickableSpan(),
					str.indexOf(spanableText), str.indexOf(spanableText)
							+ spanableText.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		}
		return ssb;

	}

	public static class NonUnderlinedClickableSpan extends ClickableSpan {

		@Override
		public void updateDrawState(TextPaint ds) {

			ds.setColor(Color.parseColor("#DFBB6A"));
			ds.setUnderlineText(false);
			ds.setTypeface(Typeface.DEFAULT_BOLD);
		}

		@Override
		public void onClick(View v) {

		}
	}

	public static String getAddresFromLatLong(Context context, double latitude,
			double longitude) {
		String final_address = "";

		try {

			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(context, Locale.getDefault());
			addresses = geocoder.getFromLocation(latitude, longitude, 1);

			String address = addresses.get(0).getAddressLine(0);
			String city = addresses.get(0).getAddressLine(1);
			String country = addresses.get(0).getAddressLine(2);

			final_address = address + city + country;
		} catch (Exception e) {

		}
		return final_address;
	}

	public static void savePicture(Bitmap bm, String imgName, Context c) {
		OutputStream fOut = null;
		String strDirectory = Environment.getExternalStorageDirectory()
				.toString();

		File f = new File(strDirectory + Constant.PREF_VAL.DIR_NAME, imgName
				+ ".jpg");
		try {
			fOut = new FileOutputStream(f);

			/** Compress image **/
			bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();

			/** Update image to gallery **/
			MediaStore.Images.Media.insertImage(c.getContentResolver(),
					f.getAbsolutePath(), f.getName(), f.getName());
		} catch (Exception e) {
			Log.v("", "Exception to storing image in sdcard : " + e);
		}
	}

	public static void mkDirSdcard() {
		File folder = new File(Environment.getExternalStorageDirectory()
				+ Constant.PREF_VAL.DIR_NAME);
		boolean success = true;
		if (!folder.exists()) {
			success = folder.mkdir();
		}
		if (success) {
			// Do something on success
			Log.v("", ">>>>>>>>>>Dir created successfully ......");
		} else {
			// Do something else on failure
			Log.v("", ">>>>>>>>>>Dir created cant made ......");
		}
	}

	public static boolean validateEmail(String emailId) {
		if (emailId == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(emailId)
					.matches();
		}
	}

	@SuppressWarnings("unused")
	public static String getDateFormatter(String UTCTime) {

		String local_date_time = "";
		String strTime = "";
		String strDate = "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date date = formatter.parse(UTCTime);

			SimpleDateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat formatter_time = new SimpleDateFormat("hh:mm a");

			strDate = formatter_date.format(date);
			strTime = formatter_time.format(date);

			local_date_time = strDate + " " + strTime;
		} catch (Exception e) {
			Log.v("", "Exception to formatting date : " + e);
		}

		return strDate;
	}

	public static String getDateTimeFormatter(String UTCTime) {

		String local_date_time = "";
		String strTime = "";
		String strDate = "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

			Date date = formatter.parse(UTCTime);
			formatter.setTimeZone(TimeZone.getDefault());

			SimpleDateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat formatter_time = new SimpleDateFormat("hh:mm a");

			strDate = formatter_date.format(date);
			strTime = formatter_time.format(date);

			local_date_time = strDate + " " + strTime;
		} catch (Exception e) {
			Log.v("", "Exception to formatting date : " + e);
		}

		return local_date_time;
	}

	public static String getTimeFormatter(String UTCTime) {

		@SuppressWarnings("unused")
		String local_date_time = "";
		String strTime = "";
		String strDate = "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date date = formatter.parse(UTCTime);

			SimpleDateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat formatter_time = new SimpleDateFormat("hh:mm a");

			strDate = formatter_date.format(date);
			strTime = formatter_time.format(date);

			local_date_time = strDate + " " + strTime;
		} catch (Exception e) {
			Log.v("", "Exception to formatting date : " + e);
		}

		return strTime;
	}

	public static Bitmap getCircleBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffff0000;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawOval(rectF, paint);

		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((float) 4);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff4242DB;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = bitmap.getWidth() / 2;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		// canvas.drawCircle(0, 0, bitmap.getWidth(), paint);
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static void validateDialog(Context context, String errorMsg,
			String title) {

		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("" + title);
		alert.setMessage("" + errorMsg);
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alert.create();
		alert.show();
	}

	public static Typeface setHelveticatype(Context context) {
		return Typeface.createFromAsset(context.getAssets(),
				"fonts/helvetica_fnt.ttf");
	}

	private static ConnectionDetector cd;
	private static Boolean isInternetPresent = false;

	@SuppressLint("SdCardPath")
	public static boolean ifConditionDataExist(Context c) {
		File f = new File("/data/data/" + c.getPackageName()
				+ "/shared_prefs/inlist_preferences.xml");

		if (f.exists()) {
			return true;

		} else {
			return false;
		}
	}

	public static boolean isInternetConnectionExist(Context c) {
		cd = new ConnectionDetector(c.getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();
		if (isInternetPresent) {
			return true;
		} else {
			return false;
		}
	}

	public static void WriteSharePrefrence(Context context, String key,
			String values) {
		@SuppressWarnings("static-access")
		SharedPreferences write_Data = context.getSharedPreferences(
				Constant.SHRED_PR.SHARE_PREF, context.MODE_PRIVATE);
		Editor editor = write_Data.edit();
		editor.putString(key, values);
		editor.commit();
	}

	public static String ReadSharePrefrence(Context context, String key) {
		@SuppressWarnings("static-access")
		SharedPreferences read_data = context.getSharedPreferences(
				Constant.SHRED_PR.SHARE_PREF, context.MODE_PRIVATE);

		return read_data.getString(key, Constant.BLANK);
	}

	public static String postData(Context context,
			List<NameValuePair> nameValuePairs, String url) {
		// TODO Auto-generated method stub
		String responseStr = "";
		HttpClient httpclient = getNewHttpClient();
		HttpPost httpPost = new HttpPost(url);

		// ************* Common Data: *****************//
		nameValuePairs.add(new BasicNameValuePair("common_appVersion", ""
				+ UtilInList.getCommon_appVersion(context)));
		nameValuePairs.add(new BasicNameValuePair("common_deviceId", ""
				+ UtilInList.getDeviceId(context)));
		try {
			nameValuePairs
					.add(new BasicNameValuePair("common_locationLatitude", ""
							+ SplashScreenActivity.objSplash.gps.getLatitude()));
			nameValuePairs
					.add(new BasicNameValuePair("common_locationLongitude", ""
							+ SplashScreenActivity.objSplash.gps.getLongitude()));

		} catch (Exception e) {
			// TODO: handle exception
		}
		// ******************************************* //

		Log.e("reqURL", "" + url);
		Log.e("nameValuePairs", "" + nameValuePairs);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				responseStr = EntityUtils.toString(resEntity).trim();
			}
			Log.e("Response-->", responseStr);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseStr;
	};

	public static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public String prepareWebserviceRequest(String[] keys, String[] values)
			throws JSONException {

		String retStr = null;
		JSONObject json = new JSONObject();
		for (int i = 0; i < keys.length; i++) {
			json.put(keys[i], values[i]);
		}
		retStr = json.toString();

		return retStr;
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
