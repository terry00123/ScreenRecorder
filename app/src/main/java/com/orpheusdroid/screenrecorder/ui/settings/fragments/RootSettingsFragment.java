package com.orpheusdroid.screenrecorder.ui.settings.fragments;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.interfaces.IPermissionListener;
import com.orpheusdroid.screenrecorder.interfaces.IPreferenceChangeListener;
import com.orpheusdroid.screenrecorder.preferences.PreferenceListener;
import com.orpheusdroid.screenrecorder.ui.MainActivity;
import com.orpheusdroid.screenrecorder.ui.settings.AppPickerDialog;
import com.orpheusdroid.screenrecorder.ui.settings.AppPickerPreference;
import com.orpheusdroid.screenrecorder.utils.ConfigHelper;
import com.orpheusdroid.screenrecorder.utils.LanguageSelectionHandler;
import com.orpheusdroid.screenrecorder.utils.PermissionHelper;

public class RootSettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, IPermissionListener {
    private static final String DIALOG_FRAGMENT_TAG = "CustomPreference";
    private IPreferenceChangeListener preferenceChangeListener;

    private PermissionHelper permissionHelper;

    // Get preferences only for which has to be validated for permission
    private ListPreference audioSource;

    private CheckBoxPreference floatingControls;

    private CheckBoxPreference showTouches;

    private CheckBoxPreference cameraOverlay;

    private ListPreference languages;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        preferenceChangeListener = new PreferenceListener(getActivity());

        if (getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).setPermissionListener(this);
        }

        permissionHelper = PermissionHelper.getInstance((AppCompatActivity)getActivity());

        init();

        checkPermissions();
    }

    private void init() {
        audioSource = findPreference(getString(R.string.audiorec_key));
        floatingControls = findPreference(getString(R.string.preference_floating_control_key));
        showTouches = findPreference(getString(R.string.preference_show_touch_key));
        cameraOverlay = findPreference(getString(R.string.preference_camera_overlay_key));
        languages = findPreference(getString(R.string.preference_language_key));

        String[] entries = LanguageSelectionHandler.getInstance(this.getActivity()).buildLanguageStrings();
        languages.setEntries(entries);

        ConfigHelper.getInstance(getActivity())
                .prepareResolutions(findPreference(getString(R.string.res_key)));
    }

    private void checkPermissions(){
        checkAudioRecPermission();
        if (floatingControls.isChecked())
            permissionHelper.requestSystemWindowsPermission(Const.FLOATING_CONTROLS_SYSTEM_WINDOWS_CODE);

        if (cameraOverlay.isChecked()) {
            permissionHelper.requestPermissionCamera();
            permissionHelper.requestSystemWindowsPermission(Const.CAMERA_SYSTEM_WINDOWS_CODE);
        }

    }

    private void checkAudioRecPermission() {
        String value = audioSource.getValue();
        switch (value) {
            case "1":
                permissionHelper.requestPermissionAudio(Const.AUDIO_REQUEST_CODE);
                break;
            case "2":
                permissionHelper.requestPermissionAudio(Const.INTERNAL_AUDIO_REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        checkPermissions();
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        ((MainActivity)getActivity()).setBottomBarVisibility(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
            return;
        }

        if (preference instanceof AppPickerPreference) {
            final DialogFragment f = AppPickerDialog.newInstance(preference.getKey());
            f.setTargetFragment(this, 0);
            f.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        preferenceChangeListener.onPreferenceChangeListener(key, preference, sharedPreferences);
    }


    @Override
    public void onPermissionResult(int requestCode,
                                   String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Const.AUDIO_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    android.util.Log.d(Const.TAG, "Record audio permission granted.");
                    audioSource.setValue("1");
                } else {
                    android.util.Log.d(Const.TAG, "Record audio permission denied");
                    audioSource.setValue("0");
                }
                //((ListPreference)findPreference(getString(R.string.audiorec_key))).setSummary(recaudio.getEntry());
                break;
            case Const.INTERNAL_AUDIO_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    android.util.Log.d(Const.TAG, "Record audio permission granted.");
                    audioSource.setValue("2");
                } else {
                    android.util.Log.d(Const.TAG, "Record audio permission denied");
                    audioSource.setValue("0");
                }
                //recaudio.setSummary(recaudio.getEntry());
                break;
            case Const.INTERNAL_R_SUBMIX_AUDIO_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    android.util.Log.d(Const.TAG, "Record audio permission granted.");
                    audioSource.setValue("3");
                } else {
                    android.util.Log.d(Const.TAG, "Record audio permission denied");
                    audioSource.setValue("0");
                }
                //recaudio.setSummary(recaudio.getEntry());
                break;
            case Const.FLOATING_CONTROLS_SYSTEM_WINDOWS_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    android.util.Log.d(Const.TAG, "System Windows permission granted");
                    floatingControls.setChecked(true);
                } else {
                    android.util.Log.d(Const.TAG, "System Windows permission denied");
                    floatingControls.setChecked(false);
                }
                break;
            case Const.CAMERA_SYSTEM_WINDOWS_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    android.util.Log.d(Const.TAG, "System Windows permission granted");
                    cameraOverlay.setChecked(true);
                } else {
                    android.util.Log.d(Const.TAG, "System Windows permission denied");
                    cameraOverlay.setChecked(false);
                }
                break;
            case Const.CAMERA_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    android.util.Log.d(Const.TAG, "System Windows permission granted");
                    permissionHelper.requestSystemWindowsPermission(Const.CAMERA_SYSTEM_WINDOWS_CODE);
                } else {
                    android.util.Log.d(Const.TAG, "System Windows permission denied");
                    cameraOverlay.setChecked(false);
                }
        }
    }
}
