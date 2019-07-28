package com.orpheusdroid.screenrecorder.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;

import java.io.File;

public class PermissionHelper
{
    private static PermissionHelper permissionHelper;
    private AppCompatActivity mContext;

    public static PermissionHelper getInstance(AppCompatActivity mContext) {
        if (permissionHelper == null)
            permissionHelper = new PermissionHelper(mContext);
        return permissionHelper;
    }

    private PermissionHelper(AppCompatActivity mContext) {
        this.mContext = mContext;
    }

    private PermissionHelper() {

    }

    private String getString(int ID) {
        return mContext.getString(ID);
    }

    /**
     * Method to request permission for writing to external storage
     *
     * @return boolean
     */
    public boolean requestPermissionStorage(int RequestCode) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(mContext)
                    .setTitle(getString(R.string.storage_permission_request_title))
                    .setMessage(getString(R.string.storage_permission_request_summary))
                    .setNeutralButton(getString(android.R.string.ok), (dialogInterface, i) -> ActivityCompat.requestPermissions(mContext,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            RequestCode))
                    .setCancelable(false);

            alert.create().show();
            return false;
        }
        ActivityCompat.requestPermissions(mContext,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                RequestCode);
        return true;
    }


    /**
     * Method to request audio permission
     */
    public void requestPermissionAudio(int requestCode) {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mContext,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    requestCode);
        }
    }

    /**
     * Check if "show touches" plugin is installed.
     *
     * @return boolean
     */
    public boolean hasPluginInstalled(){
        PackageManager pm = mContext.getPackageManager();
        try {
            pm.getPackageInfo("com.orpheusdroid.screencamplugin",PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(Const.TAG, "Plugin not installed");
            return false;
        }
        return true;
    }

    public void requestPermissionCamera() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mContext,
                    new String[]{Manifest.permission.CAMERA},
                    Const.CAMERA_REQUEST_CODE);
        }
    }

    /**
     * Method to request system windows permission. The permission is granted implicitly on API's below 23
     */
    @TargetApi(23)
    public void requestSystemWindowsPermission(int code) {
        Log.d("Preference", "Can draw over: " + Settings.canDrawOverlays(mContext));
        if (!Settings.canDrawOverlays(mContext)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + mContext.getPackageName()));
            mContext.startActivityForResult(intent, code);
        }
    }

    /**
     * Static method to create the app's default directory in the external storage
     */
    public void createDir() {
        Log.d(Const.TAG, "creating Directory");
        File appDir = new File(Environment.getExternalStorageDirectory() + File.separator + Const.APPDIR);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && !appDir.isDirectory()) {
            Log.d(Const.TAG, "Can write Directory: " + appDir.canWrite());
            //if (!appDir.exists() || !appDir.isDirectory()) {

                if (appDir.mkdirs())
                    Log.d(Const.TAG, "Directory created");
            //}
        }
    }

    public void showSnackbar() {
        Snackbar.make(mContext.findViewById(R.id.container), R.string.snackbar_storage_permission_message,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.snackbar_storage_permission_action_enable,
                v -> {
                    if (mContext != null){
                        requestPermissionStorage(Const.EXTDIR_REQUEST_CODE);
                    }
                }).show();
    }
}
