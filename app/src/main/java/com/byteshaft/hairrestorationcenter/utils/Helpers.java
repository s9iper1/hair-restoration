package com.byteshaft.hairrestorationcenter.utils;

import android.content.SharedPreferences;

/**
 * Created by s9iper1 on 8/11/16.
 */
public class Helpers {

    public static void setIsLoggedIn(boolean loggedIn) {
        SharedPreferences preferences = AppGlobals.getPreferences();
        preferences.edit().putBoolean("CONFIG_KEY_FIRST_RUN",  loggedIn).apply();
    }

    public static boolean isLoggedIn() {
        SharedPreferences preferences = AppGlobals.getPreferences();
        return preferences.getBoolean("CONFIG_KEY_FIRST_RUN", false);

    }
}
