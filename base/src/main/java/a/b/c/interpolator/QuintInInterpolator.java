package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class QuintInInterpolator implements Interpolator {

	public QuintInInterpolator() {}

    public QuintInInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		return input * input * input * input * input;
	}
}
