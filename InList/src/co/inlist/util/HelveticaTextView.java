package co.inlist.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class HelveticaTextView extends TextView {

	public HelveticaTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public HelveticaTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HelveticaTextView(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/helve_unbold.ttf");
			setTypeface(tf);
		}
	}
}
