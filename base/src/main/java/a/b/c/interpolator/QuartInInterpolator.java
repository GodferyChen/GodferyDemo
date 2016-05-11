package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class QuartInInterpolator implements Interpolator {

	public QuartInInterpolator() {}

    public QuartInInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		return  input * input * input * input;
	}
}
