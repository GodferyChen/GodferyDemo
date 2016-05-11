package a.b.c.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import a.b.c.R;

public class SimpleCustomView extends View {

	public final static int SCALE_FIT_XY =0;
	public final static int SCALE_CENTER =1;

	private String text;
	private int textColor;
	private int textSize;
	private Paint textPaint;
	private Rect textBound;

	private Rect globalRect;

	private Bitmap bitmap;
	private int scaleType;

	public SimpleCustomView(Context context) {
		this(context, null, 0);
	}

	public SimpleCustomView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SimpleCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.SimpleCustomView, defStyleAttr, 0);
		for (int i=0; i < array.getIndexCount(); i++) {
			int attr=array.getIndex(i);
			if (attr == R.styleable.SimpleCustomView_text) {
				text = array.getString(attr);
			} else if (attr == R.styleable.SimpleCustomView_textColor) {
				textColor = array.getColor(attr, Color.BLACK);
			} else if (attr == R.styleable.SimpleCustomView_textSize) {
				textSize = array.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
			} else if (attr == R.styleable.SimpleCustomView_image) {
				bitmap = BitmapFactory.decodeResource(getResources(), array.getResourceId(attr, 0));
			} else if (attr == R.styleable.SimpleCustomView_imageScaleType) {
				scaleType = array.getInt(attr, SCALE_FIT_XY);
			}
		}
		array.recycle();

		globalRect =new Rect();
		textPaint =new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(textSize);
		textBound =new Rect();
		textPaint.getTextBounds(text, 0, text.length(), textBound);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode=MeasureSpec.getMode(widthMeasureSpec);
		int widthSize=MeasureSpec.getSize(widthMeasureSpec);
		int heightMode=MeasureSpec.getMode(heightMeasureSpec);
		int heightSize=MeasureSpec.getSize(heightMeasureSpec);

		int width=0;
		int height=0;

		switch (widthMode) {
			case MeasureSpec.EXACTLY://一般是设置了明确的值或者是MATCH_PARENT
				width=widthSize;
				break;
			case MeasureSpec.AT_MOST://表示子布局限制在一个最大值内，一般为WARP_CONTENT
				int desiredImageAtMost=0;
				if (bitmap != null) desiredImageAtMost=getPaddingLeft() + bitmap.getWidth() + getPaddingRight();
				int desiredTextAtMost=getPaddingLeft() + textBound.width() + getPaddingRight();
				int desireAtMost=Math.max(desiredImageAtMost, desiredTextAtMost);
				width=Math.min(desireAtMost, widthSize);
				break;
			case MeasureSpec.UNSPECIFIED://表示子布局想要多大就多大，很少使用
				int desiredImageUnspecified=0;
				if (bitmap != null) desiredImageUnspecified=getPaddingLeft() + bitmap.getWidth() + getPaddingRight();
				int desiredTextUnspecified=getPaddingLeft() + textBound.width() + getPaddingRight();
				int desireUnspecified=Math.max(desiredImageUnspecified, desiredTextUnspecified);
				if (widthSize<=0) {
					width=desireUnspecified;
				} else {
					width=Math.min(desireUnspecified, widthSize);
				}
				break;
		}

		switch (heightMode) {
			case MeasureSpec.EXACTLY://一般是设置了明确的值或者是MATCH_PARENT
				height=heightSize;
				break;
			case MeasureSpec.AT_MOST://表示子布局限制在一个最大值内，一般为WARP_CONTENT
				int desireAtMost=getPaddingTop() + textBound.height() + getPaddingBottom();
				if (bitmap != null) desireAtMost+= bitmap.getHeight();
				height=Math.min(desireAtMost, heightSize);
				break;
			case MeasureSpec.UNSPECIFIED://表示子布局想要多大就多大，很少使用
				int desireUnspecified=getPaddingTop() + textBound.height() + getPaddingBottom();
				if (bitmap != null) desireUnspecified+= bitmap.getHeight();
				if (heightSize<=0) {
					height=desireUnspecified;
				} else {
					height=Math.min(desireUnspecified, heightSize);
				}
				break;
		}

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		textPaint.setColor(Color.TRANSPARENT);
		canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), textPaint);

		globalRect.left=getPaddingLeft();
		globalRect.right=getWidth() - getPaddingRight();
		globalRect.top=getPaddingTop();
		globalRect.bottom=getHeight() - getPaddingBottom();

		textPaint.setColor(textColor);
		if (textBound.width() > getWidth()) {//当前设置的宽度小于字体需要的宽度，将字体改为xxx...
			TextPaint paint=new TextPaint(textPaint);
			String text=TextUtils.ellipsize(this.text, paint, getWidth() - getPaddingLeft() - getPaddingRight(), TextUtils.TruncateAt.END).toString();
			canvas.drawText(text, getPaddingLeft(), getHeight() - getPaddingBottom(), textPaint);
		} else {//正常情况，将字体居中
			canvas.drawText(text, getWidth() / 2 - textBound.width() / 2, getHeight() - getPaddingBottom(), textPaint);
		}

		globalRect.bottom-= textBound.height();

		if (bitmap != null) {
			if (scaleType == SCALE_FIT_XY) {
				canvas.drawBitmap(bitmap, null, globalRect, textPaint);
			} else {
				globalRect.left=getWidth() / 2 - bitmap.getWidth() / 2;
				globalRect.right=getWidth() / 2 + bitmap.getWidth() / 2;
				globalRect.top=(getHeight() - textBound.height()) / 2 - bitmap.getHeight() / 2;
				globalRect.bottom=(getHeight() - textBound.height()) / 2 + bitmap.getHeight() / 2;
				canvas.drawBitmap(bitmap, null, globalRect, textPaint);
			}
		}
	}
}
