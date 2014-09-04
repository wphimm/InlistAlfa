package co.inlist.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import co.inlist.activities.R;

public class LeadingFragment extends Fragment {
	// Store instance variables
	@SuppressWarnings("unused")
	private String title;
	private int page;
	Typeface typeAkzidgrobeligex, typeAvenir,
			typeLeaguegothic_condensedregular;

	// newInstance constructor for creating fragment with arguments
	public static LeadingFragment newInstance(int page, String title) {
		LeadingFragment fragmentFirst = new LeadingFragment();
		Bundle args = new Bundle();
		args.putInt("someInt", page);
		args.putString("someTitle", title);
		fragmentFirst.setArguments(args);
		return fragmentFirst;
	}

	// Inflate the view for the fragment based on layout XML
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.leading_fragment, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		page = getArguments().getInt("someInt", 0);
		title = getArguments().getString("someTitle");

		typeAkzidgrobeligex = Typeface.createFromAsset(getActivity()
				.getAssets(), "akzidgrobeligex.ttf");
		typeLeaguegothic_condensedregular = Typeface.createFromAsset(
				getActivity().getAssets(), "leaguegothic_condensedregular.otf");
		typeAvenir = Typeface.createFromAsset(getActivity().getAssets(),
				"avenir.ttc");

		TextView tvLabel = (TextView) getView().findViewById(R.id.txt);
		ImageView img = (ImageView) getView().findViewById(R.id.img);
		tvLabel.setTypeface(typeAvenir);

		switch (page) {
		case 0:
			img.setBackgroundResource(R.drawable.splash_logo);
			tvLabel.setText("");
			break;
		case 1:
			img.setBackgroundResource(R.drawable.onboarding_image1);
			tvLabel.setText(Html
					.fromHtml("<p><h3><font color=\"#ffffff\">InList is your key to the </font>"
							+ "<font color=\"#DFBB6A\">most prestigious events</font>"
							+ "<font color=\"#ffffff\"> in your city.</font></h3></p>"));
			break;
		case 2:
			img.setBackgroundResource(R.drawable.onboarding_image2);
			tvLabel.setText(Html
					.fromHtml("<p><h3><font color=\"#ffffff\">Find the </font>"
							+ "<font color=\"#DFBB6A\">best events</font>"
							+ "<font color=\"#ffffff\">, and book your reservation, right from your phone.</font></h3></p>"));
			break;
		case 3:
			img.setBackgroundResource(R.drawable.onboarding_image3);
			tvLabel.setText(Html
					.fromHtml("<p><h3><font color=\"#ffffff\">Made by nightlife, for </font>"
							+ "<font color=\"#DFBB6A\">nightlife</font>"
							+ "<font color=\"#ffffff\">.</font><h3></p>"));
			break;

		default:
			break;
		}
	}
}
