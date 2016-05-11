package a.b.c.widget.step;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import a.b.c.R;

public class StepView extends LinearLayout implements StepViewIndicator.OnDrawListener {

    private StepViewIndicator mStepViewIndicator;
    private FrameLayout mLabelsLayout;
    private String[] mLabels;
    private int mProgressColorIndicator;
    private int mLabelColorIndicator = 0xFF333333;
    private int mBarColorIndicator = 0xFFBBBBBB;
    private int mCompletedPosition = -1;

    public StepView(Context context) {
        this(context, null);
    }

    public StepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public StepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.layout_step, this);
        mStepViewIndicator = (StepViewIndicator) rootView.findViewById(R.id.step_indicator_view);
        mStepViewIndicator.setDrawListener(this);
        mLabelsLayout = (FrameLayout) rootView.findViewById(R.id.labels_container);
        int[] attrs = {R.attr.colorPrimaryDark};
        TypedArray array = getContext().obtainStyledAttributes(attrs);
        int colorPrimary = array.getColor(0, 0xFF000000);
        array.recycle();
        mProgressColorIndicator = colorPrimary;
    }

    public String[] getLabels() {
        return mLabels;
    }

    public StepView setLabels(String[] labels) {
        mLabels = labels;
        mStepViewIndicator.setStepSize(labels.length);
        return this;
    }

    public int getProgressColorIndicator() {
        return mProgressColorIndicator;
    }

    public StepView setProgressColorIndicator(int progressColorIndicator) {
        mProgressColorIndicator = progressColorIndicator;
        mStepViewIndicator.setProgressColor(mProgressColorIndicator);
        return this;
    }

    public int getLabelColorIndicator() {
        return mLabelColorIndicator;
    }

    public StepView setLabelColorIndicator(int labelColorIndicator) {
        mLabelColorIndicator = labelColorIndicator;
        return this;
    }

    public int getBarColorIndicator() {
        return mBarColorIndicator;
    }

    public StepView setBarColorIndicator(int barColorIndicator) {
        mBarColorIndicator = barColorIndicator;
        mStepViewIndicator.setBarColor(mBarColorIndicator);
        return this;
    }

    public int getCompletedPosition() {
        return mCompletedPosition;
    }

    public StepView setCompletedPosition(int completedPosition) {
        mCompletedPosition = completedPosition;
        mStepViewIndicator.setCompletedPosition(mCompletedPosition);
        return this;
    }

    public void drawView() {
        if (mLabels == null) {
            throw new IllegalArgumentException("labels must not be null.");
        }

        if (mCompletedPosition > mLabels.length - 1) {
            throw new IndexOutOfBoundsException(String.format("Index : %s, Size : %s", mCompletedPosition, mLabels.length));
        }

        mStepViewIndicator.invalidate();
    }

    @Override
    public void onReady() {
        drawLabels();
    }

    private void drawLabels() {
        List<Float> indicatorPosition = mStepViewIndicator.getThumbContainerXPosition();

        if (mLabels != null) {
            for (int i = 0; i < mLabels.length; i++) {
                TextView textView = new TextView(getContext());
                textView.setText(mLabels[i]);
                textView.setTextColor(mLabelColorIndicator);
                float dp16 = TypedValue.applyDimension(1, 16, getResources().getDisplayMetrics());
                textView.setX(dp16+indicatorPosition.get(i)-textView.getPaint().measureText(mLabels[i])/2);
                textView.setLayoutParams(
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));

//                if (i <= mCompletedPosition) {
//                    textView.setTypeface(null, Typeface.BOLD);
//                }

                mLabelsLayout.addView(textView);
            }
        }
    }
}
