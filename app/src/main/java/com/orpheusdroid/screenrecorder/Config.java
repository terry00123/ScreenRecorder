package com.orpheusdroid.screenrecorder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.Locale;

public class Config {
    private static Config config;
    private Context mContext;

    private String saveLocation;
    private String fileFormat;
    private String prefix;

    private String resolution;
    private String fps;
    private String videoBitrate;
    private String orientation;

    private String audioSource;
    private String audioBitrate;
    private String audioChannel;
    private String audioSamplingRate;

    private boolean floatingControls;
    private boolean showTouches;
    private boolean cameraOverlay;
    private boolean targetApp;
    private String targetAppPackage;

    private String language;

    public String getSaveLocation() {
        return saveLocation;
    }

    public void setSaveLocation(String saveLocation) {
        this.saveLocation = saveLocation;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getFps() {
        return fps;
    }

    public void setFps(String fps) {
        this.fps = fps;
    }

    public String getVideoBitrate() {
        return videoBitrate;
    }

    public void setVideoBitrate(String videoBitrate) {
        this.videoBitrate = videoBitrate;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getAudioSource() {
        return audioSource;
    }

    public void setAudioSource(String audioSource) {
        this.audioSource = audioSource;
    }

    public String getAudioBitrate() {
        return audioBitrate;
    }

    public void setAudioBitrate(String audioBitrate) {
        this.audioBitrate = audioBitrate;
    }

    public String getAudioChannel() {
        return audioChannel;
    }

    public void setAudioChannel(String audioChannel) {
        this.audioChannel = audioChannel;
    }

    public String getAudioSamplingRate() {
        return audioSamplingRate;
    }

    public void setAudioSamplingRate(String audioSamplingRate) {
        this.audioSamplingRate = audioSamplingRate;
    }

    public boolean isFloatingControls() {
        return floatingControls;
    }

    public void setFloatingControls(boolean floatingControls) {
        this.floatingControls = floatingControls;
    }

    public boolean isShowTouches() {
        return showTouches;
    }

    public void setShowTouches(boolean showTouches) {
        this.showTouches = showTouches;
    }

    public boolean isCameraOverlay() {
        return cameraOverlay;
    }

    public void setCameraOverlay(boolean cameraOverlay) {
        this.cameraOverlay = cameraOverlay;
    }

    public boolean isTargetApp() {
        return targetApp;
    }

    public void setTargetApp(boolean targetApp) {
        this.targetApp = targetApp;
    }

    public String getTargetAppPackage() {
        return targetAppPackage;
    }

    public void setTargetAppPackage(String targetAppPackage) {
        this.targetAppPackage = targetAppPackage;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public static Config getInstance(Context mContext){
        if (config == null) {
            config = new Config(mContext);
        }
        return config;
    }

    private Config(Context mContext){
        this.mContext = mContext;
    }

    private Config(){

    }

    public void buildConfig(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        saveLocation = new File(Environment.getExternalStorageDirectory() + File.separator + Const.APPDIR).getPath();
        fileFormat = preferences.getString(getString(R.string.filename_key), "yyyyMMdd_HHmmss");
        prefix = preferences.getString(getString(R.string.fileprefix_key), "recording");

        resolution = preferences.getString(getString(R.string.res_key), "");
        fps = preferences.getString(getString(R.string.fps_key), "30");
        videoBitrate = preferences.getString(getString(R.string.bitrate_key), "7130317");
        orientation =preferences.getString(getString(R.string.orientation_key), "auto");

        audioSource = preferences.getString(getString(R.string.audiorec_key), "0");
        audioBitrate = preferences.getString(getString(R.string.bitrate_key), "192000");
        audioChannel = preferences.getString(getString(R.string.audiochannels_key), "1");
        audioSamplingRate = preferences.getString(getString(R.string.audiosamplingrate_key), "48000");

        floatingControls = preferences.getBoolean(getString(R.string.preference_floating_control_key), false);
        showTouches = preferences.getBoolean(getString(R.string.preference_show_touch_key), false);
        cameraOverlay = preferences.getBoolean(getString(R.string.preference_camera_overlay_key), false);
        targetApp = preferences.getBoolean(getString(R.string.preference_enable_target_app_key), false);
        targetAppPackage = preferences.getString(getString(R.string.preference_app_chooser_key), "");

        language = preferences.getString(getString(R.string.preference_language_key), Locale.getDefault().getISO3Language());
    }

    private String getString(int ID){
        return mContext.getString(ID);
    }

    @Override
    public String toString() {
        return "Config{" +
                "resolution='" + resolution + '\'' +
                ", fps='" + fps + '\'' +
                ", videoBitrate='" + videoBitrate + '\'' +
                ", orientation='" + orientation + '\'' +
                ", audioSource='" + audioSource + '\'' +
                ", audioBitrate='" + audioBitrate + '\'' +
                ", audioChannel='" + audioChannel + '\'' +
                ", audioSamplingRate='" + audioSamplingRate + '\'' +
                ", floatingControls=" + floatingControls +
                ", showTouches=" + showTouches +
                ", cameraOverlay=" + cameraOverlay +
                ", targetApp=" + targetApp +
                ", targetAppPackage='" + targetAppPackage + '\'' +
                '}';
    }
}
