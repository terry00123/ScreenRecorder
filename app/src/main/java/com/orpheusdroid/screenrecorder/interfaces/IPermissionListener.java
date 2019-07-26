package com.orpheusdroid.screenrecorder.interfaces;

public interface IPermissionListener {
    void onPermissionResult(int requestCode,
                            String[] permissions, int[] grantResults);
}
