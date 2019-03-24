package com.fyd.miku.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionHelper {
    private static final int WRITE_SDCARD_PERMISSION_CODE = 0;
    private static final String WRITE_SDCARD_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    /** Check to see we have the necessary permissions for this app. */
    public static boolean hasSdcardPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, WRITE_SDCARD_PERMISSION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /** Check to see we have the necessary permissions for this app, and ask for them if we don't. */
    public static void requestSdcardPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity, new String[] {WRITE_SDCARD_PERMISSION}, WRITE_SDCARD_PERMISSION_CODE);
    }

    /** Check to see if we need to show the rationale for this permission. */
    public static boolean shouldShowRequestPermissionRationale(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, WRITE_SDCARD_PERMISSION);
    }

    /** Launch Application Setting to grant permission. */
    public static void launchPermissionSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }
}
