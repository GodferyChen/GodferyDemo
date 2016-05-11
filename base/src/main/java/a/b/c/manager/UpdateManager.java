package a.b.c.manager;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import org.json.JSONObject;

import java.util.Locale;

import a.b.c.R;
import okhttp3.Call;

public class UpdateManager {

	public final static String SP_DOWNLOAD_ID = "download_id";

	public final static int MODE_HOME = 0;
	public final static int MODE_SETTINGS = 1;

	private Activity mActivity;
	private ProgressDialog mProgressDialog;
	private OkHttpManager mOkHttpManager;
	private Call mCall;
	private boolean isShowInfo = true;
	private boolean isShowProgress;
	private boolean isAttachedToWindow;
	private boolean isChecking;
	private String mVersionName;
	private int mVersionCode;

	public void onAttachedToWindow() {
		isAttachedToWindow = true;
	}

	public void onDetachedFromWindow() {
		isAttachedToWindow = false;
	}

	public static UpdateManager newInstance(Activity activity) {
		return new UpdateManager(activity);
	}

	private UpdateManager(Activity activity) {

		mActivity = activity;

		initVersion(activity);
		mOkHttpManager = defaultOkHttpManager(activity);
		initProgressDialog(activity);
	}

	private void initVersion(Activity activity) {
		try {
			PackageInfo info = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			if (info != null) {
				mVersionCode = info.versionCode;
				mVersionName = "v" + info.versionName;
			}
		} catch (Exception ignored) {
		}
	}

	private void initProgressDialog(Activity activity) {
		mProgressDialog = new ProgressDialog(activity);
		mProgressDialog.setMessage("正在检查更新…");
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				cancelCall();
			}
		});
	}

	private void cancelCall() {
		if (mCall != null) mCall.cancel();
	}

	private OkHttpManager defaultOkHttpManager(Activity activity) {
		return OkHttpManager.newInstance(activity)
				.domain(UrlManager.DOMAIN)
				.path("api/userapp/MobileAppVersion.jsp")
				.put("mobile_type", 1)// android
				.put("fromtype", 3)// 胎心
				.put("app_line", mVersionName)
				.put("language_type", Locale.getDefault().getLanguage().equals("zh") ? "0" : "1");
	}

	public UpdateManager okHttpManager(OkHttpManager okHttpManager) {
		mOkHttpManager = okHttpManager;
		return this;
	}

	public UpdateManager mode(int mode) {
		if (mode == MODE_HOME) {
			isShowProgress = false;
			isShowInfo = true;
		} else if (mode == MODE_SETTINGS) {
			isShowProgress = true;
			isShowInfo = true;
		}
		return this;
	}

	public void check() {
		if (!isChecking) {
			isChecking = true;
			if (isShowProgress && isAttachedToWindow) mProgressDialog.show();
			mCall = mOkHttpManager.get(new OkHttpManager.Callback() {
				@Override
				public void failure(OkHttpManager.Result result) {
					isChecking = false;
					mProgressDialog.dismiss();
				}

				@Override
				public void success(OkHttpManager.Result result) {
					isChecking = false;
					mProgressDialog.dismiss();
					try {
						if (LogManager.debug) LogManager.d("更新", result.string(false));
						JSONObject jsonObject = result.jsonObject(false);
						if (jsonObject != null) {
							int errorCode = jsonObject.optInt("errorCode", Integer.MIN_VALUE);
							if (errorCode == 0) {
								if (isShowInfo && isAttachedToWindow) {
									if (mVersionCode < jsonObject.optInt("versionCode", 0)) {
										String updateMsg = jsonObject.optString("updateLog", "");
										final boolean isQiangzhi = (jsonObject.optInt("is_qiangzhi", 0) != 0);
										final int qiangzhiVersionCode = jsonObject.optInt("qiangzhi_versionCode", 0);
										final String versionName = jsonObject.optString("versionName");
										final String downloadUrl = jsonObject.optString("downloadUrl", "");
										new AlertDialog.Builder(mActivity)
												.setTitle(mActivity.getString(R.string.new_version))
												.setMessage(updateMsg)
												.setPositiveButton(mActivity.getString(R.string.now_update), new OnClickListener() {
													@Override
													public void onClick(DialogInterface dialog, int which) {
														DownloadManager downloadManager = (DownloadManager) mActivity.getSystemService(Context.DOWNLOAD_SERVICE);
														Uri uri = Uri.parse(downloadUrl);
														DownloadManager.Request request = new DownloadManager.Request(uri);
														request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
														request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
														request.setMimeType("application/vnd.android.package-archive");
														request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mActivity.getString(R.string.apk_header) + versionName + ".apk");
														long id = downloadManager.enqueue(request);
														SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
														preferences.edit().putLong(SP_DOWNLOAD_ID, id).apply();
													}
												})
												.setNegativeButton(mActivity.getString(R.string.see_you_later), new OnClickListener() {
													@Override
													public void onClick(DialogInterface dialog, int which) {
														if (isQiangzhi && qiangzhiVersionCode > mVersionCode) {
															try {
																Intent intent = new Intent(mActivity, Class.forName("com.uwellnesshk.ukfh.activity.MainActivity"));
																intent.putExtra("EXTRA_EXIT", true);
																mActivity.startActivity(intent);
															} catch (ClassNotFoundException e) {
																e.printStackTrace();
															}
														}
													}
												})
												.setCancelable(false)
												.create()
												.show();
									} else {
										if (isShowProgress) {
											new AlertDialog.Builder(mActivity)
													.setMessage(mActivity.getString(R.string.already_new_version))
													.create()
													.show();
										}
									}
								}
							} else {
								LogManager.tS(mActivity, R.string.http_unknown);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	public static class CompleteReceiver extends BroadcastReceiver {

		public CompleteReceiver() {
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
				long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
				if (id == preferences.getLong(SP_DOWNLOAD_ID, Long.MIN_VALUE)) {
					DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
					DownloadManager.Query query = new DownloadManager.Query();
					query.setFilterById(id);
					Cursor cursor = downloadManager.query(query);
					String path = "";
					while (cursor.moveToNext()) {
						path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
					}
					cursor.close();
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setDataAndType(Uri.parse(path), "application/vnd.android.package-archive");
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}
			} else if (action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
			}
		}
	}
}
