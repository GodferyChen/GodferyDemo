package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class QuadOutInterpolator implements Interpolator {

	public QuadOutInterpolator() {}

    public QuadOutInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		return -input * (input - 2);
	}
}
