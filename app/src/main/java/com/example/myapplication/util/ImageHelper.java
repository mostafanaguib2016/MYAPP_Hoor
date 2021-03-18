package com.example.myapplication.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.myapplication.R;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;


/**
 * <h1>ImageHelper for images operation</h1>
 * ImageHelper class for get image from camera , gallery and compress image
 * <p>
 *
 * @author kemo94
 * @version 1.0
 * @since 2017-08-9
 */
public abstract class ImageHelper {

    public static final int FROM_GALLERY = 1;
    public static final int FROM_CAMERA = 2;
    public static final int CROP_IMAGE = 3;

    static int fileNumber = 0;
    public static Uri outputFileUri;
    public static final String DOCUMENTS_DIR = "documents";
    // configured android:authorities in AndroidManifest (https://developer.android.com/reference/android/support/v4/content/FileProvider)
    public static final String AUTHORITY = "YOUR_AUTHORITY.provider";
    public static final String HIDDEN_PREFIX = ".";
    /**
     * TAG for log messages.
     */

    static final String TAG = "FileUtils";
    private static final boolean DEBUG = false; // Set to true to enabl

    public interface UploadListener {
        void onUploaded(String url);
    }

    class ImageUploadResponse {

        @SerializedName("data")
        @Expose
        private ArrayList<String> imageUrls;

        public ArrayList<String> getImageUrls() {
            return imageUrls;
        }
    }

    public static void imageDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choosing_image);
        RelativeLayout cameraLayout = dialog.findViewById(R.id.camera_layout);
        RelativeLayout galleryLayout = dialog.findViewById(R.id.gallery_layout);
        cameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity != null)
                    ImageHelper.captureImage(activity);
                dialog.dismiss();
            }
        });

        galleryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity != null)
                    ImageHelper.pickFromGallery(activity);
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }


    private static Bitmap resizedImage(Activity activity, Uri uri, String Path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(Path, options);
        if (options.outHeight == 0) {
            try {
                InputStream in = activity.getContentResolver().openInputStream(
                        uri);
                BitmapFactory.decodeStream(in, null, options);
            } catch (FileNotFoundException e) {
                // do something
            }
        }
        float maxWidth = 500;
        float maxHeight = 500;
        float actualWidth = options.outWidth;
        float actualHeight = options.outHeight;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                //adjust width according to maxHeight
                imgRatio = maxHeight / actualHeight;
                actualWidth = imgRatio * actualWidth;
                actualHeight = maxHeight;
            } else if (imgRatio > maxRatio) {
                //adjust height according to maxWidth
                imgRatio = maxWidth / actualWidth;
                actualHeight = imgRatio * actualHeight;
                actualWidth = maxWidth;
            } else {
                actualHeight = maxHeight;
                actualWidth = maxWidth;
            }
        }
        try {
            return Picasso.get().load(uri).resize((int) actualWidth, (int) actualHeight)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void pickFromGallery(Activity activity) {
        if (externalStoragePermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, FROM_GALLERY)) {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

            }
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("scale", true);
            intent.putExtra("outputX", 256);
            intent.putExtra("outputY", 256);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("return-data", true);
            activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), FROM_GALLERY);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }


