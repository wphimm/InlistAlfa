package co.inlist.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.StickyListHeadersAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import co.inlist.activities.EventDetailsActivity;
import co.inlist.activities.HomeScreenActivity;
import co.inlist.activities.R;
import co.inlist.util.UtilInList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
public class EventsAdapter extends BaseAdapter implements
		StickyListHeadersAdapter, SectionIndexer {

	ArrayList<String> locallist = new ArrayList<String>();
	Context context;
	DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	Typeface typeAkzidgrobeligex, typeAkzidgrobemedex, typeAvenir,
			typeLeaguegothic_condensedregular;
	Activity objAct;

	private ArrayList<HashMap<String, String>> mSectionLetters;

	public EventsAdapter(ArrayList<String> list, Context context,
			Activity objAct) throws JSONException {
		// TODO Auto-generated constructor stub
		locallist = list;
		this.context = context;
		this.objAct = objAct;

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.no_image)
				.resetViewBeforeLoading(true)
				.showImageForEmptyUri(R.drawable.no_image)
				.showImageOnFail(R.drawable.no_image).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));

		typeAkzidgrobeligex = Typeface.createFromAsset(context.getAssets(),
				"akzidgrobeligex.ttf");
		typeAkzidgrobemedex = Typeface.createFromAsset(context.getAssets(),
				"helve_unbold.ttf");
		typeLeaguegothic_condensedregular = Typeface.createFromAsset(
				context.getAssets(), "leaguegothic_condensedregular.otf");
		typeAvenir = Typeface

		.createFromAsset(context.getAssets(), "avenir.ttc");

		mSectionLetters = getSectionLetters();
	}

	private ArrayList<HashMap<String, String>> getSectionLetters()
			throws JSONException {

		ArrayList<HashMap<String, String>> letters = new ArrayList<HashMap<String, String>>();
		for (int j = 0; j < locallist.size(); j++) {
			JSONObject jObj = new JSONObject(locallist.get(j));
			boolean flag = true;
			for (int i = 0; i < j; i++) {
				JSONObject iObj = new JSONObject(locallist.get(i));
				if (iObj.getString("event_start_date").equals(
						jObj.getString("event_start_date")))
					flag = false;
			}
			if (flag) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("id", "" + j);
				map.put("event_start_date",
						"" + jObj.getString("event_start_date"));
				letters.add(map);
			} else {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("id", "" + letters.get(letters.size() - 1).get("id"));
				map.put("event_start_date",
						"" + jObj.getString("event_start_date"));
				letters.add(map);
			}
		}

		Log.e("letters size:", "" + letters.size());
		return letters;
	}

	public void add(String map) throws JSONException {
		// TODO Auto-generated method stub
		locallist.add(map);
		mSectionLetters = getSectionLetters();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return locallist.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View rowView = convertView;

		if (rowView == null) {
			rowView = LayoutInflater.from(context).inflate(
					R.layout.events_list_row, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.txt_event_title = (TextView) rowView
					.findViewById(R.id.event_title);
			viewHolder.txt_event_location_city = (TextView) rowView
					.findViewById(R.id.event_location_city);
			viewHolder.img_event_poster_url = (ImageView) rowView
					.findViewById(R.id.img);
			viewHolder.relativeMain = (RelativeLayout) rowView
					.findViewById(R.id.main);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();
		JSONObject jObj;
		try {
			jObj = new JSONObject(locallist.get(position));

			holder.txt_event_title.setShadowLayer(2, 2, 0, Color.BLACK);
			holder.txt_event_title.setText(jObj.getString("event_title")
					.toString().toUpperCase());
			holder.txt_event_location_city.setText(""
					+ jObj.getString("event_location_club") + ", "
					+ jObj.getString("event_location_city"));

			holder.txt_event_title.setTypeface(typeAkzidgrobemedex);
			holder.txt_event_location_city.setTypeface(typeAkzidgrobemedex);

			String image_url = jObj.getString("event_poster_url");
			imageLoader.displayImage(image_url, holder.img_event_poster_url,
					options);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (position == (getCount() - 1)) {
			if (UtilInList.isInternetConnectionExist(context
					.getApplicationContext())) {
				HomeScreenActivity.flagReset = false;
				HomeScreenActivity.flagIfProgress = false;
				HomeScreenActivity.HomeScreenObj.new EventsAsyncTask(
						context.getApplicationContext()).execute("");
			}
		}

		rowView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(context, EventDetailsActivity.class);
				i.putExtra("resultlistEvents", ""+locallist.get(position));
				context.startActivity(i);
				objAct.overridePendingTransition(R.anim.enter_from_left,
						R.anim.hold_bottom);
			}
		});

		return rowView;
	}

	static class ViewHolder {
		ImageView img_event_poster_url;
		TextView txt_event_title, txt_event_location_city;
		RelativeLayout relativeMain;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;

		if (convertView == null) {
			holder = new HeaderViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.headerlayout, parent, false);
			holder.text_header = (TextView) convertView
					.findViewById(R.id.event_start_date);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}

		// ***** Date Format ************************************//
		String strDate = ""
				+ mSectionLetters.get(position).get("event_start_date");
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date date1;

		try {
			date1 = sdf.parse(strDate);

			SimpleDateFormat format = new SimpleDateFormat("d");
			String date = format.format(date1);

			if (date.endsWith("1") && !date.endsWith("11"))
				format = new SimpleDateFormat("EEEE, MMMM d'st'");
			else if (date.endsWith("2") && !date.endsWith("12"))
				format = new SimpleDateFormat("EEEE, MMMM d'nd'");
			else if (date.endsWith("3") && !date.endsWith("13"))
				format = new SimpleDateFormat("EEEE, MMMM d'rd'");
			else
				format = new SimpleDateFormat("EEEE, MMMM d'th'");

			strDate = format.format(date1);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		holder.text_header.setText("" + strDate.toUpperCase());

		// ***** Date Format ************************************//

		return convertView;
	}

	/**
	 * Remember that these have to be static, postion=1 should always return the
	 * same Id that is.
	 */
	@Override
	public long getHeaderId(int position) {
		// return the first character of the country as ID because this is what
		// headers are based upon
		return Long.parseLong(mSectionLetters.get(position).get("id"));
	}

	@Override
	public int getPositionForSection(int section) {
		return section;
	}

	@Override
	public int getSectionForPosition(int position) {
		return position;
	}

	@Override
	public String[] getSections() {
		String[] mHeaders = new String[mSectionLetters.size()];
		for (int i = 0; i < mSectionLetters.size(); i++) {
			mHeaders[i] = mSectionLetters.get(i).get("id");
		}
		Log.e("sections size:", "" + mHeaders.length);
		return mHeaders;
	}

	class HeaderViewHolder {
		TextView text_header;
	}
}
