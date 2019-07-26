package com.orpheusdroid.screenrecorder.ui.settings;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.orpheusdroid.screenrecorder.R;

public class AppPickerPreference extends DialogPreference {
    public AppPickerPreference(Context context) {
        super(context);
    }

    public AppPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPersistent(true);

        //set custom dialog layout
        setDialogLayoutResource(R.layout.layout_apps_list_preference);
        setPositiveButtonText(null);
    }



    public boolean saveString(String value){
        return persistString(value);
    }

    public String getString(String defaultValue){
        return getPersistedString(defaultValue);
    }
}

