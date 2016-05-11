package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class QuintOutInterpolator implements Interpolator {

	public QuintOutInterpolator() {}

    public QuintOutInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		return (input - 1) * (input - 1) * (input - 1) * (input - 1) * (input - 1) + 1;
	}
}
