package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class BackOutInterpolator implements Interpolator {

    public BackOutInterpolator() {
    }

    public BackOutInterpolator(Context context, AttributeSet attrs) {
    }

    public float getInterpolation(float input) {
        return ((input = input - 1) * input * ((1.70158f + 1) * input + 1.70158f) + 1);
    }
}
