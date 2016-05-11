package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class QuadInOutInterpolator implements Interpolator {

	public QuadInOutInterpolator() {}

    public QuadInOutInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		if((input /= 0.5f) < 1) {
			return 0.5f * input * input;
		}
		return -0.5f * ((--input) * (input - 2) - 1);
	}
}
