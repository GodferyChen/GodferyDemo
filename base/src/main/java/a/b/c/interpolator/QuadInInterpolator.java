package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class QuadInInterpolator implements Interpolator {

	public QuadInInterpolator() {}

    public QuadInInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		return input * input;
	}
}
