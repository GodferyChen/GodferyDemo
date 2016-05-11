package a.b.c.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class SodoukuView extends View {

	public final static int GREEN_100 = 0xffc8e6c9;
	public final static int GREEN_200 = 0xffa5d6a7;
	public final static int GREEN_300 = 0xff81c784;
	public final static int GRAY_400 = 0xffbdbdbd;
	public final static int GRAY_900 = 0xff212121;
	public final static int RED_500 = 0xffe51c23;
	private float mWidth;// 大的九宫格单元格宽度
	private float mHeight;// 大的九宫格单元格高度
	private String mSodoukuData;
	private int[] mSodouku;// 九宫格中的所有数据
	private int[] mCancel;// 可以修改的单元格
	private int mPressXPos = -1;// 按下的单元格X坐标
	private int mPressYPos = -1;// 按下的单元格Y坐标
	private int mPressStatus = -99;
	private int mPressNum = -1;
	private boolean mEnable = true;// 防止选数字时,出现两个Dialog实例

	public SodoukuView(Context context) {
		super(context);
	}

	public SodoukuView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SodoukuView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mWidth = w / 9f;
		mHeight = h / 9f;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		drawLine(canvas);

		drawNum(canvas);

		drawPress(canvas);
	}

	private void drawPress(Canvas canvas) {

		if (mPressXPos >= 0 && mPressYPos >= 0) {
			Paint pressPaint = new Paint();
			pressPaint.setAntiAlias(true);
			if (mPressStatus == MotionEvent.ACTION_DOWN) {
				pressPaint.setColor(GREEN_300);
				canvas.drawRect(mWidth * mPressXPos + 1, mHeight * mPressYPos + 1, mWidth * (mPressXPos + 1) - 1, mHeight * (mPressYPos + 1) - 1, pressPaint);
				if (mPressNum > 0) {
					Paint numPaint = new Paint();
					numPaint.setAntiAlias(true);
					numPaint.setColor(Color.BLACK);
					numPaint.setStyle(Style.STROKE);
					numPaint.setTextAlign(Align.CENTER);
					numPaint.setTextSize(mHeight * 0.75f);
					numPaint.setTypeface(Typeface.SERIF);
					FontMetrics fontMetrics = numPaint.getFontMetrics();
					float x = mWidth / 2;
					float y = mHeight / 2 - (fontMetrics.ascent + fontMetrics.descent) / 2;
					canvas.drawText(String.valueOf(mPressNum), mWidth * mPressXPos + x, mHeight * mPressYPos + y, numPaint);
				}
			} else if (mPressStatus == MotionEvent.ACTION_UP) {
				pressPaint.setColor(Color.TRANSPARENT);
				mEnable = false;
				canvas.drawRect(mWidth * mPressXPos + 1, mHeight * mPressYPos + 1, mWidth * (mPressXPos + 1) - 1, mHeight * (mPressYPos + 1) - 1, pressPaint);
				if (mPressNum > 0) {
					showDeleteDialog(mPressXPos, mPressYPos);
					mPressNum = -1;
				} else {
					showNumDialog(getUsable(mPressXPos, mPressYPos), mPressXPos, mPressYPos);
				}
			}
			mPressXPos = -1;
			mPressYPos = -1;
			mPressStatus = -99;
		}
	}

	private void drawNum(Canvas canvas) {

		Paint numPaint = new Paint();
		numPaint.setAntiAlias(true);
		numPaint.setColor(Color.BLACK);
		numPaint.setStyle(Style.STROKE);
		numPaint.setTextAlign(Align.CENTER);
		numPaint.setTextSize(mHeight * 0.75f);
		numPaint.setTypeface(Typeface.SERIF);
		FontMetrics fontMetrics = numPaint.getFontMetrics();

		float x = mWidth / 2;
		float y = mHeight / 2 - (fontMetrics.ascent + fontMetrics.descent) / 2;
		if (mSodouku == null) {
			String data = "360000000" + "004230800" + "000004200" + "070460003" + "820000014" + "500013020" + "001900000" + "007048300" + "000000045";
			mSodouku = parseData(data);
			mCancel = parseData(data);
		}

		Paint rectPaint = new Paint();
		rectPaint.setAntiAlias(true);
		rectPaint.setColor(GREEN_100);

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				int num = mSodouku[i * 9 + j];
				boolean isCancel = mCancel[i * 9 + j] == 0;
				if (isCancel) {
					canvas.drawRect(mWidth * j + 1, mHeight * i + 1, mWidth * (j + 1) - 1, mHeight * (i + 1) - 1, rectPaint);
				}
				canvas.drawText(String.valueOf(num == 0 ? "" : num), mWidth * j + x, mHeight * i + y, numPaint);
			}
		}
	}

	/**
	 * 画九宫格的布局线
	 */
	private void drawLine(Canvas canvas) {

		Paint ltgrayPaint = new Paint();
		ltgrayPaint.setColor(GRAY_400);
		ltgrayPaint.setAntiAlias(true);

		Paint dkgrayPaint = new Paint();
		dkgrayPaint.setColor(GRAY_900);
		dkgrayPaint.setAntiAlias(true);

		Paint bgPaint = new Paint();
		bgPaint.setColor(GREEN_200);
		bgPaint.setAntiAlias(true);

		// 画背景
		canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);

		for (int i = 0; i < 9; i++) {
			if (i % 3 == 0) {
				// 画大单元格分割线
				canvas.drawLine(0, mHeight * i, getWidth(), mHeight * i, dkgrayPaint);
				canvas.drawLine(mWidth * i, 0, mWidth * i, getHeight(), dkgrayPaint);
			} else {
				// 画小单元格分割线
				canvas.drawLine(0, mHeight * i, getWidth(), mHeight * i, ltgrayPaint);
				canvas.drawLine(mWidth * i, 0, mWidth * i, getHeight(), ltgrayPaint);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int x = (int) (event.getX() / mWidth);
		int y = (int) (event.getY() / mHeight);
		mPressXPos = x;
		mPressYPos = y;
		if (mEnable) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mPressStatus = MotionEvent.ACTION_DOWN;
				if (mSodouku[y * 9 + x] == 0) {
					invalidate();
					return true;
				} else if (mCancel[y * 9 + x] == 0) {
					mPressNum = mSodouku[y * 9 + x];
					invalidate();
					return true;
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				mPressStatus = MotionEvent.ACTION_UP;
				invalidate();
				return true;
			}
		}
		return super.onTouchEvent(event);
	}

	@SuppressWarnings("deprecation")
	private void showDeleteDialog(final int x, final int y) {

		Context context = getContext();
		int dp16 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
		StateListDrawable drawable = new StateListDrawable();
		GradientDrawable pressed = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{GREEN_300, GREEN_300});
		drawable.addState(new int[]{android.R.attr.state_pressed}, pressed);
		GradientDrawable normal = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{GREEN_100, GREEN_100});
		drawable.addState(new int[]{}, normal);

		final Dialog dialog = new Dialog(context);
		TextView delete = new TextView(context);
		delete.setPadding(dp16 * 3, dp16, dp16 * 3, dp16);
		delete.setText("撤销该数字");
		delete.setTextSize(32);
		delete.setGravity(Gravity.CENTER);
		delete.setTextColor(RED_500);
		if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN) {
			delete.setBackgroundDrawable(drawable);
		} else {
			delete.setBackground(drawable);
		}
		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSodouku[y * 9 + x] = 0;
				mEnable = true;
				dialog.dismiss();
				invalidate();
			}
		});
		Window window = dialog.getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams windowParams = window.getAttributes();
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(windowParams);
		dialog.setContentView(delete);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				mEnable = true;
			}
		});
		dialog.show();
	}

	@SuppressWarnings("deprecation")
	private void showNumDialog(int[] usable, final int x, final int y) {

		Context context = getContext();
		final Dialog dialog = new Dialog(context);
		int dp16 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

		LinearLayout container = new LinearLayout(getContext());
		container.setOrientation(LinearLayout.VERTICAL);
		container.setBackgroundColor(Color.WHITE);
		container.setPadding(dp16, dp16, dp16, dp16);
		LinearLayout line1 = new LinearLayout(context);
		LinearLayout line2 = new LinearLayout(context);
		LinearLayout line3 = new LinearLayout(context);
		LayoutParams line2Params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		line2Params.topMargin = dp16;
		line2Params.bottomMargin = dp16;
		line2.setLayoutParams(line2Params);
		boolean isSelect = false;
		for (int i = 0; i < 9; i++) {
			if (usable[i] > 0) {
				isSelect = true;
				break;
			}
		}
		if (isSelect) {
			for (int i = 0; i < 9; i++) {
				StateListDrawable drawable = new StateListDrawable();
				GradientDrawable pressed = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{GREEN_300, GREEN_300});
				drawable.addState(new int[]{android.R.attr.state_pressed}, pressed);
				GradientDrawable normal = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{GREEN_100, GREEN_100});
				drawable.addState(new int[]{}, normal);
				TextView num = new TextView(context);
				if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN) {
					num.setBackgroundDrawable(drawable);
				} else {
					num.setBackground(drawable);
				}
				num.setTypeface(Typeface.SERIF);
				num.setTextSize(16 * 4 * 0.75f);
				num.setGravity(Gravity.CENTER);
				num.setTextColor(Color.DKGRAY);
				final int data = usable[i];
				num.setText(String.valueOf(data));
				if (data == 0) {
					num.setVisibility(View.INVISIBLE);
				}
				LayoutParams params = new LayoutParams(dp16 * 4, dp16 * 4);
				if (i == 1 || i == 4 || i == 7) {
					params.leftMargin = dp16;
					params.rightMargin = dp16;
				}
				num.setLayoutParams(params);
				num.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mSodouku[y * 9 + x] = data;
						mEnable = true;
						dialog.dismiss();
						invalidate();
					}
				});
				if (i < 3) {
					line1.addView(num);
				} else if (i < 6) {
					line2.addView(num);
				} else if (i < 9) {
					line3.addView(num);
				}
			}
			container.addView(line1);
			container.addView(line2);
			container.addView(line3);
		} else {
			TextView nullNum = new TextView(context);
			nullNum.setPadding(dp16 * 2, 0, dp16 * 2, 0);
			nullNum.setText("无可选数字");
			nullNum.setTextSize(32);
			nullNum.setGravity(Gravity.CENTER);
			nullNum.setTextColor(Color.DKGRAY);
			container.addView(nullNum);
		}

		Window window = dialog.getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(params);
		dialog.setContentView(container);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				mEnable = true;
			}
		});
		dialog.show();
	}

	/**
	 * 解析字符串数据为二位整型数组
	 */
	private int[] parseData(String data) {

		int len = data.length();
		int[] sodouku = new int[len];
		for (int i = 0; i < len; i++) {
			sodouku[i] = data.charAt(i) - '0';
		}
		return sodouku;
	}

	/**
	 * 通过单元格坐标获得点击单元格可用数据
	 */
	private int[] getUsable(int x, int y) {

		int[] used = new int[9];
		int[] usable = new int[9];

		// 找出x轴用过的数据
		for (int i = 0; i < 9; i++) {
			if (i != x) {
				int num = mSodouku[y * 9 + i];
				if (num != 0) {
					used[num - 1] = num;
				}
			}
		}

		// 找出y轴用过的数据
		for (int i = 0; i < 9; i++) {
			if (i != y) {
				int num = mSodouku[i * 9 + x];
				if (num != 0) {
					used[num - 1] = num;
				}
			}
		}

		// 找出小九宫格中用过的数据
		int startX = (x / 3) * 3;
		int startY = (y / 3) * 3;
		for (int i = startX; i < startX + 3; i++) {
			for (int j = startY; j < startY + 3; j++) {
				if (i != x && j != y) {
					int num = mSodouku[j * 9 + i];
					if (num != 0) {
						used[num - 1] = num;
					}
				}
			}
		}

		// 找出可用的数据
		for (int i = 0; i < 9; i++) {
			int num = used[i];
			if (num == 0) {
				usable[i] = i + 1;
			}
		}

		return usable;
	}
}
