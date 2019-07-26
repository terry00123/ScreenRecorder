package com.orpheusdroid.screenrecorder;

public class Const {

    public enum Orientation{
        PORTRAIT, LANDSCAPE
    }

    public static final String TAG = "ScreenCam";

    // Permission request code constants. Starts from 100
    public static final int EXTDIR_REQUEST_CODE = 100;
    public static final int AUDIO_REQUEST_CODE = 101;
    public static final int INTERNAL_AUDIO_REQUEST_CODE = 102;
    public static final int INTERNAL_R_SUBMIX_AUDIO_REQUEST_CODE = 103;
    public static final int FLOATING_CONTROLS_SYSTEM_WINDOWS_CODE = 104;
    public static final int SCREEN_RECORD_REQUEST_CODE = 105;
    public static final int CAMERA_REQUEST_CODE = 106;
    public static final int CAMERA_SYSTEM_WINDOWS_CODE = 107;

    // Preference Keys
    public static final String PREFS_REQUEST_ANALYTICS_PERMISSION = "request_analytics_permission";
    public static final String PREFS_WHITE_THEME = "white_theme";
    public static final String PREFS_LIGHT_THEME = "light_theme";
    public static final String PREFS_DARK_THEME = "dark_theme";
    public static final String PREFS_BLACK_THEME = "black_theme";
    public static final String PREFS_CAMERA_OVERLAY_POS = "camera_overlay_pos";
    public static final String PREFS_INTERNAL_AUDIO_DIALOG_KEY = "int_audio_diag";

    public static boolean IS_MAGISK_MODE;

    // Number of seconds the alert dialog for audio source is blocked
    public static final int AUDIO_SOURCE_ALERT_DIALOG_BLOCK = 1;

    //Notification Channels
    public static final String RECORDING_NOTIFICATION_CHANNEL_ID = "recording_notification_channel_id1";
    public static final String SHARE_NOTIFICATION_CHANNEL_ID = "share_notification_channel_id1";
    public static final String RECORDING_NOTIFICATION_CHANNEL_NAME = "Shown Persistent notification when recording screen or when waiting for shake gesture";
    public static final String SHARE_NOTIFICATION_CHANNEL_NAME = "Show Notification to share or edit the recorded video";

    //Recorder Intent Data
    public static final String RECORDER_INTENT_DATA = "recorder_intent_data";
    public static final String RECORDER_INTENT_RESULT = "recorder_intent_result";

    //Custom Intent
    public static final String SCREEN_RECORDING_START = "com.orpheusdroid.screenrecorder.services.action.startrecording";
    public static final String SCREEN_RECORDING_PAUSE = "com.orpheusdroid.screenrecorder.services.action.pauserecording";
    public static final String SCREEN_RECORDING_RESUME = "com.orpheusdroid.screenrecorder.services.action.resumerecording";
    public static final String SCREEN_RECORDING_STOP = "com.orpheusdroid.screenrecorder.services.action.stoprecording";
    public static final String SCREEN_RECORDING_SHOW_FLOATING_ACTIONS = "com.orpheusdroid.screenrecorder.services.action.showfloatingcontrols";
    public static final String SCREEN_RECORDING_DESTORY_SHAKE_GESTURE = "com.orpheusdroid.screenrecorder.services.action.destoryshakegesture";
    public static final String SCREEN_RECORDER_VIDEOS_LIST_FRAGMENT_INTENT = "com.orpheusdroid.screenrecorder.SHOWVIDEOSLIST";

    //Countly consts
    public static final String ANALYTICS_URL = "https://analytics.orpheusdroid.com";
    public static final String ANALYTICS_API_KEY = "07273a5c91f8a932685be1e3ad0d160d3de6d4ba";
    public static final String COUNTLY_USAGE_STATS_GROUP_NAME = "analytics_group";

    //Notification IDs
    public static final int SCREEN_RECORDER_NOTIFICATION_ID = 5001;
    public static final int SCREEN_RECORDER_SHARE_NOTIFICATION_ID = 5002;
    public static final int SCREEN_RECORDER_WAITING_FOR_SHAKE_NOTIFICATION_ID = 5003;

    public static final String APPDIR = "screenrecorder";
}
