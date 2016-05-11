package a.b.c.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

public class FavorLayout extends RelativeLayout {

	private static final String TAG = "FavorLayout";

	private Interpolator linearInterpolator = new LinearInterpolator();//线性
	private Interpolator accelerateInterpolator = new AccelerateInterpolator();//加速
	private Interpolator decelerateInterpolator = new DecelerateInterpolator();//减速
	private Interpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();//先加速后减速
	private Interpolator[] interpolators;

	private int mHeight;
	private int mWidth;

	private LayoutParams layoutParams;

	public FavorLayout(Context context) {
		super(context);
	}

	public FavorLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBackgroundColor(0x00000000);
		init();
	}

	private Drawable red;
	private Drawable yellow;
	private Drawable blue;
	private Drawable[] drawables;

	private Random random = new Random();

	private int dHeight;
	private int dWidth;

	private void init() {

		//初始化显示的图片
		drawables = new Drawable[3];
		red = getResources().getDrawable(android.R.drawable.ic_popup_reminder);
		yellow = getResources().getDrawable(android.R.drawable.ic_popup_reminder);
		blue = getResources().getDrawable(android.R.drawable.ic_popup_reminder);

		drawables[0] = red;
		drawables[1] = yellow;
		drawables[2] = blue;
		//获取图的宽高 用于后面的计算
		//注意 我这里3张图片的大小都是一样的,所以我只取了一个
		dHeight = red.getIntrinsicHeight();
		dWidth = red.getIntrinsicWidth();

		//底部 并且 水平居中
		layoutParams = new LayoutParams(dWidth, dHeight);
		layoutParams.addRule(CENTER_HORIZONTAL, TRUE);//这里的TRUE 要注意 不是true
		layoutParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);

		// 初始化插补器
		interpolators = new Interpolator[4];
		interpolators[0] = linearInterpolator;
		interpolators[1] = accelerateInterpolator;
		interpolators[2] = decelerateInterpolator;
		interpolators[3] = accelerateDecelerateInterpolator;

	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//获取本身的宽高 这里要注意,测量之后才有宽高
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
	}


	public void addFavor() {

		ImageView imageView = new ImageView(getContext());
		//随机选一个
		imageView.setImageDrawable(drawables[random.nextInt(3)]);
		imageView.setLayoutParams(layoutParams);

		addView(imageView);
		Log.v(TAG, "add后子view数:" + getChildCount());

		Animator set = getAnimator(imageView);
		set.addListener(new AnimEndListener(imageView));
		set.start();
	}

	private Animator getAnimator(View target) {
		AnimatorSet set = getEnterAnimtor(target);

		ValueAnimator bezierValueAnimator = getBezierValueAnimator(target);

		AnimatorSet finalSet = new AnimatorSet();
		finalSet.playSequentially(set);
		finalSet.playSequentially(set, bezierValueAnimator);
		finalSet.setInterpolator(interpolators[random.nextInt(4)]);
		finalSet.setTarget(target);
		return finalSet;
	}

	private AnimatorSet getEnterAnimtor(final View target) {

		ObjectAnimator alpha = ObjectAnimator.ofFloat(target, ALPHA, 0.2f, 1f);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, SCALE_X, 0.2f, 1f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, SCALE_Y, 0.2f, 1f);
		AnimatorSet enter = new AnimatorSet();
		enter.setDuration(500);
		enter.setInterpolator(new LinearInterpolator());
		enter.playTogether(alpha, scaleX, scaleY);
		enter.setTarget(target);
		return enter;
	}

	public static final Property<View, Float> ALPHA = new Property<View, Float>(Float.class, "alpha") {

		@Override
		public void set(View object, Float value) {
			object.setAlpha(value);
		}

		@Override
		public Float get(View object) {
			return object.getAlpha();
		}
	};

	public static final Property<View, Float> SCALE_X = new Property<View, Float>(Float.class, "scaleX") {

		@Override
		public void set(View object, Float value) {
			object.setScaleX(value);
		}

		@Override
		public Float get(View object) {
			return object.getScaleX();
		}
	};

	public static final Property<View, Float> SCALE_Y = new Property<View, Float>(Float.class, "scaleY") {
		@Override
		public void set(View object, Float value) {
			object.setScaleY(value);
		}

		@Override
		public Float get(View object) {
			return object.getScaleY();
		}
	};

	private ValueAnimator getBezierValueAnimator(View target) {

		//初始化一个贝塞尔计算器- - 传入
		BezierEvaluator evaluator = new BezierEvaluator(getPointF(2), getPointF(1));

		//这里最好画个图 理解一下 传入了起点 和 终点
		ValueAnimator animator = ValueAnimator.ofObject(evaluator, new PointF((mWidth - dWidth) / 2, mHeight - dHeight), new PointF(random.nextInt(getWidth()), 0));
		animator.addUpdateListener(new BezierListenr(target));
		animator.setTarget(target);
		animator.setDuration(3000);
		return animator;
	}

	/**
	 * 获取中间的两个 点
	 *
	 * @param scale
	 */
	private PointF getPointF(int scale) {

		PointF pointF = new PointF();
		pointF.x = random.nextInt(mWidth-100);//减去100 是为了控制 x轴活动范围,看效果 随意~~
		//再Y轴上 为了确保第二个点 在第一个点之上,我把Y分成了上下两半 这样动画效果好一些  也可以用其他方法
		pointF.y = random.nextInt(mHeight-100) / scale;
		return pointF;
	}

	private class BezierListenr implements ValueAnimator.AnimatorUpdateListener {

		private View target;

		public BezierListenr(View target) {
			this.target = target;
		}

		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			//这里获取到贝塞尔曲线计算出来的的x y值 赋值给view 这样就能让爱心随着曲线走啦
			PointF pointF = (PointF) animation.getAnimatedValue();
			target.setX(pointF.x);
			target.setY(pointF.y);
			target.setAlpha(1 - animation.getAnimatedFraction());
		}
	}


	private class AnimEndListener extends AnimatorListenerAdapter {
		private View target;

		public AnimEndListener(View target) {
			this.target = target;
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			super.onAnimationEnd(animation);
			//因为不停的add 导致子view数量只增不减,所以在view动画结束后remove掉
			removeView((target));
			Log.v(TAG, "removeView后子view数:" + getChildCount());
		}
	}

	private class BezierEvaluator implements TypeEvaluator<PointF> {


		private PointF pointF1;
		private PointF pointF2;

		public BezierEvaluator(PointF pointF1, PointF pointF2) {
			this.pointF1 = pointF1;
			this.pointF2 = pointF2;
		}

		@Override
		public PointF evaluate(float time, PointF startValue,
		                       PointF endValue) {

			float timeLeft = 1.0f - time;
			PointF point = new PointF();//结果

			PointF point0 = (PointF) startValue;//起点

			PointF point3 = (PointF) endValue;//终点

			// 贝塞尔曲线
			point.x = timeLeft * timeLeft * timeLeft * (point0.x)
					+ 3 * timeLeft * timeLeft * time * (pointF1.x)
					+ 3 * timeLeft * time * time * (pointF2.x)
					+ time * time * time * (point3.x);

			point.y = timeLeft * timeLeft * timeLeft * (point0.y)
					+ 3 * timeLeft * timeLeft * time * (pointF1.y)
					+ 3 * timeLeft * time * time * (pointF2.y)
					+ time * time * time * (point3.y);
			return point;
		}
	}
}
