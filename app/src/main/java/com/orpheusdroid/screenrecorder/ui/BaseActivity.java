package com.orpheusdroid.screenrecorder.ui;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.orpheusdroid.screenrecorder.utils.LocaleHelper;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

}
