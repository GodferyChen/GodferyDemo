package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class QuartInOutInterpolator implements Interpolator {

	public QuartInOutInterpolator() {}

    public QuartInOutInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		if((input /= 0.5f) < 1) {
			return 0.5f * input * input * input * input;
		}
		return -0.5f * ((input -= 2) * input * input * input - 2);
	}
}
