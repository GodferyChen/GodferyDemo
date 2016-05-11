package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class CubicInInterpolator implements Interpolator {
	
    public CubicInInterpolator() {}
	
    public CubicInInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		return input * input * input;
	}
}
