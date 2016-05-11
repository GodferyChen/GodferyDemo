package a.b.c.manager;

import android.content.Context;
import android.util.TypedValue;

public class DimenManager {

	private DimenManager() {}

	// dp转像素
	public static float dp2px(Context context, float value) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
	}

	// sp转像素
	public static float sp2px(Context context, float value) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, context.getResources().getDisplayMetrics());
	}

	// pt转像素
	public static float pt2px(Context context, float value) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, value, context.getResources().getDisplayMetrics());
	}

	// 英寸转像素
	public static float in2px(Context context, float value) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, value, context.getResources().getDisplayMetrics());
	}

	// 毫米转像素
	public static float mm2px(Context context, float value) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, value, context.getResources().getDisplayMetrics());
	}
}
