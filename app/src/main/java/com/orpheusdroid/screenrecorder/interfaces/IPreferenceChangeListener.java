package com.orpheusdroid.screenrecorder.interfaces;

import android.content.SharedPreferences;

import androidx.preference.Preference;

public interface IPreferenceChangeListener {
    void onPreferenceChangeListener(String key, Preference preference, SharedPreferences sharedPreferences);
}
