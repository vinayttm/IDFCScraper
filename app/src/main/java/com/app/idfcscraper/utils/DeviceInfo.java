package com.app.idfcscraper.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class DeviceInfo {

    public static String getModelNumber() {
        return Build.MODEL;
    }

    public static String generateSecureId(Context context) {
        String secureId = "";
        if(context !=null)
        {
            secureId = getAndroidId(context);
        }
        return secureId;
    }

    private static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
