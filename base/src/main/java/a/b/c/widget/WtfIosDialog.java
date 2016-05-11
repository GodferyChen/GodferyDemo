package a.b.c.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import a.b.c.R;

public class WtfIosDialog {

	private Dialog dialog;
	private View contentView;
	private ViewGroup childContentView;
	private View space0, space1;
	private View clickLayout;
	private TextView msgTextView;
	private TextView titleTextView;
	private TextView negativeTextView;
	private TextView positiveTextView;
	private OnNegativeListener onNegativeListener;
	private OnPositiveListener onPositiveListener;
	private String title;
	private String msg;
	private String negative;
	private String positive;

	public interface OnNegativeListener {
		void onNegative(Dialog dialog, View v);
	}

	public interface OnPositiveListener {
		void onPositive(Dialog dialog, View v);
	}

	public static WtfIosDialog newInstance(Activity activity) {
		return new WtfIosDialog(activity);
	}

	private WtfIosDialog(Activity activity) {
		dialog = new Dialog(activity);
		Window window = dialog.getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.getDecorView().setBackgroundResource(android.R.color.transparent);
		DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
		window.getAttributes().width = (int) (displayMetrics.widthPixels - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics));
		contentView = activity.getLayoutInflater().inflate(R.layout.layout_wtf_ios_dialog, null);
		childContentView = (ViewGroup) contentView.findViewById(R.id.childContentView);
		titleTextView = (TextView) contentView.findViewById(R.id.title);
		msgTextView = (TextView) contentView.findViewById(R.id.msg);
		space0 = contentView.findViewById(R.id.space0);
		space1 = contentView.findViewById(R.id.space1);
		clickLayout = contentView.findViewById(R.id.clickLayout);
		negativeTextView = (TextView) contentView.findViewById(R.id.cancel);
		positiveTextView = (TextView) contentView.findViewById(R.id.confirm);
		dialog.setContentView(contentView);
	}

	public WtfIosDialog setTitle(String title) {
		this.title = title;
		titleTextView.setText(title);
		return this;
	}

	public WtfIosDialog setMsg(String msg) {
		this.msg = msg;
		msgTextView.setText(msg);
		return this;
	}

	public WtfIosDialog setOnNegativeListener(String negative, OnNegativeListener onNegativeListener) {
		this.negative = negative;
		negativeTextView.setText(negative);
		this.onNegativeListener = onNegativeListener;
		negativeTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (WtfIosDialog.this.onNegativeListener != null) WtfIosDialog.this.onNegativeListener.onNegative(dialog, v);
			}
		});
		return this;
	}

	public WtfIosDialog setOnPositiveListener(String positive, OnPositiveListener onPositiveListener) {
		this.positive = positive;
		positiveTextView.setText(positive);
		this.onPositiveListener = onPositiveListener;
		positiveTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (WtfIosDialog.this.onPositiveListener != null) WtfIosDialog.this.onPositiveListener.onPositive(dialog, v);
			}
		});
		return this;
	}

	public WtfIosDialog setContentView(View contentView) {
		this.childContentView.addView(contentView);
		return this;
	}

	public WtfIosDialog setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
		dialog.setOnCancelListener(onCancelListener);
		return this;
	}

	public WtfIosDialog setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
		dialog.setOnDismissListener(onDismissListener);
		return this;
	}

	public WtfIosDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public WtfIosDialog setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
		return this;
	}

	public void show() {
		if (!dialog.isShowing()) {
			if (TextUtils.isEmpty(title) && TextUtils.isEmpty(msg) && childContentView.getChildCount() <= 0) {
				return;
			} else if (TextUtils.isEmpty(title) || TextUtils.isEmpty(msg) || childContentView.getChildCount() <= 0) {
				if (TextUtils.isEmpty(title)) {
					if (!TextUtils.isEmpty(msg)) space0.setVisibility(View.GONE);
					titleTextView.setVisibility(View.GONE);
				}
				if (TextUtils.isEmpty(msg)) {
					space0.setVisibility(View.GONE);
					msgTextView.setVisibility(View.GONE);
				}
				if (childContentView.getChildCount() <= 0) {
					space1.setVisibility(View.GONE);
					childContentView.setVisibility(View.GONE);
				}
			} else {
				space0.setVisibility(View.VISIBLE);
				space1.setVisibility(View.VISIBLE);
				titleTextView.setVisibility(View.VISIBLE);
				msgTextView.setVisibility(View.VISIBLE);
				childContentView.setVisibility(View.VISIBLE);
			}
			if (TextUtils.isEmpty(negative) && TextUtils.isEmpty(positive)) {
				clickLayout.setVisibility(View.GONE);
			} else if (TextUtils.isEmpty(negative) || TextUtils.isEmpty(positive)) {
				clickLayout.setVisibility(View.VISIBLE);
				if (TextUtils.isEmpty(negative)) {
					negativeTextView.setVisibility(View.GONE);
					positiveTextView.setBackgroundResource(R.drawable.selector_wtfios_single);
				} else if (TextUtils.isEmpty(positive)) {
					positiveTextView.setVisibility(View.GONE);
					negativeTextView.setBackgroundResource(R.drawable.selector_wtfios_single);
				}
			} else {
				clickLayout.setVisibility(View.VISIBLE);
				negativeTextView.setVisibility(View.VISIBLE);
				positiveTextView.setVisibility(View.VISIBLE);
				negativeTextView.setBackgroundResource(R.drawable.selector_wtfios_left);
				positiveTextView.setBackgroundResource(R.drawable.selector_wtfios_right);
			}
			dialog.show();
		}
	}

	public void dismiss() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}
