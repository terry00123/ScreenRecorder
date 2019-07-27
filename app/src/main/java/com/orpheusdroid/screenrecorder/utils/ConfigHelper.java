package com.orpheusdroid.screenrecorder.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceManager;

import com.orpheusdroid.screenrecorder.Config;
import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.ui.FAQActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static com.orpheusdroid.screenrecorder.Const.AUDIO_SOURCE_ALERT_DIALOG_BLOCK;

public class ConfigHelper {
    private static ConfigHelper helper;
    private Context mContext;

    public static ConfigHelper getInstance(Context mContext) {
        if (helper == null)
            helper = new ConfigHelper(mContext);
        return helper;
    }

    private ConfigHelper(Context mContext) {
        this.mContext = mContext;
    }

    private ConfigHelper() {

    }

    public void prepareResolutions(ListPreference res) {

        ArrayList<String> resEntries = new ArrayList<>(Arrays.asList(
                mContext.getResources().getStringArray(R.array.resolutionsArray))
        );
        ArrayList<String> resEntryValues = new ArrayList<>(Arrays.asList(
                mContext.getResources().getStringArray(R.array.resolutionValues))
        );

        String nativeRes = getNativeRes();

        boolean hasValuesChanged = false;

        for (String resolution : resEntryValues) {
            if (Integer.parseInt(resolution) > Integer.parseInt(nativeRes)) {
                resEntries.remove(resolution + "P");
                resEntryValues.remove(resolution);
                hasValuesChanged = true;
                Log.d(Const.TAG, "Removed " + resolution + " from entries");
            }
        }

        if (!resEntryValues.contains(nativeRes)) {
            Log.d(Const.TAG, "Add native res! " + nativeRes);
            resEntries.add(nativeRes + "P");
            resEntryValues.add(nativeRes);
            hasValuesChanged = true;
        }

        if (hasValuesChanged) {
            res.setEntries(resEntries.toArray(new CharSequence[resEntries.size()]));
            res.setEntryValues(resEntryValues.toArray(new CharSequence[resEntryValues.size()]));
        }
    }

    /**
     * Method to get the device's native resolution
     *
     * @return device resolution
     */
    private String getNativeRes() {
        DisplayMetrics metrics = getRealDisplayMetrics();
        return String.valueOf(getScreenWidth(metrics));
    }

