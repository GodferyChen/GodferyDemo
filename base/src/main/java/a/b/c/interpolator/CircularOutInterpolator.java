package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class CircularOutInterpolator implements Interpolator {
	
    public CircularOutInterpolator() {}
	
    public CircularOutInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		return (float) (Math.sqrt(1 - (input - 1) * (input - 1)));
	}
}
