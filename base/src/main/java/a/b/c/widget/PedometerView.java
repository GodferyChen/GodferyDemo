package a.b.c.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class PedometerView extends View {

	private final static int DURATION = 3200;

	private int width;
	private int blurWidth;
	private int thinCircleToBoundWidth;
	private int thinCircleToSolidCircleWidth;
	private int textToSolidCircleHeight;
	private int[] colors = new int[]{0x00000000, 0xFF7C59C4, 0x00000000};
	private float[] positions = new float[]{0.0f, 0.0f, 1.0F};
	private RectF oval;

	private int preValue;
	private int value;
	private int preMaxValue;
	private int maxValue;
	private int maxValueTemp;

	private Paint solidCirclePaint;
	private Paint thickLineCirclePaint;
	private Paint thinLineCirclePaint;
	private Paint numberPaint;
	private Paint textPaint;

	public PedometerView(Context context) {
		this(context, null, 0);
	}

	public PedometerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PedometerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		if (Build.VERSION.SDK_INT >= 11) {
			setLayerType(LAYER_TYPE_SOFTWARE, null);
		}
		solidCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		solidCirclePaint.setColor(0xFF7C59C4);
		solidCirclePaint.setStyle(Paint.Style.FILL);

		thickLineCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		thickLineCirclePaint.setStyle(Paint.Style.STROKE);
		thickLineCirclePaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics()));

		thinLineCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		thinLineCirclePaint.setColor(0x3F7C59C4);
		thinLineCirclePaint.setStyle(Paint.Style.STROKE);
		thickLineCirclePaint.setStrokeCap(Paint.Cap.ROUND);
		thinLineCirclePaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics()));

		numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		numberPaint.setColor(0xFFFFFFFF);
		numberPaint.setTypeface(Typeface.DEFAULT_BOLD);
		numberPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 48, context.getResources().getDisplayMetrics()));

		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(0x9FFFFFFF);
		textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, context.getResources().getDisplayMetrics()));

		blurWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics());
		thinCircleToBoundWidth = (int) (blurWidth + thickLineCirclePaint.getStrokeWidth() / 2);
		thinCircleToSolidCircleWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());
		textToSolidCircleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, context.getResources().getDisplayMetrics());
		thickLineCirclePaint.setMaskFilter(new BlurMaskFilter(blurWidth, BlurMaskFilter.Blur.SOLID));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		width = MeasureSpec.getSize(widthMeasureSpec);
		oval = new RectF(thinCircleToBoundWidth, thinCircleToBoundWidth, width - thinCircleToBoundWidth, width - thinCircleToBoundWidth);
		setMeasuredDimension(width, width);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 最外层很细的圆圈
		canvas.drawCircle(width / 2, width / 2, width / 2 - thinCircleToBoundWidth, thinLineCirclePaint);

		// 里面的实心圆
		canvas.drawCircle(width / 2, width / 2, width / 2 - thinCircleToSolidCircleWidth - thinCircleToBoundWidth, solidCirclePaint);

		// 数字上下的文字
		String text1 = "步数";
		canvas.drawText(text1, width / 2 - textPaint.measureText(text1) / 2, thinCircleToSolidCircleWidth + textToSolidCircleHeight + Math.abs(textPaint.ascent()), textPaint);
		String text2 = "目标" + maxValue;
		canvas.drawText(text2, width / 2 - textPaint.measureText(text2) / 2, width - thinCircleToSolidCircleWidth - textToSolidCircleHeight - Math.abs(textPaint.descent()), textPaint);

		// 数字
		String text3;
		if (maxValue<=0) {
			text3 = String.valueOf(preValue);
		} else {
			text3 = String.valueOf(value);
		}
		canvas.drawText(text3, width / 2 - numberPaint.measureText(text3) / 2, width / 2 + (Math.abs(numberPaint.ascent()) - Math.abs(numberPaint.descent())) / 2, numberPaint);

		canvas.rotate(-90, width / 2, width / 2);
		// 进度会变的比较粗的圆圈
		if (maxValue<=0 && preMaxValue>0) {
			positions[1] = value * 1.0f / preMaxValue;
		} else if (maxValue>0){
			if (preValue>maxValue) {
				positions[1] = value * 1.0f / preValue;
			} else {
				positions[1] = value * 1.0f / maxValue;
			}
		} else if (maxValue<=0&&preMaxValue<=0) {
			positions[1] = 0.0f;
		}
		thickLineCirclePaint.setShader(new SweepGradient(width / 2, width / 2, colors, positions));
		canvas.drawArc(oval, 0, positions[1] * 360, false, thickLineCirclePaint);
		double radian = Math.toRadians(positions[1] * 360);
		float x = (float) (width / 2 + (width / 2 - thinCircleToBoundWidth) * Math.cos(radian));
		float y = (float) (width / 2 + (width / 2 - thinCircleToBoundWidth) * Math.sin(radian));

		canvas.drawCircle(x, y, thickLineCirclePaint.getStrokeWidth() * 1.0f, solidCirclePaint);
		canvas.drawCircle(x, y, thickLineCirclePaint.getStrokeWidth() * 1.5f, thinLineCirclePaint);
		canvas.drawCircle(x, y, thickLineCirclePaint.getStrokeWidth() * 2.0f, thinLineCirclePaint);
	}

	public void setMaxValue(int maxValue) {
		this.maxValueTemp = maxValue;
	}

	public void start(final int curValue, Object objectTag) {
		Object tag = this.getTag();
		if (tag == null) return;
		if (!tag.equals(objectTag)) return;
		this.preMaxValue = this.maxValue;
		this.maxValue = this.maxValueTemp;
//		if (maxValue>0) {
//			preValue = curValue>maxValue?maxValue:curValue;
//		} else {
			preValue = curValue;
//		}
		int start = value;
		if (start>preMaxValue) {
			start = preMaxValue;
		}
		int end = 0;
		if (maxValue>0) end = preValue;
		int intervalValue = end - start;
		long time = 0;
		if (maxValue>0) {
			time = (long) (DURATION*1.0f* Math.abs(intervalValue)/maxValue);
		} else if (preMaxValue>0) {
			time = (long) (DURATION*1.0f* Math.abs(intervalValue)/preMaxValue);
		}
		if (time>DURATION) time = DURATION;
		ObjectAnimator animator = ObjectAnimator.ofFloat(this, "PedometerView", start, end)
				.setDuration(time);
		animator.setInterpolator(new DecelerateInterpolator());
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				value = ((Float) animation.getAnimatedValue()).intValue();
				invalidate();
			}
		});
		animator.start();
	}
}
