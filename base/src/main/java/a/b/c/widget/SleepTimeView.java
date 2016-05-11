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

public class SleepTimeView extends View {

	private final static int DURATION = 3200;
	private static final String TAG = "SleepTimeView";

	private int width;
	private int radius;
	private int innerRadius;
	private int preNightValue;
	private int nightValue;
	private int preDayValue;
	private int dayValue;
	private int maxValue = 12 * 60;
	private int[] colors = new int[]{0x6F7C59C4, 0xAFFFC11B, 0x6F7C59C4};
	private float[] positions = new float[]{0.0f, 0.5f, 1.0F};
	private float dp1;
	private float sp1;
	private float sweepStartDegree;
	private float sweepDegree;
	private Bitmap nightCat;
	private Bitmap dayCat;
	private int catWidth;
	private int catHeight;
	private int flag; // 1设置白天，2设置晚上
	private float ratio;
	private RectF oval;
	private boolean enableSliding;
	private OnTimePickerListener onTimePickerListener;

	private Paint grayCirclePaint;
	private Paint lineCirclePaint;
	private Paint longScalePaint;
	private Paint shortScalePaint;
	private Paint progressPaint;
	private Paint numberPaint;
	private Paint unitPaint;
	private Paint nightCatPaint;
	private Paint dayCatPaint;


	public SleepTimeView(Context context) {
		this(context, null, 0);
	}

