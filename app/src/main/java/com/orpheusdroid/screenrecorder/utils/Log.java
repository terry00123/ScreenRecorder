package com.orpheusdroid.screenrecorder.utils;

import com.orpheusdroid.screenrecorder.BuildConfig;
import com.orpheusdroid.screenrecorder.Const;

public class Log {
    private static boolean isDebug = BuildConfig.DEBUG;

    public static void d(String key, String value){
        android.util.Log.d(Const.TAG, isDebug+"");
        if (isDebug)
            android.util.Log.d(Const.TAG, key + " : " + value);
    }
}
