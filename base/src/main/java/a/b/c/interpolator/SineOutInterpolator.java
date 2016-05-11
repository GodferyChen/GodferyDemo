package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class SineOutInterpolator implements Interpolator {

	public SineOutInterpolator() {}

    public SineOutInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		return (float) Math.sin(input * MathConstants._HALF_PI);
	}
}
