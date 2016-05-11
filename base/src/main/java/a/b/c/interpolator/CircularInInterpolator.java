package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class CircularInInterpolator implements Interpolator {
	
    public CircularInInterpolator() {}
	
    public CircularInInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		return (float) (-1 * (Math.sqrt(1 - input * input) - 1.0f));
	}
}
