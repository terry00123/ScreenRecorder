package com.orpheusdroid.screenrecorder.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.orpheusdroid.screenrecorder.Config;
import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.ScreenCamBaseApp;
import com.orpheusdroid.screenrecorder.interfaces.IPreferenceChangeListener;
import com.orpheusdroid.screenrecorder.ui.BaseActivity;
import com.orpheusdroid.screenrecorder.ui.MainActivity;
import com.orpheusdroid.screenrecorder.utils.ConfigHelper;
import com.orpheusdroid.screenrecorder.utils.Log;
import com.orpheusdroid.screenrecorder.utils.PermissionHelper;

public class PreferenceListener implements IPreferenceChangeListener {
    private Context mContext;
    private Config config;

    public PreferenceListener(Context mContext){
        this.mContext = mContext;
        this.config = Config.getInstance(mContext);
    }

    private String getString(int ID){
        return mContext.getString(ID);
    }

    @Override
    public void onPreferenceChangeListener(String key, Preference preference, SharedPreferences sharedPreferences) {
        Log.d("Preference", key + " : " + preference);
        PermissionHelper permissionHelper = PermissionHelper.getInstance((AppCompatActivity)mContext);
        switch (key){
            case "audioSource":
                ConfigHelper configHelper = ConfigHelper.getInstance(mContext);
                switch (((ListPreference)preference).getValue()) {
                    case "0":
                        break;
                    case "1":
                        permissionHelper.requestPermissionAudio(Const.AUDIO_REQUEST_CODE);
                        break;
                    case "2":
                        if (!sharedPreferences.getBoolean(Const.PREFS_INTERNAL_AUDIO_DIALOG_KEY, false))
                            configHelper.showInternalAudioWarning(false);
                        else
                            permissionHelper.requestPermissionAudio(Const.INTERNAL_AUDIO_REQUEST_CODE);
                        break;
                    case "3":
                        if (!Const.IS_MAGISK_MODE) {
                            Toast.makeText(mContext, getString(R.string.toast_magisk_module_required_message), Toast.LENGTH_SHORT).show();
                            ((ListPreference) preference).setValue("0");
                            break;
                        }

                        if (!sharedPreferences.getBoolean(Const.PREFS_INTERNAL_AUDIO_DIALOG_KEY, false))
                            configHelper.showInternalAudioWarning(true);
                        else
                            permissionHelper.requestPermissionAudio(Const.INTERNAL_R_SUBMIX_AUDIO_REQUEST_CODE);
                        break;
                    default:
                        ((ListPreference) preference).setValue("0");
                        break;
                }
                break;
            case "floating_controls":
                if (((CheckBoxPreference)preference).isChecked()) {
                    permissionHelper.requestSystemWindowsPermission(Const.FLOATING_CONTROLS_SYSTEM_WINDOWS_CODE);
                    config.setFloatingControls(true);
                }else
                    config.setFloatingControls(false);
                break;
            case "touch_pointer":
                if(((CheckBoxPreference)preference).isChecked()){
                    if (!permissionHelper.hasPluginInstalled())
                        ((CheckBoxPreference)preference).setChecked(false);
                }
                break;
            case "camera_overlay":
                if (((CheckBoxPreference)preference).isChecked()) {
                    permissionHelper.requestPermissionCamera();
                    permissionHelper.requestSystemWindowsPermission(Const.CAMERA_SYSTEM_WINDOWS_CODE);
                }
                break;
            case "language_key":
                Log.d(Const.TAG, "Lang: changed");
                //String lang = ((ListPreference)preference).getValue();
                //((ScreenCamBaseApp)((AppCompatActivity) mContext).getApplication()).changeLanguage(lang);
                ((BaseActivity) mContext).recreate();
                break;
        }
        config.buildConfig();
        Log.d("Preference", config.toString());
    }
}
