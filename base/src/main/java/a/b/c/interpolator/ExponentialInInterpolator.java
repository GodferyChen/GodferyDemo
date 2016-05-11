package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class ExponentialInInterpolator implements Interpolator {
	
    public ExponentialInInterpolator() {}
	
    public ExponentialInInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		return (float) ((input == 0) ? 0 : 1 * Math.pow(2, 10 * (input - 1)) - 0.001f);
	}
}
