package a.b.c.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import a.b.c.manager.cookie.CookiesManager;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class OkHttpManager {

	private final static String TAG = "OkHttpManager";

	private final static String GET = "GET";
	private final static String POST = "POST";

	private String method = GET;
	private String tag;
	private String scheme;
	private String host;
	private int port = 80;
	private String domain;
	private String path;
	private boolean isSync;
	private boolean isChild;
	private boolean isCache;
	private Context context;
	private final SortedMap<String, Object> sortedMap = new TreeMap<String, Object>();
	private final HashMap<String, Object> params = new HashMap<String, Object>();
	private final Handler handler = new Handler(Looper.getMainLooper());

	private OkHttpManager() {
	}

	public static OkHttpManager newInstance(@NonNull Context context) {
		OkHttpManager manager = new OkHttpManager();
		manager.context = context;
		return manager;
	}

	public OkHttpManager domain(@NonNull String domain) {
		this.domain = domain;
		return this;
	}

	public OkHttpManager tag(@Nullable String tag) {
		this.tag = tag;
		return this;
	}

	@NonNull
	public OkHttpManager path(@Nullable String path) {
		this.path = path;
		return this;
	}

	public OkHttpManager child() {
		this.isChild = true;
		return this;
	}

	@NonNull
	public OkHttpManager sync() {
		this.isSync = true;
		return this;
	}

	@NonNull
	public OkHttpManager cache() {
		this.isCache = true;
		return this;
	}

	public boolean hasNetwork() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			return networkInfo != null && networkInfo.isConnectedOrConnecting();
		}
		return false;
	}

	@NonNull
	public OkHttpManager action(String action) {
		put("action", action);
		return this;
	}

	@NonNull
	public OkHttpManager put(@NonNull String key, @Nullable Object value) {
		if (!TextUtils.isEmpty(key) && value != null) {
			params.put(key, value);
			if (!(value instanceof File)) sortedMap.put(key, value);
		}
		return this;
	}

	@NonNull
	public OkHttpManager putAll(@Nullable Map<String, String> map) {
		if (map != null) params.putAll(map);
		return this;
	}

	@NonNull
	private HashMap<String, Object> paramsSign() {
		if (!TextUtils.isEmpty(tag)) {
			String postBody = "";
			for (Map.Entry entry : sortedMap.entrySet()) {
				Object value = entry.getValue();
				if (!(value instanceof File)) {
					postBody += entry.getKey() + "=" + value + "&";
				}
			}
			postBody = postBody.substring(0, postBody.length() - 1);
			String sign = method + scheme + "://" + host + "/" + path + postBody + tag;
			if (LogManager.debug) {
				LogManager.d("签名", EncryptManager.MD5StrLower32(sign) + "\n" + sign);
			}
			params.put("sign", EncryptManager.MD5StrLower32(sign));
		}
		return params;
	}

	@CheckResult
	public Call get(final Callback callback) {
		method = GET;
		Result result = Result.newInstance();
		OkHttpClient client = new OkHttpClient.Builder()
				.connectTimeout(30, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.writeTimeout(30, TimeUnit.SECONDS)
				.cookieJar(new CookiesManager(context))
				.build();
		final Call call = client
				.newCall(new Request.Builder()
						.cacheControl(new CacheControl.Builder().noCache().build())
						.url(getUrlBuilder().build())
						.build());
		try {
			if (hasNetwork()) {
				result.hasNetwork(true);
				if (isCache) {
					byte[] bytes = getCache();
					if (bytes != null && bytes.length > 0) {
						String string = result.bytes(bytes, true).string(true);
						if (LogManager.debug) LogManager.d("Get缓存", string);
						if (callback != null) callback.success(result);
					} else {
						if (LogManager.debug) LogManager.d("Get缓存", "空的");
						if (callback != null) callback.success(result.bytes(null, false));
					}
				}
				if (isSync) {// 同步
					Response response = call.execute();
					if (response.isSuccessful()) {
						byte[] bytes = response.body().bytes();
						if (bytes != null && bytes.length > 0) {
							String string = result.bytes(bytes, false).string(false);
							if (LogManager.debug) LogManager.d("Get实时", string);
							if (callback != null) callback.success(result);
							if (isCache) setCache(bytes);
						}
					} else {
						result.bytes(null, false);
						if (callback != null) callback.failure(result);
					}
				} else {
					call.enqueue(callback(callback, result));
				}
			} else {
				result.hasNetwork(false);
				if (isCache) {
					byte[] bytes = getCache();
					if (bytes != null && bytes.length > 0) {
						String string = result.bytes(bytes, true).string(true);
						if (LogManager.debug) LogManager.d("Get缓存 无网络", string);
						if (callback != null) callback.failure(result);
					} else {
						if (LogManager.debug) LogManager.d("Get缓存 无网络", "空的");
						if (callback != null) callback.failure(result.bytes(null, false));
					}
				} else {
					if (callback != null) callback.failure(result.bytes(null, false));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return call;
	}

	@CheckResult
	public Call post(Callback callback) {
		method = POST;
		Result result = Result.newInstance();
		OkHttpClient client = new OkHttpClient.Builder()
				.connectTimeout(30, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.writeTimeout(30, TimeUnit.SECONDS)
				.cookieJar(new CookiesManager(context))
				.build();
		Call call = client
				.newCall(new Request.Builder()
						.cacheControl(new CacheControl.Builder().noCache().build())
						.url(postUrlBuilder().build())
						.post(postRequestBody())
						.build());
		try {
			if (hasNetwork()) {
				result.hasNetwork(true);
				if (isSync) {// 同步
					Response response = call.execute();
					if (response.isSuccessful()) {
						byte[] bytes = response.body().bytes();
						if (bytes != null && bytes.length > 0) {
							String string = result.bytes(bytes, false).string(false);
							if (LogManager.debug) LogManager.d(method, string);
							if (callback != null) callback.success(result);
						}
					} else {
						result.bytes(null, false);
						if (callback != null) callback.failure(result);
					}
				} else {
					call.enqueue(callback(callback, result));
				}
			} else {
				result.bytes(null, false).hasNetwork(false);
				if (callback != null) callback.failure(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return call;
	}

	private okhttp3.Callback callback(final Callback callback, @NonNull final Result result) {
		return new okhttp3.Callback() {
			@Override
			public void onFailure(Call call, IOException ioe) {
				try {
					result.call(call);
					if (LogManager.debug) {
						LogManager.d(method + " 地址", Thread.currentThread().getName());
						if (!TextUtils.isEmpty(method) && method.equals(POST)) {
							LogManager.d(method + " 参数", params.toString());
						}
					}
					if (isChild) {
						if (callback != null) callback.failure(result);
					} else {
						handler.post(new Runnable() {
							@Override
							public void run() {
								try {
									if (callback != null) callback.failure(result);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				try {
					result.call(call);
					if (LogManager.debug) {
						LogManager.d(method + " 地址", Thread.currentThread().getName());
						if (!TextUtils.isEmpty(method) && method.equals(POST)) {
							LogManager.d(method + " 参数", params.toString());
						}
					}
					if (response.isSuccessful()) {
						byte[] bytes = response.body().bytes();
						if (bytes != null && bytes.length > 0) {
							final String string = result.bytes(bytes, false).string(false);
							if (isChild) {
								try {
									if (LogManager.debug) LogManager.d(method, string);
									if (callback != null) callback.success(result);
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								handler.post(new Runnable() {
									@Override
									public void run() {
										try {
											if (LogManager.debug) {
												LogManager.d(method, string);
											}
											if (callback != null) callback.success(result);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
							}
						}
						if (isCache) setCache(bytes);
					} else {
						result.bytes(null, false);
						if (isChild) {
							if (callback != null) callback.failure(result);
						} else {
							handler.post(new Runnable() {
								@Override
								public void run() {
									try {
										if (callback != null) callback.failure(result);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	private boolean setCache(byte[] bytes) {
		String fileName = EncryptManager.MD5StrLower16(domain + path + paramsSign().toString());
		return FileManager.writeByteFile(new File(context.getCacheDir(), fileName), bytes);
	}

	@CheckResult
	private byte[] getCache() {
		String fileName = EncryptManager.MD5StrLower16(domain + path + params.toString());
		File file = new File(context.getCacheDir(), fileName);
		if (file.exists() && file.length() > 0) {
			return FileManager.readByteFile(file);
		}
		return null;
	}

	@CheckResult
	public static Call download(String url, final File file, @Nullable final OnProgressListener onProgressListener) {
		OkHttpClient client = new OkHttpClient();
//		client.setCookieHandler(new CookieManager(new PersistentCookieStore(context.getApplicationContext()), CookiePolicy.ACCEPT_ALL));
		client.networkInterceptors().add(new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				Response originalResponse = chain.proceed(chain.request());
				return originalResponse.newBuilder()
						.body(new ProgressResponseBody(originalResponse.body(), onProgressListener))
						.build();
			}
		});
		Call call = client.newCall(new Request.Builder().url(url).build());
		call.enqueue(new okhttp3.Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				if (onProgressListener != null) onProgressListener.failure();
			}

			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				FileManager.writeByteFile(file, response.body().bytes());
				if (onProgressListener != null) {
					if (response.isSuccessful()) {
						onProgressListener.success();
					} else {
						onProgressListener.failure();
					}
				}
			}
		});
		return call;
	}

	private static class ProgressResponseBody extends ResponseBody {

		private final ResponseBody responseBody;
		private final OnProgressListener onProgressListener;
		private BufferedSource bufferedSource;

		public ProgressResponseBody(ResponseBody responseBody, OnProgressListener onProgressListener) {
			this.responseBody = responseBody;
			this.onProgressListener = onProgressListener;
		}

		@Override
		public MediaType contentType() {
			return responseBody.contentType();
		}

		@Override
		public long contentLength() {
			return responseBody.contentLength();
		}

		@Override
		public BufferedSource source() {
			if (bufferedSource == null) {
				bufferedSource = Okio.buffer(source(responseBody.source()));
			}
			return bufferedSource;
		}

		private Source source(Source source) {
			return new ForwardingSource(source) {
				long totalBytesRead = 0L;

				@Override
				public long read(Buffer sink, long byteCount) throws IOException {
					long bytesRead = super.read(sink, byteCount);
					// read() returns the number of bytes read, or -1 if this source is exhausted.
					totalBytesRead += bytesRead != -1 ? bytesRead : 0;
					onProgressListener.update(totalBytesRead, responseBody.contentLength());
					return bytesRead;
				}


			};
		}
	}

	public static class Result {

		private Call call;
		private boolean hasNetwork;
		private byte[] bytes;
		private byte[] cacheBytes;

		private Result() {
		}

		private void call(Call call) {
			this.call = call;
		}

		public Call call() {
			return this.call;
		}

		private static Result newInstance() {
			return new Result();
		}

		private Result bytes(byte[] bytes, boolean isCache) {
			if (isCache) {
				this.cacheBytes = bytes;
			} else {
				this.bytes = bytes;
			}
			return this;
		}

		@Nullable
		public byte[] bytes(boolean isCache) {
			if (isCache) {
				return this.cacheBytes;
			} else {
				return this.bytes;
			}
		}

		@Nullable
		public String string(boolean isCache) {
			try {
				if (isCache) {
					return new String(cacheBytes, "UTF-8");
				} else {
					return new String(bytes, "UTF-8");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Nullable
		public JSONObject jsonObject(boolean isCache) {
			String string = string(isCache);
			if (!TextUtils.isEmpty(string)) {
				try {
					return new JSONObject(string);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Nullable
		public JSONArray jsonArray(boolean isCache) {
			String string = string(isCache);
			if (!TextUtils.isEmpty(string)) {
				try {
					return new JSONArray(string);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		private void hasNetwork(boolean hasNetwork) {
			this.hasNetwork = hasNetwork;
		}

		public boolean hasNetwork() {
			return hasNetwork;
		}
	}

	public static void bitmap(String url, ImageView imageView, int defResId) {
		if (!TextUtils.isEmpty(url)) {
			DrawableRequestBuilder builder = Glide.with(imageView.getContext())
					.load(url)
					.diskCacheStrategy(DiskCacheStrategy.ALL);
			if (defResId >= 0) builder.placeholder(defResId);
			builder.into(imageView);
		}
	}

	public static void roundBitmap(String url, ImageView imageView, int defResId) {
		if (!TextUtils.isEmpty(url)) {
			DrawableRequestBuilder builder = Glide.with(imageView.getContext())
					.load(url)
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.transform(new RoundTransformation(imageView.getContext()));
			if (defResId >= 0) builder.placeholder(defResId);
			builder.into(imageView);
		}
	}

	public static class RoundTransformation extends BitmapTransformation {

		Context context;

		public RoundTransformation(Context context) {
			super(context);
			this.context = context;
		}

		@Override
		protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
			return roundCrop(pool, toTransform);
		}

		private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
			if (source == null) return null;
			int width = source.getWidth();
			int height = source.getHeight();
			int min = Math.min(width, height);
			Bitmap result = pool.get(min, min, Bitmap.Config.ARGB_8888);
			if (result == null) {
				result = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
			}
			Canvas canvas = new Canvas(result);
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			canvas.drawCircle(min / 2.0f, min / 2.0f, min / 2.0f, paint);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			Rect srcRect;
			int offset = (int) Math.abs((width - height) / 2.0f);
			if (width > height) {
				srcRect = new Rect(offset, 0, offset + min, min);
			} else {
				srcRect = new Rect(0, offset, min, offset + min);
			}
			Rect desRect = new Rect(0, 0, min, min);
			canvas.drawBitmap(source, srcRect, desRect, paint);
			return result;
		}

		@Override
		public String getId() {
			return getClass().getName() + Math.random();
		}
	}

	@CheckResult
	private HttpUrl.Builder getUrlBuilder() {
		HttpUrl.Builder builder = postUrlBuilder();
		paramsSign();
		for (Map.Entry entry : params.entrySet()) {
			builder.addQueryParameter(entry.getKey().toString(), entry.getValue().toString());
		}
		return builder;
	}

	@CheckResult
	private HttpUrl.Builder postUrlBuilder() {
		HttpUrl.Builder builder = new HttpUrl.Builder();
		parseDomain(builder);
		parsePath(builder);
		return builder;
	}

	@CheckResult
	private RequestBody postRequestBody() {
		paramsSign();
		MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
		for (Map.Entry entry : params.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof File) {
				File file = (File) value;
				multipartBuilder.addFormDataPart(entry.getKey().toString(), file.getName(), RequestBody.create(MediaType.parse("multipart/form-data; charset=utf-8"), file));
			} else {
				multipartBuilder.addFormDataPart(entry.getKey().toString(), null, RequestBody.create(MediaType.parse("multipart/form-data; charset=utf-8"), value.toString()));
			}
		}
		return multipartBuilder.setType(MultipartBody.FORM).build();
	}

	private void parseDomain(@NonNull HttpUrl.Builder builder) {
		scheme = domain.startsWith("http://") ? "http" : domain.startsWith("https") ? "https" : "";
		host = domain.replace(scheme + "://", "");
		int index = -1;
		if ((index = host.indexOf(":")) >= 0) {
			port = Integer.parseInt(host.substring(index + 1, host.length()));
			host = host.substring(0, index);
		}
		builder.scheme(scheme).host(host).port(port);
	}

	private void parsePath(@NonNull HttpUrl.Builder builder) {
		if (!TextUtils.isEmpty(path)) {
			String[] paths = path.split("/");
			for (String pa : paths) {
				builder.addPathSegment(pa);
			}
		}
	}

	public static String parseUnicode(String line) {
		int len = line.length();
		char[] out = new char[len];//保存解析以后的结果
		int outLen = 0;
		for (int i = 0; i < len; i++) {
			char aChar = line.charAt(i);
			if (aChar == '\\') {
				aChar = line.charAt(++i);
				if (aChar == 'u') {
					int value = 0;
					for (int j = 0; j < 4; j++) {
						aChar = line.charAt(++i);
						switch (aChar) {
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								value = (value << 4) + aChar - '0';
								break;
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
								value = (value << 4) + 10 + aChar - 'a';
								break;
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
								value = (value << 4) + 10 + aChar - 'A';
								break;
						}
					}
					out[outLen++] = (char) value;
				} else {
					if (aChar == 't') aChar = '\t';
					else if (aChar == 'r') aChar = '\r';
					else if (aChar == 'n') aChar = '\n';
					else if (aChar == 'f') aChar = '\f';
					out[outLen++] = aChar;
				}
			} else {
				out[outLen++] = aChar;
			}
		}
		return new String(out, 0, outLen);
	}

	public interface OnProgressListener {
		void failure();

		void success();

		void update(long bytesWritten, long totalSize);
	}

	public interface Callback {
		void failure(Result result) throws Exception;

		void success(Result result) throws Exception;
	}
}
