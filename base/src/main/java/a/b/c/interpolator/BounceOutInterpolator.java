package a.b.c.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class BounceOutInterpolator implements Interpolator {

    public BounceOutInterpolator() {
    }

    public BounceOutInterpolator(Context context, AttributeSet attrs) {
    }

    public float getInterpolation(float input) {
        if (input < (1 / 2.75))
            return (7.5625f * input * input);
        else if (input < (2 / 2.75))
            return (7.5625f * (input -= (1.5f / 2.75f)) * input + 0.75f);
        else if (input < (2.5 / 2.75))
            return (7.5625f * (input -= (2.25f / 2.75f)) * input + 0.9375f);
        else
            return (7.5625f * (input -= (2.625f / 2.75f)) * input + 0.984375f);
    }
}