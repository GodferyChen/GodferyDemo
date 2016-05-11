package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class SineInInterpolator implements Interpolator {

	public SineInInterpolator() {}

    public SineInInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		return (float) (-1 * Math.cos(input * MathConstants._HALF_PI) + 1);
	}
}
