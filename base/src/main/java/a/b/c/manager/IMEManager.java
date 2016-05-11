package a.b.c.manager;

import android.content.Context;
import android.os.Handler;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class IMEManager {

	/**
	 * 打开输入法
	 */
	public static void openIME(final EditText editText) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.showSoftInput(editText, 0);
			}
		}, 150);
	}

	/**
	 * 关闭输入法
	 */
	public static void closeIME(EditText editText) {
		InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
}
