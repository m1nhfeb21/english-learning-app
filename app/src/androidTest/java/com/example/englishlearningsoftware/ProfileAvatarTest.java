package com.example.englishlearningsoftware;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ProfileAvatarTest {
    @Test
    public void avatarPersistsWithoutLeakingToAnotherAccountOrPreview() {
        Context context = ApplicationProvider.getApplicationContext();
        Intent account = new Intent().putExtra(HomeActivity.EXTRA_ACCOUNT_ID, 2147483646);
        Intent other = new Intent().putExtra(HomeActivity.EXTRA_ACCOUNT_ID, 2147483645);
        Intent preview = new Intent().putExtra(HomeActivity.EXTRA_PREVIEW, true);
        String previewBefore = ProfileStore.forIntent(context, preview).getString("avatar_uri", null);
        ProfileStore.forIntent(context, account).edit().clear().commit();
        ProfileStore.forIntent(context, other).edit().clear().commit();
        try {
            Uri selected = Uri.parse("content://avatar-test/photo");
            ProfileStore.setAvatarUri(context, account, selected);
            Intent relaunched = new Intent().putExtra(HomeActivity.EXTRA_ACCOUNT_ID, 2147483646);
            assertEquals(selected, ProfileStore.getAvatarUri(context, relaunched));
            assertNull(ProfileStore.getAvatarUri(context, other));
            assertEquals(previewBefore,
                    ProfileStore.forIntent(context, preview).getString("avatar_uri", null));
        } finally {
            ProfileStore.forIntent(context, account).edit().clear().commit();
            ProfileStore.forIntent(context, other).edit().clear().commit();
        }
    }

    @Test
    public void largePhotoDecodesAtAvatarSize() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        File image = File.createTempFile("avatar-test-", ".png", context.getCacheDir());
        Bitmap source = Bitmap.createBitmap(1600, 900, Bitmap.Config.RGB_565);
        try {
            try (FileOutputStream stream = new FileOutputStream(image)) {
                assertTrue(source.compress(Bitmap.CompressFormat.PNG, 100, stream));
            }
            Bitmap decoded = ProfileAvatar.decode(context.getContentResolver(), Uri.fromFile(image));
            assertNotNull(decoded);
            assertTrue(Math.max(decoded.getWidth(), decoded.getHeight()) <= 384);
            decoded.recycle();
        } finally {
            source.recycle();
            image.delete();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void remoteImageUriIsRejected() {
        Context context = ApplicationProvider.getApplicationContext();
        ProfileStore.setAvatarUri(context, new Intent(), Uri.parse("https://example.com/photo"));
    }
}
