package a.b.c.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class FitsLinearLayout extends LinearLayout {


	public FitsLinearLayout(Context context) {
		this(context, null);
	}

	public FitsLinearLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FitsLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected boolean fitSystemWindows(Rect insets) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			insets.left = 0;
			insets.top = 0;
			insets.right = 0;
		}
		return super.fitSystemWindows(insets);
	}
}
