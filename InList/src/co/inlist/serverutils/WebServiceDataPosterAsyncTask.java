package co.inlist.serverutils;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import co.inlist.interfaces.AsyncTaskCompleteListener;
import co.inlist.util.Constant;
import co.inlist.util.MyProgressbar;
import co.inlist.util.UtilInList;

public class WebServiceDataPosterAsyncTask extends
		AsyncTask<String, String, String> {

	private MyProgressbar dialog;
	private List<NameValuePair> nameValuePair;
	private String URL;
	public AsyncTaskCompleteListener callback;
	Context context;

	public WebServiceDataPosterAsyncTask(Context context,
			List<NameValuePair> nameValuePair, String URL) {
		dialog = new MyProgressbar(context);
		this.callback = (AsyncTaskCompleteListener) context;
		this.nameValuePair = nameValuePair;
		this.URL = URL;
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
		Log.v("", "Chk URL : " + URL + nameValuePair);
		String response = UtilInList.postData(context, nameValuePair, URL);
		Log.v("Response In Activity-->", response);

		return response;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		try {
			dialog.dismiss();
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			JSONObject jObj = new JSONObject(result);
			UtilInList.WriteSharePrefrence(context,
					Constant.SHRED_PR.KEY_SESSIONID,
					jObj.getJSONObject("session").getJSONObject("userInfo")
							.getString("sessionId"));
			UtilInList.WriteSharePrefrence(context,
					Constant.SHRED_PR.KEY_VIP_STATUS,
					jObj.getJSONObject("session").getJSONObject("userInfo")
							.getString("vip_status"));

		} catch (Exception e) {
			// TODO: handle exception
		}
		callback.onTaskComplete(result);

	}

}