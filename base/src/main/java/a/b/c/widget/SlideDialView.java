package a.b.c.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import a.b.c.R;

/**
 * 滑动表盘选择器
 * Created by chen on 2015/11/30.
 */
public class SlideDialView extends View {

    private final static int DURATION = 3200;

    private int width;
    private int radius;
    private int innerRadius;
    private int curValue;
    private int maxValue = 12 * 20;
    private int[] colors = new int[]{0x6F7C59C4, 0xAFFFC11B, 0x6F7C59C4};
    private float[] positions = new float[]{0.0f, 0.5f, 1.0F};
    private float dp1;
    private float sweepStartDegree;
    private float sweepDegree;
    private Bitmap curCat;
    private int catWidth;
    private int catHeight;
    private int flag;
    private RectF oval;
    private boolean enableSliding;
    private OnSlideSelectorListener onSlideSelectorListener;

    private Paint grayCirclePaint;
    private Paint lineCirclePaint;
    private Paint longScalePaint;
    private Paint shortScalePaint;
    private Paint progressPaint;
    private Paint numberPaint;
    private Paint dayCatPaint;


    public SlideDialView(Context context) {
        this(context, null, 0);
    }

    public SlideDialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideDialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        dp1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getContext().getResources().getDisplayMetrics());
        float sp1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1, getContext().getResources().getDisplayMetrics());
        innerRadius = (int) (dp1 * 80);

        curCat = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_sleep_morning_slide);
        float ratio = curCat.getWidth() * 1.0f / curCat.getHeight();
        catWidth = (int) (dp1 * 20);
        catHeight = (int) (catWidth / ratio);

        grayCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        grayCirclePaint.setStyle(Paint.Style.STROKE);
        grayCirclePaint.setColor(0x0F000000);
        grayCirclePaint.setStrokeWidth(dp1 * 16);

        lineCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lineCirclePaint.setStyle(Paint.Style.STROKE);
        lineCirclePaint.setColor(0xff24afee);
        lineCirclePaint.setStrokeWidth(dp1 * 6);

        longScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        longScalePaint.setColor(0xff24afee);
        longScalePaint.setStrokeCap(Paint.Cap.ROUND);
        longScalePaint.setStrokeWidth(dp1 * 3);

        shortScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shortScalePaint.setColor(0xff24afee);
        shortScalePaint.setStrokeCap(Paint.Cap.ROUND);
        shortScalePaint.setStrokeWidth(dp1 * 2);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(dp1 * 16);

        numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numberPaint.setColor(0xff24afee);
        numberPaint.setTextSize(sp1 * 32);

        Paint unitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unitPaint.setColor(0xff24afee);
        unitPaint.setTextSize(sp1 * 20);

        dayCatPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        radius = width / 2;
        oval = new RectF(radius - innerRadius + progressPaint.getStrokeWidth() / 2, radius - innerRadius + progressPaint.getStrokeWidth() / 2,
                radius + innerRadius - progressPaint.getStrokeWidth() / 2, radius + innerRadius - progressPaint.getStrokeWidth() / 2);
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 灰色透明的圆圈
        canvas.drawCircle(radius, radius, innerRadius - grayCirclePaint.getStrokeWidth() / 2, grayCirclePaint);

        // 外部细的圆圈
        canvas.drawCircle(radius, radius, innerRadius + lineCirclePaint.getStrokeWidth() / 2, lineCirclePaint);

        // 运动目标数量
        int intervalValue = (int) (sweepDegree / 360 * maxValue) + 10;
        float widthValue = numberPaint.measureText(String.valueOf(intervalValue * 100));
        canvas.drawText(String.valueOf(intervalValue * 100), width / 2 - widthValue / 2,
                width / 2 + (Math.abs(numberPaint.ascent()) - Math.abs(numberPaint.descent())) / 2, numberPaint);

        // 进度渐变圆弧
        canvas.rotate(-90, width / 2, width / 2);
        if(sweepDegree == 135) sweepDegree += 0.05;
        progressPaint.setShader(new SweepGradient(width / 2, width / 2, colors, positions));
        canvas.drawArc(oval, sweepStartDegree, sweepDegree, false, progressPaint);

        // 刻度
        canvas.rotate(90, radius, radius);
        canvas.drawLine(radius, radius - innerRadius, radius, radius - innerRadius + grayCirclePaint.getStrokeWidth() - dp1, longScalePaint);
        canvas.drawLine(radius, radius + innerRadius - grayCirclePaint.getStrokeWidth() + dp1, radius, radius + innerRadius, longScalePaint);
        canvas.drawLine(radius - innerRadius, radius, radius - innerRadius + grayCirclePaint.getStrokeWidth() - dp1, radius, longScalePaint);
        canvas.drawLine(radius + innerRadius - grayCirclePaint.getStrokeWidth() + dp1, radius, radius + innerRadius, radius, longScalePaint);
        for (int i = 30; i < 360; i += 30) {
            if (i % 90 != 0) {
                double radian = Math.toRadians(i);
                float x1 = (float) (radius + innerRadius * Math.cos(radian));
                float y1 = (float) (radius + innerRadius * Math.sin(radian));
                float tmpRadius = innerRadius - grayCirclePaint.getStrokeWidth() / 2 + dp1;
                float x2 = (float) (radius + tmpRadius * Math.cos(radian));
                float y2 = (float) (radius + tmpRadius * Math.sin(radian));
                canvas.drawLine(x1, y1, x2, y2, shortScalePaint);
            }
        }

        // 画猫
        canvas.rotate(sweepStartDegree, radius, radius);
        canvas.rotate(sweepDegree, radius, radius);
        canvas.drawBitmap(curCat, new Rect(0, 0, curCat.getWidth(), curCat.getHeight()),
                new Rect(radius - catWidth / 2, (int) (radius - innerRadius + progressPaint.getStrokeWidth() - catHeight),
                        radius + catWidth / 2, (int) (radius - innerRadius + progressPaint.getStrokeWidth())),
                dayCatPaint);
    }

    public void start(int value) {
        if (value > maxValue * 100 + 1000) return;

        value = value - 1000;
        ObjectAnimator sweepStartDegreeAnimator = ObjectAnimator.ofFloat(this, "SlideDialView", sweepStartDegree, 0)
                .setDuration((long) (DURATION * 1.0f * Math.abs(0 - sweepStartDegree) / (maxValue * 100)));
        sweepStartDegreeAnimator.setInterpolator(new DecelerateInterpolator());
        sweepStartDegreeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sweepStartDegree = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        float intervalEndDegree = (value >= 0 ? value : (value + maxValue * 100)) * 1.0f / (maxValue * 100) * 360;
        ObjectAnimator sweepDegreeAnimator = ObjectAnimator.ofFloat(this, "SlideDialView", sweepDegree, intervalEndDegree)
                .setDuration((long) (DURATION * 1.0f * Math.abs(intervalEndDegree - sweepDegree) / 360));
        sweepDegreeAnimator.setInterpolator(new DecelerateInterpolator());
        sweepDegreeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sweepDegree = (Float) animation.getAnimatedValue();
                int intervalValue = (int) (sweepDegree / 360 * maxValue * 100);
                curValue = (intervalValue > maxValue * 100) ? (intervalValue - maxValue) * 100 : intervalValue * 100;
                invalidate();
            }
        });

        sweepStartDegreeAnimator.start();
        sweepDegreeAnimator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (enableSliding) {
            float x = event.getX();
            float y = event.getY();

            float slideRadius = innerRadius + lineCirclePaint.getStrokeWidth() + dp1 * 24;
            double radian1 = Math.toRadians(sweepDegree + sweepStartDegree - 90);
            float x1 = (float) (radius + slideRadius * Math.cos(radian1));
            float y1 = (float) (radius + slideRadius * Math.sin(radian1));
            double radian2 = Math.toRadians(sweepStartDegree - 90);
            float x2 = (float) (radius + slideRadius * Math.cos(radian2));
            float y2 = (float) (radius + slideRadius * Math.sin(radian2));

            if (Math.pow(x - x1, 2) + Math.pow(y - y1, 2) <= Math.pow(dp1 * 24, 2) || flag == 1) {
                getParent().requestDisallowInterceptTouchEvent(true);
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                    flag = 1;
                    float deg = (float) Math.abs(Math.toDegrees(Math.atan2(x - radius, y - radius)) - 180);
                    if (Math.abs(sweepDegree - deg) >= 0.3f) {
                        if (deg > sweepStartDegree) {
                            sweepDegree = Math.abs(deg - sweepStartDegree);
                        } else {
                            sweepDegree = Math.abs(360 - sweepStartDegree) + (deg <= sweepStartDegree ? Math.abs(deg) : 0);
                        }
                        if (sweepDegree > 360) {
                            sweepDegree = 360;
                        } else if (sweepDegree < 0) {
                            sweepDegree = 0;
                        }
                        if (onSlideSelectorListener != null) {
                            int intervalValue = (int) (sweepDegree / 360 * maxValue);
                            curValue = (((intervalValue) > maxValue) ? (intervalValue - maxValue) * 100 : intervalValue * 100) + 1000;
                            onSlideSelectorListener.onSlideSelector(curValue);
                        }
                        invalidate();
                        return true;
                    }
                } else if (action == MotionEvent.ACTION_UP) {
                    flag = 0;
                }
            } else if (Math.pow(x - x2, 2) + Math.pow(y - y2, 2) <= Math.pow(dp1 * 24, 2) || flag == 2) {
                getParent().requestDisallowInterceptTouchEvent(true);
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                    flag = 2;
                    float deg = (float) Math.abs(Math.toDegrees(Math.atan2(x - radius, y - radius)) - 180);
                    //此为起点滑动的代码，暂不需要两头都滑动
//                    if (Math.abs(sweepStartDegree - deg) >= 0.3f) {
//                        sweepDegree += sweepStartDegree - deg;
//                        if (sweepDegree > 360) {
//                            sweepDegree -= 360;
//                        } else if (sweepDegree<0) {
//                            sweepDegree += 360;
//                        }
//                        sweepStartDegree = deg;
//                        if (sweepStartDegree > 360) {
//                            sweepStartDegree = 360;
//                        } else if (sweepStartDegree < 0) {
//                            sweepStartDegree = 0;
//                        }
//                        invalidate();
//                        return true;
//                    }
                } else if (action == MotionEvent.ACTION_UP) {
                    flag = 0;
                }
            }
        }
        getParent().requestDisallowInterceptTouchEvent(false);
        return super.onTouchEvent(event);
    }

    public void setOnSlideSelectorListener(OnSlideSelectorListener listener) {
        this.onSlideSelectorListener = listener;
    }

    public void enableSliding(boolean enableSliding) {
        this.enableSliding = enableSliding;
    }

    public interface OnSlideSelectorListener {
        void onSlideSelector(int value);
    }
}
