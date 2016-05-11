package a.b.c.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import a.b.c.R;

public class TagView extends LinearLayout {

	private ArrayList<String> tagList = new ArrayList<String>();
	private ArrayList<Integer> idList = new ArrayList<Integer>();
	private ArrayList<TextView> textViewList = new ArrayList<TextView>();

	private ViewTreeObserver viewTreeObserver;

	private int mWidth;

	private boolean initialized = false;

	float dp1;
	int lineMargin = 4;
	int tagMargin = 4;
	int textPaddingLeft = 12;
	int textPaddingRight = 12;
	int layoutWidthOffset = 2;

	int gravity = Gravity.LEFT;

	public TagView(Context context) {
		this(context, null);
	}

	public TagView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TagView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs, defStyle);
	}

	private void initialize(Context context, AttributeSet attrs, int defStyle) {
		dp1 = TypedValue.applyDimension(1, 1, getResources().getDisplayMetrics());
		viewTreeObserver = getViewTreeObserver();
		viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (!initialized) {
					initialized = true;
					drawTags();
				}
			}
		});
		setOrientation(LinearLayout.VERTICAL);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = w;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getMeasuredWidth();
		if (width <= 0) return;
		mWidth = getMeasuredWidth();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawTags();
	}

	public void setGravity(int gravity) {
		this.gravity = gravity;
	}

	public void drawTags() {

		removeAllViews();

		if (getChildCount() < 1) {
			LinearLayout linearLayout = new LinearLayout(getContext());
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			linearLayout.setGravity(gravity);
			addView(linearLayout, -1, -2);
		}

		float total = getPaddingLeft() + getPaddingRight();

		for (int i = 0; i < tagList.size(); i++) {
			final String tag = tagList.get(i);
			final TextView tagTextView = new TextView(getContext());
			tagTextView.setEllipsize(TextUtils.TruncateAt.END);
			tagTextView.setSingleLine();
			tagTextView.setText(tag);
			tagTextView.setPadding((int) (textPaddingLeft * dp1), 0, (int) (textPaddingRight * dp1), 0);
			tagTextView.setGravity(Gravity.CENTER);
			tagTextView.setHeight((int) (dp1 * 24));
			tagTextView.setBackgroundDrawable(getDrawable());
			tagTextView.setTextColor(0xFF999999);
			tagTextView.setTextColor(getColor());
			textViewList.add(tagTextView);
			if (idList != null && idList.size() > 0) {
				tagTextView.setId(idList.get(i));
				tagTextView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (tagTextView.isSelected()) {
							tagTextView.setSelected(false);
						} else {
							tagTextView.setSelected(true);
						}
					}
				});
			}

			float tagWidth = tagTextView.getPaint().measureText(tag) + textPaddingLeft * dp1 + textPaddingRight * dp1 + 1 * dp1 * 2;
			LayoutParams tagParams = new LayoutParams(-2, -2);
			tagParams.bottomMargin = (int) (lineMargin * dp1);
			tagParams.topMargin = (int) (1 * dp1 * 2);

			if (mWidth <= total + tagWidth + layoutWidthOffset * dp1) {
				total = getPaddingLeft() + getPaddingRight();
				LinearLayout newLinearLayout = new LinearLayout(getContext());
				newLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
				newLinearLayout.setGravity(gravity);
				addView(newLinearLayout, -1, -2);
			}
			LinearLayout linearLayout = (LinearLayout) getChildAt(getChildCount() - 1);
			linearLayout.addView(tagTextView, tagParams);
			LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
			if (linearLayout.getChildCount()>=1) {
				tagParams.leftMargin = (int) (tagMargin + 1 * dp1 * 2);
				total += tagParams.leftMargin;
			}

			layoutParams.topMargin = (int) (1 * dp1);
			layoutParams.bottomMargin = (int) (1 * dp1);
			total += tagWidth;
		}
	}

	private ColorStateList getColor() {
		return new ColorStateList(new int[][]{
//				new int[]{android.R.attr.state_pressed},
				new int[]{android.R.attr.state_selected},
				new int[]{}},
				new int[]{
//						0xFFFFFFFF,
						0xFFFFFFFF,
						0xFF999999}
		);
	}

	private Drawable getDrawable() {
		StateListDrawable states = new StateListDrawable();
		GradientDrawable gdNormal = new GradientDrawable();
		gdNormal.setColor(Color.TRANSPARENT);
		gdNormal.setCornerRadius(dp1 * 12);
		gdNormal.setStroke((int) dp1, 0xFF999999);
		int[] attrs = {R.attr.colorPrimary};
		TypedArray array = getContext().obtainStyledAttributes(attrs);
		int colorPrimary = array.getColor(0, 0xFF000000);
		array.recycle();
//		GradientDrawable gdPressed = new GradientDrawable();
//		gdPressed.setColor(colorPrimary);
//		gdPressed.setCornerRadius(dp1 * 12);
		GradientDrawable gdSelected = new GradientDrawable();
		gdSelected.setColor(colorPrimary);
		gdSelected.setCornerRadius(dp1 * 12);
//		states.addState(new int[]{android.R.attr.state_pressed}, gdPressed);
		states.addState(new int[]{android.R.attr.state_selected}, gdSelected);
		states.addState(new int[]{}, gdNormal);
		return states;
	}

	public ArrayList<TextView> getTextViews() {
		return textViewList;
	}

	public void addTags(ArrayList<String> tagList) {
		if (tagList == null) return;
		this.tagList.addAll(tagList);
	}

	public void addIds(ArrayList<Integer> idList) {
		if (idList == null) return;
		this.idList.addAll(idList);
	}

	public ArrayList<String> getTags() {
		return tagList;
	}

	public void remove(int position) {
		tagList.remove(position);
	}

	public void removeAllTags() {
		tagList.clear();
		idList.clear();
	}
}