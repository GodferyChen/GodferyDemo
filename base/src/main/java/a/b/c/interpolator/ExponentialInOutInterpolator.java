package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class ExponentialInOutInterpolator implements Interpolator {
	
    public ExponentialInOutInterpolator() {}
	
    public ExponentialInOutInterpolator(Context context, AttributeSet attrs) {}

	public float getInterpolation(float input) {
		if(input == 0) {
			return 0;
		}
		if(input == 1) {
			return 1;
		}
		if((input /= 0.5f) < 1) {
			return (float) (0.5f * Math.pow(2, 10 * (input - 1)));
		}
		return (float) (0.5f * (-Math.pow(2, -10 * --input) + 2));
	}
}
