package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class ExponentialOutInterpolator implements Interpolator {
	
    public ExponentialOutInterpolator() {}
	
    public ExponentialOutInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		return (float) ((input == 1) ? 1 : (-Math.pow(2, -10 * input) + 1));
	}
}
