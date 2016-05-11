package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class QuartOutInterpolator implements Interpolator {

	public QuartOutInterpolator() {}

    public QuartOutInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		return 1 - (input - 1) * (input - 1) * (input - 1) * (input - 1);
	}
}
