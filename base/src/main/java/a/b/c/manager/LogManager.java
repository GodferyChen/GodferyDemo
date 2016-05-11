package a.b.c.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class LogManager {

	public static boolean debug = true;

	public static void setDebug(boolean debug) {
		LogManager.debug = debug;
	}

	public static void v(String tag, String log) {
		if (debug) Log.v(tag, log);
	}

	public static void d(String tag, String log) {
		if (debug) Log.d(tag, log);
	}

	public static void i(String tag, String log) {
		if (debug) Log.i(tag, log);
	}

	public static void w(String tag, String log) {
		if (debug) Log.w(tag, log);
	}

	public static void e(String tag, String log) {
		if (debug) Log.e(tag, log);
	}

	/**
	 * Toast短时间
	 */
	public static void tS(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Toast短时间
	 */
	public static void tS(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Toast长时间
	 */
	public static void tL(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * Toast长时间
	 */
	public static void tL(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
	}
}
