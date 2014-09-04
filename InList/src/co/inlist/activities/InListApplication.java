package co.inlist.activities;

import android.app.Application;
import co.inlist.util.Constant;

import com.parse.Parse;

public class InListApplication extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		try {
			Parse.initialize(this, Constant.YOUR_APP_ID,
					Constant.YOUR_CLIENT_KEY);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}
