package a.b.c.manager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ImageChooseManager {

    public final static int REQ_PICK_PICTURE_FROM_GALLERY = 7458;
    public final static int REQ_TAKE_PICTURE = 7459;
    public final static int REQ_SOURCE_CHOOSER = 7460;

    public enum ImageSource {
        GALLERY, CAMERA
    }

    public interface Callbacks {
        void onImagePickerError(Exception e, ImageSource source);

        void onImagePicked(File imageFile, ImageSource source);

        void onCanceled(ImageSource source);
    }

    private static final String KEY_PHOTO_URI = "a.b.c.photo.uri";
    private static final String KEY_PHOTO_CAMERA_LAST = "a.b.c.photo.camera.last";
    private static String DEFAULT_FOLDER_NAME = "photo";

    private static File tempImageDirectory(Context context) {
        File dir = new File(context.getApplicationContext().getCacheDir(), getFolderName(context));
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private static File publicImageDirectory(Context context) {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getFolderName(context));
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }


    private static Intent createGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        return intent;
    }

    private static Uri createCameraPictureFile(Context context) throws IOException {
        File image = File.createTempFile(UUID.randomUUID().toString(), ".jpg", publicImageDirectory(context));
        Uri uri = Uri.fromFile(image);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(KEY_PHOTO_URI, uri.toString());
        editor.putString(KEY_PHOTO_CAMERA_LAST, uri.toString());
        editor.commit();
        return uri;
    }


    private static Intent createCameraIntent(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            Uri capturedImageUri = createCameraPictureFile(context);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return intent;
    }

    private static Intent createChooserIntent(Context context, String chooserTitle) throws IOException {
        Uri outputFileUri = createCameraPictureFile(context);
        List<Intent> cameraIntents = new ArrayList<>();
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> camList = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : camList) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        Intent galleryIntent = createGalleryIntent();

        Intent chooserIntent = Intent.createChooser(galleryIntent, chooserTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        return chooserIntent;
    }

    public static void openChooser(Activity activity, String chooserTitle) {
        try {
            Intent intent = createChooserIntent(activity, chooserTitle);
            activity.startActivityForResult(intent, REQ_SOURCE_CHOOSER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openChooser(Fragment fragment, String chooserTitle) {
        try {
            Intent intent = createChooserIntent(fragment.getActivity(), chooserTitle);
            fragment.startActivityForResult(intent, REQ_SOURCE_CHOOSER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openChooser(android.app.Fragment fragment, String chooserTitle) {
        try {
            Intent intent = createChooserIntent(fragment.getActivity(), chooserTitle);
            fragment.startActivityForResult(intent, REQ_SOURCE_CHOOSER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openGalleryPicker(Activity activity) {
        Intent intent = createGalleryIntent();
        activity.startActivityForResult(intent, REQ_PICK_PICTURE_FROM_GALLERY);
    }

    public static void openGalleryPicker(Fragment fragment) {
        Intent intent = createGalleryIntent();
        fragment.startActivityForResult(intent, REQ_PICK_PICTURE_FROM_GALLERY);
    }

    public static void openGalleryPicker(android.app.Fragment fragment) {
        Intent intent = createGalleryIntent();
        fragment.startActivityForResult(intent, REQ_PICK_PICTURE_FROM_GALLERY);
    }

    public static void openCamera(Activity activity) {
        Intent intent = createCameraIntent(activity);
        activity.startActivityForResult(intent, REQ_TAKE_PICTURE);
    }

    public static void openCamera(Fragment fragment) {
        Intent intent = createCameraIntent(fragment.getActivity());
        fragment.startActivityForResult(intent, REQ_TAKE_PICTURE);
    }

    public static void openCamera(android.app.Fragment fragment) {
        Intent intent = createCameraIntent(fragment.getActivity());
        fragment.startActivityForResult(intent, REQ_TAKE_PICTURE);
    }

    private static File pickedGalleryPicture(Context context, Uri photoPath) throws IOException {
        InputStream pictureInputStream = context.getContentResolver().openInputStream(photoPath);
        File directory = ImageChooseManager.tempImageDirectory(context);
        File photoFile = new File(directory, UUID.randomUUID().toString());
        photoFile.createNewFile();
        ImageChooseManager.writeToFile(pictureInputStream, photoFile);
        return photoFile;

    }

    private static File takenCameraPicture(Context context) throws IOException, URISyntaxException {
        URI imageUri = new URI(PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_PHOTO_URI, null));
        notifyGallery(context, imageUri);
        return new File(imageUri);
    }

    private static void writeToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void notifyGallery(Context context, URI pictureUri) throws URISyntaxException {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(pictureUri);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }


    public static void handleActivityResult(int requestCode, int resultCode, Intent data, Activity activity, Callbacks callbacks) {
        if (requestCode == REQ_SOURCE_CHOOSER || requestCode == REQ_PICK_PICTURE_FROM_GALLERY || requestCode == REQ_TAKE_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQ_PICK_PICTURE_FROM_GALLERY) {
                    onPictureReturnedFromGallery(data, activity, callbacks);
                } else if (requestCode == REQ_TAKE_PICTURE) {
                    onPictureReturnedFromCamera(activity, callbacks);
                } else if (data == null || data.getData() == null) {
                    onPictureReturnedFromCamera(activity, callbacks);
                } else {
                    onPictureReturnedFromGallery(data, activity, callbacks);
                }
            } else {
                if (requestCode == REQ_PICK_PICTURE_FROM_GALLERY) {
                    callbacks.onCanceled(ImageSource.GALLERY);
                } else if (requestCode == REQ_TAKE_PICTURE) {
                    callbacks.onCanceled(ImageSource.CAMERA);
                } else if (data == null || data.getData() == null) {
                    callbacks.onCanceled(ImageSource.CAMERA);
                } else {
                    callbacks.onCanceled(ImageSource.GALLERY);
                }
            }
        }
    }

    /**
     * @param context
     * @return File containing lastly taken (using camera) photo. Returns null if there was no photo taken or it doesn't exist anymore.
     */
    public static File lastlyTakenButCanceledPhoto(Context context) {
        String filePath = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_PHOTO_CAMERA_LAST, null);
        if (filePath == null) return null;
        File file = new File(filePath);
        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    private static void onPictureReturnedFromGallery(Intent data, Activity activity, Callbacks callbacks) {
        try {
            Uri photoPath = data.getData();
            File photoFile = ImageChooseManager.pickedGalleryPicture(activity, photoPath);
            callbacks.onImagePicked(photoFile, ImageSource.GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
            callbacks.onImagePickerError(e, ImageSource.GALLERY);
        }
    }

    private static void onPictureReturnedFromCamera(Activity activity, Callbacks callbacks) {
        try {
            File photoFile = ImageChooseManager.takenCameraPicture(activity);
            callbacks.onImagePicked(photoFile, ImageSource.CAMERA);
            PreferenceManager.getDefaultSharedPreferences(activity).edit().remove(KEY_PHOTO_CAMERA_LAST).commit();
        } catch (Exception e) {
            callbacks.onImagePickerError(e, ImageSource.CAMERA);
        }
    }

    private static String getFolderNameKey(Context context) {
        return context.getPackageName() + ".folder_name";
    }

    private static String getFolderName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(getFolderNameKey(context), DEFAULT_FOLDER_NAME);
    }

    public static Configuration configuration(Context context) {
        return new Configuration(context);
    }

    public static class Configuration {
        private Context context;

        private Configuration(Context context) {
            this.context = context;
        }

        public void setImagesFolderName(String folderName) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(getFolderNameKey(context), folderName).commit();
        }
    }

    public static class DefaultCallback implements ImageChooseManager.Callbacks {

        @Override
        public void onImagePickerError(Exception e, ImageChooseManager.ImageSource source) {}

        @Override
        public void onImagePicked(File imageFile, ImageChooseManager.ImageSource source) {}

        @Override
        public void onCanceled(ImageChooseManager.ImageSource source) {}
    }
}
