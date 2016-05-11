package a.b.c.widget;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

public class SelfTextView extends TextView {

	public SelfTextView(Context context) {
		super(context);
	}

	public SelfTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SelfTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		Layout layout = getLayout();
		if (layout != null) {
			int height = (int) Math.ceil(getMaxLineHeight(this.getText().toString()))
					+ getCompoundPaddingTop() + getCompoundPaddingBottom();
			int width = getMeasuredWidth();
			setMeasuredDimension(width, height);
		}
	}

	private float getMaxLineHeight(String str) {
		float height = 0.0f;
		float screenW = getResources().getDisplayMetrics().widthPixels;
		float paddingLeft = ((LinearLayout) this.getParent()).getPaddingLeft();
		float paddingReft = ((LinearLayout) this.getParent()).getPaddingRight();
		//这里具体this.getPaint()要注意使用，要看你的TextView在什么位置，这个是拿TextView父控件的Padding的，为了更准确的算出换行
		int line = (int) Math.ceil((this.getPaint().measureText(str) / (screenW - paddingLeft - paddingReft)));
		try {
			Field field = this.getClass().getSuperclass().getDeclaredField("mMaximum");
			field.setAccessible(true);
			int maxLines = field.getInt(this);
			if (line > maxLines) line = maxLines;
		} catch (Exception e) {
			e.printStackTrace();
		}
		height = (this.getPaint().getFontMetrics().descent - this.getPaint().getFontMetrics().ascent) * line;
		return height;
	}
}
