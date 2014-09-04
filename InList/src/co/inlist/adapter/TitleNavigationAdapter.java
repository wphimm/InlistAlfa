package co.inlist.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import co.inlist.activities.R;

public class TitleNavigationAdapter extends BaseAdapter {

	private TextView txtTitle;
	private ArrayList<HashMap<String, String>> spinnerNavItem;
	private Context context;

	public TitleNavigationAdapter(Context context,
			ArrayList<HashMap<String, String>> spinnerNavItem) {
		this.spinnerNavItem = spinnerNavItem;
		this.context = context;
	}

	@Override
	public int getCount() {
		return spinnerNavItem.size();
	}

	@Override
	public Object getItem(int index) {
		return spinnerNavItem.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(
					R.layout.list_item_title_navigation, null);
		}

		txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
		txtTitle.setText(spinnerNavItem.get(position).get("title").toString());

		if (spinnerNavItem.get(position).get("status").equals("0")) {
			convertView.setFocusable(false);
			convertView.setClickable(false);
			convertView.setAlpha(0.5f);
		} else {
			convertView.setFocusable(true);
			convertView.setClickable(true);
			convertView.setAlpha(1.0f);
		}

		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(
					R.layout.list_item_title_navigation, null);
		}

		txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
		txtTitle.setText(spinnerNavItem.get(position).get("title").toString());
		return convertView;
	}

}
