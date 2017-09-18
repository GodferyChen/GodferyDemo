package a.b.c.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import a.b.c.R;

public class AvatarActivity extends BaseActivity {

    protected static final int REQ_GALLERY = 1;
    protected static final int REQ_CAMERA = 2;
    protected static final int REQ_CROP = 3;

    protected static final int CROP_NONE = 11;
    protected static final int CROP_SQUARE = 12;
    protected static final int CROP_ROUND = 13;

    private String fileName = System.currentTimeMillis() + ".jpg";
    private ArrayList<File> files = new ArrayList<>();
    protected int cropType = CROP_SQUARE;
    protected ImageView ivAvatar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final File file = new File(Environment.getExternalStorageDirectory(), fileName);
        if (file.exists()) file.delete();
    }

    protected void showChooseDialog(String title) {
        new AlertDialog.Builder(this).setTitle(title)
                .setItems(R.array.modify_avatar_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                fromGallery();
                                break;
                            case 1:
                                takePhoto();
                                break;
                        }
                    }
                }).create().show();
    }

    protected void fromGallery() {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, REQ_GALLERY);
    }

    protected void takePhoto() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            fileName = System.currentTimeMillis() + ".jpg";
            camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(), fileName)));
        }
        startActivityForResult(camera, REQ_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQ_GALLERY:
                    if (data == null) return;
                    Uri uri = data.getData();
                    String path = getAbsolutePathFromNoStandardUri(uri);
                    if (TextUtils.isEmpty(path)) path = getAbsoluteImagePath(uri);
                    if (TextUtils.isEmpty(path)) path = getPath(uri);
                    if (TextUtils.isEmpty(path)) return;
                    Uri resultUri = queryUriForImage(path);
                    if (cropType != CROP_NONE) {
                        cropBitmap(resultUri);
                    } else {
                        if (null != ivAvatar) {
                            ivAvatar.setImageBitmap(decodeUriAsBitmap(this, resultUri));
                        }
                        files.add(new File(resultUri.getPath()));
                    }
                    break;
                case REQ_CAMERA:
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                        return;
                    File file = new File(Environment.getExternalStorageDirectory(), fileName);
                    if (!file.exists() || file.length() <= 0) return;
                    if (cropType != CROP_NONE) {
                        cropBitmap(Uri.fromFile(file));
                    } else {
                        if (null != ivAvatar) {
                            ivAvatar.setImageBitmap(decodeUriAsBitmap(this, Uri.fromFile(file)));
                        }
                        files.add(file);
                    }
                    break;
                case REQ_CROP:
                    if (data == null) return;
                    write2File(data);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void cropBitmap(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQ_CROP);
    }

    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    private void write2File(Intent data) {
        Bundle extras = data.getExtras();
        if (extras == null) return;
        Bitmap resultBitmap = extras.getParcelable("data");
        if (resultBitmap == null) return;
        if (cropType == CROP_ROUND) {
            resultBitmap = roundCrop(resultBitmap);
        }
        if (null != ivAvatar) {
            ivAvatar.setImageBitmap(resultBitmap);
        }
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                fileName = System.currentTimeMillis() + ".jpg";
                File file = new File(Environment.getExternalStorageDirectory(), fileName);
                FileOutputStream outputStream = new FileOutputStream(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                resultBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                outputStream.write(byteArrayOutputStream.toByteArray());
                outputStream.close();
                files.add(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static Bitmap drawableToBitamp(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    private Bitmap roundCrop(Bitmap source) {
        if (source == null) return null;
        int width = source.getWidth();
        int height = source.getHeight();
        int min = Math.min(width, height);
        Bitmap result = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawCircle((float) min / 2.0F, (float) min / 2.0F, (float) min / 2.0F, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        int offset = (int) Math.abs((float) (width - height) / 2.0F);
        Rect srcRect;
        if (width > height) {
            srcRect = new Rect(offset, 0, offset + min, min);
        } else {
            srcRect = new Rect(0, offset, min, offset + min);
        }
        Rect desRect = new Rect(0, 0, min, min);
        canvas.drawBitmap(source, srcRect, desRect, paint);
        return result;
    }

    private Uri queryUriForImage(String path) {
        Uri contentUri = Uri.parse("content://media/external/images/media");
        Uri uri = null;
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                if (path.equals(data)) {
                    int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
                    uri = Uri.withAppendedPath(contentUri, "" + id);
                    break;
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return uri;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getPath(final Uri uri) {
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String authority = uri.getAuthority();
        if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {// DocumentProvider
            if (authority.equals("com.android.externalstorage.documents")) {//
                // ExternalStorageProvider
                String id = DocumentsContract.getDocumentId(uri);
                String[] splits = id.split(":");
                String type = splits[0];
                if (type.equalsIgnoreCase("primary")) {
                    return Environment.getExternalStorageDirectory() + "/" + splits[1];
                }
            } else if (authority.equals("com.android.providers.downloads.documents")) {//
                // DownloadsProvider
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse
                        ("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(contentUri, null, null);
            } else if (authority.equals("com.android.providers.media.documents")) {// MediaProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final String[] splits = id.split(":");
                final String type = splits[0];
                Uri contentUri = null;
                if (type.equalsIgnoreCase("image")) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if (type.equalsIgnoreCase("video")) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if (type.equalsIgnoreCase("audio")) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = "_id=?";
                String[] selectionArgs = new String[]{splits[1]};
                return getDataColumn(contentUri, selection, selectionArgs);
            }
        } else if (uri.getScheme().equalsIgnoreCase("content")) {// MediaStore (and general)
            if (authority.equals("com.google.android.apps.photos.content"))
                return uri.getLastPathSegment();// Return the remote address
            return getDataColumn(uri, null, null);
        } else if (uri.getScheme().equalsIgnoreCase("file")) {// File
            return uri.getPath();
        }
        return null;
    }

    private String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = {column};
        try {
            cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private String getAbsoluteImagePath(Uri uri) {
        String path = "";
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                path = cursor.getString(columnIndex);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return path;
    }

    private String getAbsolutePathFromNoStandardUri(Uri uri) {
        String pre1 = "file://" + Environment.getExternalStorageDirectory().getPath() + File
                .separator;
        String pre2 = "file://" + "/sdcard" + File.separator;
        String pre3 = "file://" + "/mnt/sdcard" + File.separator;
        String path = "";
        String uriStr = uri.toString();
        uriStr = Uri.decode(uriStr);
        if (uriStr.startsWith(pre1)) {
            path = Environment.getExternalStorageDirectory().getPath() + File.separator + uriStr
                    .substring(pre1.length());
        } else if (uriStr.startsWith(pre2)) {
            path = Environment.getExternalStorageDirectory().getPath() + File.separator + uriStr
                    .substring(pre2.length());
        } else if (uriStr.startsWith(pre3)) {
            path = Environment.getExternalStorageDirectory().getPath() + File.separator + uriStr
                    .substring(pre3.length());
        }
        return path;
    }
}
