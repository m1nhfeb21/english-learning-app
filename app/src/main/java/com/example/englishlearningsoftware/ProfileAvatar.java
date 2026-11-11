package com.example.englishlearningsoftware;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public final class ProfileAvatar {
    private static final int MAX_EDGE = 384;
    private static final ExecutorService IMAGES = Executors.newSingleThreadExecutor();

    private ProfileAvatar() {}

    public static void bind(AppCompatActivity activity, Intent intent,
                            ImageView photo, TextView initials) {
        String name = ProfileStore.getName(activity, intent).trim();
        initials.setText(name.isEmpty() ? "L"
                : new String(Character.toChars(name.codePointAt(0))).toUpperCase(Locale.ROOT));
        initials.setVisibility(View.VISIBLE);
        photo.setImageDrawable(null);
        photo.setVisibility(View.GONE);
        Uri uri = ProfileStore.getAvatarUri(activity, intent);
        read(activity, uri, photo, bitmap -> {
            if (bitmap != null) show(photo, initials, bitmap);
        });
    }

    public static void select(AppCompatActivity activity, Intent intent, Uri uri,
                              ImageView photo, TextView initials, Runnable onFinished) {
        if (!"content".equals(uri.getScheme())) {
            showError(activity);
            onFinished.run();
            return;
        }
        try {
            activity.getContentResolver().takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException exception) {
            showError(activity);
            onFinished.run();
            return;
        }
        read(activity, uri, photo, bitmap -> {
            if (bitmap == null) {
                showError(activity);
                bind(activity, intent, photo, initials);
                onFinished.run();
                return;
            }
            ProfileStore.setAvatarUri(activity, intent, uri);
            show(photo, initials, bitmap);
            onFinished.run();
        });
    }

    private static void show(ImageView photo, TextView initials, Bitmap bitmap) {
        photo.setImageBitmap(bitmap);
        photo.setVisibility(View.VISIBLE);
        initials.setVisibility(View.INVISIBLE);
    }

    private static void showError(AppCompatActivity activity) {
        Toast.makeText(activity, "Không đọc được ảnh. Vui lòng chọn ảnh khác.",
                Toast.LENGTH_SHORT).show();
    }

    private static void read(AppCompatActivity activity, Uri uri, ImageView photo,
                             Consumer<Bitmap> result) {
        Object request = new Object();
        photo.setTag(request);
        if (uri == null) return;
        ContentResolver resolver = activity.getApplicationContext().getContentResolver();
        // Decode off the UI thread and keep bitmaps bounded.
        IMAGES.execute(() -> {
            Bitmap bitmap = null;
            try {
                bitmap = decode(resolver, uri);
            } catch (IOException | RuntimeException | OutOfMemoryError ignored) {

            }
            Bitmap decoded = bitmap;
            activity.runOnUiThread(() -> {
                if (activity.isDestroyed() || photo.getTag() != request) return;
                result.accept(decoded);
            });
        });
    }

    static Bitmap decode(ContentResolver resolver, Uri uri) throws IOException {
        if (Build.VERSION.SDK_INT >= 28) {
            return ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, uri),
                    (decoder, info, source) -> {
                        int width = info.getSize().getWidth();
                        int height = info.getSize().getHeight();
                        double scale = Math.max(1.0, Math.max(width, height) / (double) MAX_EDGE);
                        decoder.setTargetSize(Math.max(1, (int) (width / scale)),
                                Math.max(1, (int) (height / scale)));
                        decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE);
                    });
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try (InputStream stream = resolver.openInputStream(uri)) {
            BitmapFactory.decodeStream(stream, null, options);
        }
        if (options.outWidth <= 0 || options.outHeight <= 0) return null;
        options.inSampleSize = 1;
        while (Math.max(options.outWidth, options.outHeight)
                / options.inSampleSize > MAX_EDGE) options.inSampleSize *= 2;
        options.inJustDecodeBounds = false;
        Bitmap bitmap;
        try (InputStream stream = resolver.openInputStream(uri)) {
            bitmap = BitmapFactory.decodeStream(stream, null, options);
        }
        if (bitmap == null) return null;
        try (InputStream stream = resolver.openInputStream(uri)) {
            int orientation = new ExifInterface(stream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL: matrix.setScale(-1, 1); break;
                case ExifInterface.ORIENTATION_ROTATE_180: matrix.setRotate(180); break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL: matrix.setScale(1, -1); break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90); matrix.postScale(-1, 1); break;
                case ExifInterface.ORIENTATION_ROTATE_90: matrix.setRotate(90); break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(270); matrix.postScale(-1, 1); break;
                case ExifInterface.ORIENTATION_ROTATE_270: matrix.setRotate(270); break;
                default: return bitmap;
            }
            Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (oriented != bitmap) bitmap.recycle();
            return oriented;
        } catch (IOException ignored) {
            return bitmap;
        }
    }
}