	public SleepTimeView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SleepTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		dp1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getContext().getResources().getDisplayMetrics());
		sp1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1, getContext().getResources().getDisplayMetrics());
		innerRadius = (int) (dp1 * 80);

		nightCat = BitmapFactory.decodeResource(getContext().getResources(), android.R.drawable.ic_popup_reminder);
		dayCat = BitmapFactory.decodeResource(getContext().getResources(), android.R.drawable.ic_popup_reminder);
		ratio = nightCat.getWidth() * 1.0f / nightCat.getHeight();
		catWidth = (int) (dp1 * 20);
		catHeight = (int) (catWidth / ratio);

		grayCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		grayCirclePaint.setStyle(Paint.Style.STROKE);
		grayCirclePaint.setColor(0x0F000000);
		grayCirclePaint.setStrokeWidth(dp1 * 16);

		lineCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		lineCirclePaint.setStyle(Paint.Style.STROKE);
		lineCirclePaint.setColor(0xFF7C59C4);
		lineCirclePaint.setStrokeWidth(dp1 * 6);

		longScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		longScalePaint.setColor(0xFF7C59C4);
		longScalePaint.setStrokeCap(Paint.Cap.ROUND);
		longScalePaint.setStrokeWidth(dp1 * 3);

		shortScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		shortScalePaint.setColor(0xFF7C59C4);
		shortScalePaint.setStrokeCap(Paint.Cap.ROUND);
		shortScalePaint.setStrokeWidth(dp1 * 2);

		progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		progressPaint.setStyle(Paint.Style.STROKE);
		progressPaint.setStrokeWidth(dp1 * 16);

		numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		numberPaint.setColor(0xFF7C59C4);
		numberPaint.setTextSize(sp1 * 32);

		unitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		unitPaint.setColor(0xFF7C59C4);
		unitPaint.setTextSize(sp1 * 20);

		nightCatPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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

		// 时间
		int intervalValue = (int) (sweepDegree / 360 * maxValue);
		String h = String.format("%02d", intervalValue / 60);
		String hUnit = "h";
		String m = String.format("%02d", intervalValue % 60);
		String mUnit = "m";
		float hW = numberPaint.measureText(h);
		float hUnitW = unitPaint.measureText(hUnit);
		float mW = numberPaint.measureText(m);
		float mUnitW = unitPaint.measureText(mUnit);
		float totalW = hW + hUnitW + mW + mUnitW;
		canvas.drawText(h, width / 2 - totalW / 2, width / 2 + (Math.abs(numberPaint.ascent()) - Math.abs(numberPaint.descent())) / 2, numberPaint);
		canvas.drawText(hUnit, width / 2 - totalW / 2 + hW, width / 2 + (Math.abs(numberPaint.ascent()) - Math.abs(numberPaint.descent())) / 2, unitPaint);
		canvas.drawText(m, width / 2 - totalW / 2 + hW + hUnitW, width / 2 + (Math.abs(numberPaint.ascent()) - Math.abs(numberPaint.descent())) / 2, numberPaint);
		canvas.drawText(mUnit, width / 2 - totalW / 2 + hW + hUnitW + mW, width / 2 + (Math.abs(numberPaint.ascent()) - Math.abs(numberPaint.descent())) / 2, unitPaint);

		// 进度渐变圆弧
		canvas.rotate(-90, width / 2, width / 2);
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
		canvas.drawBitmap(nightCat, new Rect(0, 0, nightCat.getWidth(), nightCat.getHeight()),
				new Rect(radius - catWidth / 2, (int) (radius - innerRadius + progressPaint.getStrokeWidth() - catHeight), radius + catWidth / 2, (int) (radius - innerRadius + progressPaint.getStrokeWidth())),
				nightCatPaint);
		canvas.rotate(sweepDegree, radius, radius);
		canvas.drawBitmap(dayCat, new Rect(0, 0, dayCat.getWidth(), dayCat.getHeight()),
				new Rect(radius - catWidth / 2, (int) (radius - innerRadius + progressPaint.getStrokeWidth() - catHeight), radius + catWidth / 2, (int) (radius - innerRadius + progressPaint.getStrokeWidth())),
				dayCatPaint);
	}

	public void start(int curNightValue, int curDayValue, Object objectTag) {

		if (curNightValue > maxValue) return;
		if (curDayValue > maxValue) return;

		Object tag = this.getTag();
		if (tag == null) return;
		if (!tag.equals(objectTag)) return;

		preNightValue = curNightValue;
		preDayValue = curDayValue;

		float end = preNightValue * 1.0f / maxValue * 360;
		ObjectAnimator sweepStartDegreeAnimator = ObjectAnimator.ofFloat(this, "SleepTimeView", sweepStartDegree, end)
				.setDuration((long) (DURATION * 1.0f * Math.abs(end - sweepStartDegree) / maxValue));
		sweepStartDegreeAnimator.setInterpolator(new DecelerateInterpolator());
		sweepStartDegreeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				sweepStartDegree = (Float) animation.getAnimatedValue();
				nightValue = (int) (sweepStartDegree / 360 * maxValue);
				invalidate();
			}
		});

		int interval = preDayValue - preNightValue;
		float intervalEndDegree = (interval >= 0 ? interval : (interval + maxValue)) * 1.0f / maxValue * 360;
		ObjectAnimator sweepDegreeAnimator = ObjectAnimator.ofFloat(this, "SleepTimeView", sweepDegree, intervalEndDegree)
				.setDuration((long) (DURATION * 1.0f * Math.abs(intervalEndDegree - sweepDegree) / 360));
		sweepDegreeAnimator.setInterpolator(new DecelerateInterpolator());
		sweepDegreeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				sweepDegree = (Float) animation.getAnimatedValue();
				int intervalValue = (int) (sweepDegree / 360 * maxValue);
				dayValue = ((nightValue + intervalValue) > maxValue) ? (nightValue + intervalValue - maxValue) : (nightValue + intervalValue);
				invalidate();
			}
		});

		sweepStartDegreeAnimator.start();
		sweepDegreeAnimator.start();
	}

	public void setOnTimePickerListener(OnTimePickerListener onTimePickerListener) {
		this.onTimePickerListener = onTimePickerListener;
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
						if (onTimePickerListener != null) {
							int intervalValue = (int) (sweepDegree / 360 * maxValue);
							dayValue = ((nightValue + intervalValue) > maxValue) ? (nightValue + intervalValue - maxValue) : (nightValue + intervalValue);
							onTimePickerListener.onSelectDayTime(dayValue);
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
					if (Math.abs(sweepStartDegree - deg) >= 0.3f) {
						sweepDegree += sweepStartDegree - deg;
						if (sweepDegree > 360) {
							sweepDegree -= 360;
						} else if (sweepDegree<0) {
							sweepDegree += 360;
						}
						sweepStartDegree = deg;
						if (sweepStartDegree > 360) {
							sweepStartDegree = 360;
						} else if (sweepStartDegree < 0) {
							sweepStartDegree = 0;
						}
						nightValue = (int) (sweepStartDegree / 360 * maxValue);
						if (onTimePickerListener != null) onTimePickerListener.onSelectNightTime(nightValue);
						invalidate();
						return true;
					}
				} else if (action == MotionEvent.ACTION_UP) {
					flag = 0;
				}
			}
		}
		getParent().requestDisallowInterceptTouchEvent(false);
		return super.onTouchEvent(event);
	}

	public void enableSliding(boolean enableSliding) {
		this.enableSliding = enableSliding;
	}

	public interface OnTimePickerListener {
		void onSelectNightTime(int minute);

		void onSelectDayTime(int minute);
	}
}