    private DisplayMetrics getRealDisplayMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager window = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        window.getDefaultDisplay().getRealMetrics(metrics);
        return metrics;
    }

    /**
     * Get width of screen in pixels
     *
     * @return screen width
     */
    private int getScreenWidth(DisplayMetrics metrics) {
        return metrics.widthPixels;
    }

    /**
     * Get height of screen in pixels
     *
     * @return Screen height
     */
    private int getScreenHeight(DisplayMetrics metrics) {
        return metrics.heightPixels;
    }

    public String getScreenWidth() {
        return Integer.toString(getScreenWidth(getRealDisplayMetrics()));
    }

    public void showInternalAudioWarning(boolean isR_submix) {
        int message;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        final int requestCode;
        if (isR_submix) {
            message = R.string.alert_dialog_r_submix_audio_warning_message;
            requestCode = Const.INTERNAL_R_SUBMIX_AUDIO_REQUEST_CODE;
        } else {
            message = R.string.alert_dialog_internal_audio_warning_message;
            requestCode = Const.INTERNAL_AUDIO_REQUEST_CODE;
        }
        AlertDialog audioWarningDialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.alert_dialog_internal_audio_warning_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) ->
                        PermissionHelper.getInstance((AppCompatActivity) mContext).requestPermissionAudio(requestCode))
                .setNeutralButton(R.string.alert_dialog_internal_audio_warning_faq_text, (dialogInterface, i) ->
                        mContext.startActivity(new Intent(mContext, FAQActivity.class)))
                .setNegativeButton(R.string.alert_dialog_internal_audio_warning_negative_btn_text, (dialogInterface, i) -> {
                    prefs.edit().putBoolean(Const.PREFS_INTERNAL_AUDIO_DIALOG_KEY, true)
                            .apply();
                    PermissionHelper.getInstance((AppCompatActivity) mContext)
                            .requestPermissionAudio(Const.INTERNAL_AUDIO_REQUEST_CODE);
                })
                .setCancelable(false)
                .create();
        audioWarningDialog.show();

        //Disable positive buttons (If only users are sensible enough to read without forcing them counter-intuitively)
        disableWarningDialogButtons(audioWarningDialog);
    }

    private boolean getMediaCodecFor(String format, int WIDTH, int HEIGHT) {
        MediaCodecList list = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(
                format,
                WIDTH,
                HEIGHT
        );
        String encoder = list.findEncoderForFormat(mediaFormat);
        if (encoder == null) {
            Log.d("Null Encoder: ", format);
            return false;
        }
        Log.d("Encoder", encoder);
        return !encoder.startsWith("OMX.google");
    }

    public int getBestVideoEncoder(int width, int height) {
        String codecOverride = Config.getInstance(mContext).getCodecOverride();
        if (! codecOverride.equals("0")){
            switch (codecOverride){
                case "1":
                    return MediaRecorder.VideoEncoder.HEVC;
                case "2":
                    return MediaRecorder.VideoEncoder.H264;
                case "3":
                    return MediaRecorder.VideoEncoder.DEFAULT;
            }
        }
        int VideoCodec = MediaRecorder.VideoEncoder.DEFAULT;
        if (getMediaCodecFor(MediaFormat.MIMETYPE_VIDEO_HEVC, width, height)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                VideoCodec = MediaRecorder.VideoEncoder.HEVC;
            }
        } else if (getMediaCodecFor(MediaFormat.MIMETYPE_VIDEO_AVC, width, height))
            VideoCodec = MediaRecorder.VideoEncoder.H264;
        return VideoCodec;
    }

    public long getFreeSpaceInBytes(String SAVEPATH) {
        StatFs FSStats = new StatFs(SAVEPATH);
        long bytesAvailable = FSStats.getAvailableBytes();// * FSStats.getBlockCountLong();
        Log.d(Const.TAG, "Free space in GB: " + bytesAvailable / (1000 * 1000 * 1000));
        return bytesAvailable;
    }

    //Return filename of the video to be saved formatted as chosen by the user
    public String getFileSaveName(String SAVEPATH) {
        Config config = Config.getInstance(mContext);
        String filename = config.getFileFormat();

        //Required to handle preference change
        filename = filename.replace("hh", "HH");
        String prefix = config.getPrefix();
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat(filename);
        return new File(SAVEPATH,File.pathSeparator + prefix + "_" + formatter.format(today)+".mp4").getPath();
    }

    private void disableWarningDialogButtons(AlertDialog dialog) {
        final Handler handler = new Handler();
        final int[] count = {0};

        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        final Button NegativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        final CharSequence positveButtonText = positiveButton.getText();
        final CharSequence NegativeButtonText = NegativeButton.getText();

        positiveButton.setEnabled(false);
        NegativeButton.setEnabled(false);
        String message = "Wait for " + AUDIO_SOURCE_ALERT_DIALOG_BLOCK + " seconds";
        positiveButton.setText(message);
        NegativeButton.setText(message);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                count[0]++;
                if (count[0] < AUDIO_SOURCE_ALERT_DIALOG_BLOCK) {
                    String message = "Wait for " + (AUDIO_SOURCE_ALERT_DIALOG_BLOCK - count[0]) + " seconds";
                    positiveButton.setText(message);
                    NegativeButton.setText(message);
                    handler.postDelayed(this, 1000);
                } else {
                    positiveButton.setEnabled(true);
                    NegativeButton.setEnabled(true);

                    positiveButton.setText(positveButtonText);
                    NegativeButton.setText(NegativeButtonText);
                }
            }
        }, 1000);
    }
}
