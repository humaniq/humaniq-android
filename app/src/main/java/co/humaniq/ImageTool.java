package co.humaniq;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.util.Base64;
import android.widget.ImageView;
import co.humaniq.views.ViewContext;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageTool {
    public static File createImageFile(final Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );
    }

    public interface LoadBitmapCallback {
        void onLoaded(Bitmap bitmap);
    }

    public static void loadFromUrlToImageView(ViewContext context, String url, ImageView imageView,
                                              @DrawableRes int errorRes)
    {
        if (url.isEmpty()) {
            imageView.setImageResource(errorRes);
            return;
        }

        Picasso.with(context.getInstance()).load(url).error(errorRes).into(imageView);
    }

    public static void loadFromUrlToImageView(ViewContext context, String url, ImageView imageView)
    {
        if (url.isEmpty())
            return;

        Picasso.with(context.getInstance()).load(url).into(imageView);
    }

    public static void loadFromUrlToBitmap(ViewContext context, String url,
                                           LoadBitmapCallback callback)
    {
        if (url.isEmpty()) {
            callback.onLoaded(null);
            return;
        }

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                callback.onLoaded(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                callback.onLoaded(null);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        Picasso.with(context.getInstance()).load(url).into(target);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight)
    {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight &&
                    (halfWidth / inSampleSize) > reqWidth)
            {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Bitmap decodeSampledBitmap(InputStream is, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = calculateInSampleSize (options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inDither = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return BitmapFactory.decodeStream(is);
    }

    public static Bitmap decodeSampledBitmap(String file, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
//        options.inDither = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return BitmapFactory.decodeFile(file, options);
    }

    public static Bitmap decodeSampledBitmap(Context context, Uri uri, int reqWidth, int reqHeight) throws FileNotFoundException {
        InputStream input = context.getContentResolver().openInputStream(uri);
        return decodeSampledBitmap(input, reqWidth, reqHeight);
    }

    public static Bitmap decodeSampledBitmap(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inDither = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static String encodeToBase64(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
