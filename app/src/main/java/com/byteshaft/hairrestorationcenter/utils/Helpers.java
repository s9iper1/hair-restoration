package com.byteshaft.hairrestorationcenter.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Environment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import java.io.File;

public class Helpers {

    public static void setIsLoggedIn(boolean loggedIn) {
        SharedPreferences preferences = AppGlobals.getPreferences();
        preferences.edit().putBoolean("CONFIG_KEY_FIRST_RUN",  loggedIn).apply();
    }

    public static boolean isLoggedIn() {
        SharedPreferences preferences = AppGlobals.getPreferences();
        return preferences.getBoolean("CONFIG_KEY_FIRST_RUN", false);

    }

    public static ProgressDialog getProgressDialog(Activity activity) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Processing...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static String createDirectoryAndSaveFile() {
        String internalFolder = Environment.getExternalStorageDirectory() +
                File.separator + "Android/data" + File.separator + AppGlobals.getContext().getPackageName();
        File file = new File(internalFolder);
        if (!file.exists()) {
            file.mkdirs();
        }
        internalFolder = internalFolder + File.separator + getTimeStamp() + ".jpg";
        return new File(internalFolder).getAbsolutePath();
    }

    public static String getTimeStamp() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h_mm_aa_dd_M_yyyy");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }
}