//        if (externalStoragePermission(activity,Manifest.permission.READ_EXTERNAL_STORAGE, FROM_GALLERY)) {
//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//            intent.setType("image/*");
//            intent.putExtra("crop", "true");
//            intent.putExtra("scale", true);
//            intent.putExtra("outputX", 256);
//            intent.putExtra("outputY", 256);
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
//            intent.putExtra("return-data", true);
//            activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), FROM_GALLERY);
//        } else {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
//        }

    }

    private static boolean externalStoragePermission(Activity activity, String manifest, int permission) {
        if (ContextCompat.checkSelfPermission(activity,
                manifest)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{manifest},
                    permission);

            return false;

        } else {
            return true;
        }
    }

    private static boolean cameraPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return false;

        } else {
            return true;
        }
    }

    private static String getRealPath(Intent data, Activity activity) {
        String realPath;
        if (Build.VERSION.SDK_INT < 11)
            realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(activity, data.getData());
        else if (Build.VERSION.SDK_INT < 19)
            realPath = RealPathUtil.getRealPathFromURI_API11to18(activity, data.getData());
        else
            realPath = RealPathUtil.getRealPathFromURI_API19(activity, data.getData());
        return realPath;
    }


    public static void captureImage(Activity activity) {
        if (handleCameraPermissions(activity)) {
            Random r = new Random();
            fileNumber = r.nextInt(999999 - 1) + 1;
            File file = new File(Environment.getExternalStorageDirectory(), fileNumber + "extreme.jpg");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                outputFileUri = Uri.fromFile(file);
            } else {
                outputFileUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.putExtra("return-data", true);
            // we will handle the returned data in onActivityResult

            activity.startActivityForResult(intent, FROM_CAMERA);
        }
    }

    public static void performCrop(Activity activity, Uri picUri) {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 2);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            activity.startActivityForResult(cropIntent, CROP_IMAGE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(activity, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static Bitmap resizedGalleryImage(Intent data, Activity activity) {
        String realPath;
        try {
            realPath = getRealPath(data, activity);
        } catch (Exception e) {
            realPath = GetFilePathFromDevice.getPath(activity, data.getData());
        }
        Uri uri = Uri.fromFile(new File(realPath));
        return resizedImage(activity, uri, realPath);
    }

    private static Bitmap resizedCameraImage(Activity activity) {
        return resizedImage(activity, outputFileUri, outputFileUri.getPath());
    }

    private static class cameraAsync extends AsyncTask<Activity, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Activity... params) {
            return resizedCameraImage(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
//            EventBus.getDefault().post(new ImageModel(bitmap));
        }
    }

    private static class galleryAsync extends AsyncTask<galleryAsyncParams, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(galleryAsyncParams... params) {
            return resizedGalleryImage(params[0].data, params[0].activity);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
//            EventBus.getDefault().post(new ImageModel(bitmap));
        }
    }

    private static class galleryAsyncParams {
        Activity activity;
        Intent data;

        public galleryAsyncParams(Activity activity, Intent data) {
            this.activity = activity;
            this.data = data;
        }
    }

    public static void receiveGalleryImage(Activity activity, Intent data) {
        galleryAsync galleryAsync = new galleryAsync();
        galleryAsync.execute(new galleryAsyncParams(activity, data));
    }

    public static void receiveCameraImage(Activity activity) {
        cameraAsync cameraAsync = new cameraAsync();
        cameraAsync.execute(activity);
    }

    private static boolean handleCameraPermissions(Activity activity) {
        ArrayList<String> strings = new ArrayList<>();
        boolean flag = true;
        if (!cameraPermission(activity)) {
            strings.add(Manifest.permission.CAMERA);
            flag = false;
        }
        if (!externalStoragePermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, FROM_CAMERA)) {
            strings.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            flag = false;
        }
        if (!externalStoragePermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, FROM_CAMERA)) {
            strings.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            flag = false;
        }
        if (flag) {
            return true;
        } else {
            String permissions[] = new String[strings.size()];
            for (int i = 0; i < strings.size(); i++) {
                permissions[i] = strings.get(i);
            }
            ActivityCompat.requestPermissions(activity,
                    permissions,
                    123);
            return false;
        }
    }

    private static abstract class RealPathUtil {

        public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

        public static void showDialog(final String msg, final Context context,
                                      final String permission) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage(msg + " permission is necessary");
            alertBuilder.setPositiveButton(android.R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{permission},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }

        public static boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            (Activity) context,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showDialog("External storage", context,
                                Manifest.permission.READ_EXTERNAL_STORAGE);

                    } else {
                        ActivityCompat
                                .requestPermissions(
                                        (Activity) context,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    return false;
                } else {
                    return true;
                }

            } else {
                return true;
            }
        }

        @SuppressLint("NewApi")
        public static String getRealPathFromURI_API19(Context context, Uri uri) {
            if (checkPermissionREAD_EXTERNAL_STORAGE(context)) {

                String filePath = "";
                String wholeID = DocumentsContract.getDocumentId(uri);

                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];

                String[] column = {MediaStore.Images.Media.DATA};

                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
                return filePath;
            }
            return null;
        }


        @SuppressLint("NewApi")
        public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
            String[] proj = {MediaStore.Images.Media.DATA};
            String result = null;

            CursorLoader cursorLoader = new CursorLoader(
                    context,
                    contentUri, proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();

            if (cursor != null) {
                int column_index =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(column_index);
            }
            return result;
        }

        public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index
                    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }

    }

    public static class ImageModel {
        Bitmap bitmap;
        String imagePath;
        String imageUrl;

        public ImageModel(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public ImageModel(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }


        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getImageUrl() {
            if (imageUrl == null)
                return "";
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    private static class GetFilePathFromDevice {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public static String getPath(final Context context, final Uri uri) {
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);


                    if (id != null && id.startsWith("raw:")) {
                        return id.substring(4);
                    }

                    String[] contentUriPrefixesToTry = new String[]{
                            "content://downloads/public_downloads",
                            "content://downloads/my_downloads"
                    };

                    for (String contentUriPrefix : contentUriPrefixesToTry) {
                        Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));
                        try {
                            String path = getDataColumn(context, contentUri, null, null);
                            if (path != null) {
                                return path;
                            }
                        } catch (Exception e) {
                        }
                    }

                    // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                    String fileName = getFileName(context, uri);
                    File cacheDir = getDocumentCacheDir(context);
                    File file = generateFileName(fileName, cacheDir);
                    String destinationPath = null;
                    if (file != null) {
                        destinationPath = file.getAbsolutePath();
                        saveFileFromUri(context, uri, destinationPath);
                    }
                    return destinationPath;

//                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//                    return getDataColumn(context, contentUri, null, null);
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();
                return getDataColumn(context, uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        }

        public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {column};
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }

        private static void saveFileFromUri(Context context, Uri uri, String destinationPath) {
            InputStream is = null;
            BufferedOutputStream bos = null;
            try {
                is = context.getContentResolver().openInputStream(uri);
                bos = new BufferedOutputStream(new FileOutputStream(destinationPath, false));
                byte[] buf = new byte[1024];
                is.read(buf);
                do {
                    bos.write(buf);
                } while (is.read(buf) != -1);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) is.close();
                    if (bos != null) bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }

        public static boolean isGooglePhotosUri(Uri uri) {
            return "com.google.android.apps.photos.content".equals(uri.getAuthority());
        }

        @Nullable
        public static File generateFileName(@Nullable String name, File directory) {
            if (name == null) {
                return null;
            }

            File file = new File(directory, name);

            if (file.exists()) {
                String fileName = name;
                String extension = "";
                int dotIndex = name.lastIndexOf('.');
                if (dotIndex > 0) {
                    fileName = name.substring(0, dotIndex);
                    extension = name.substring(dotIndex);
                }

                int index = 0;

                while (file.exists()) {
                    index++;
                    name = fileName + '(' + index + ')' + extension;
                    file = new File(directory, name);
                }
            }

            try {
                if (!file.createNewFile()) {
                    return null;
                }
            } catch (IOException e) {
                Log.w(TAG, e);
                return null;
            }

            logDir(directory);

            return file;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public static String getFileName(@NonNull Context context, Uri uri) {
            String mimeType = context.getContentResolver().getType(uri);
            String filename = null;

            if (mimeType == null && context != null) {
                String path = getPath(context, uri);
                if (path == null) {
                    filename = getName(uri.toString());
                } else {
                    File file = new File(path);
                    filename = file.getName();
                }
            } else {
                Cursor returnCursor = context.getContentResolver().query(uri, null,
                        null, null, null);
                if (returnCursor != null) {
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    returnCursor.moveToFirst();
                    filename = returnCursor.getString(nameIndex);
                    returnCursor.close();
                }
            }

            return filename;
        }

        public static File getDocumentCacheDir(@NonNull Context context) {
            File dir = new File(context.getCacheDir(), DOCUMENTS_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            logDir(context.getCacheDir());
            logDir(dir);

            return dir;
        }

        private static void logDir(File dir) {
            if (!DEBUG) return;
            Log.d(TAG, "Dir=" + dir);
            File[] files = dir.listFiles();
            for (File file : files) {
                Log.d(TAG, "File=" + file.getPath());
            }
        }

        public static String getName(String filename) {
            if (filename == null) {
                return null;
            }
            int index = filename.lastIndexOf('/');
            return filename.substring(index + 1);
        }
    }
}