package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class SineInOutInterpolator implements Interpolator {

	public SineInOutInterpolator() {}

    public SineInOutInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		return (float) (-1 * 0.5f * (Math.cos(MathConstants.PI * input) - 1));
	}
}
