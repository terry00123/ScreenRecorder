package com.orpheusdroid.screenrecorder.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.orpheusdroid.screenrecorder.Config;
import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;

public class ResolutionHelper {
    private static ResolutionHelper resolutionHelper;
    private Context context;
    private Config config;
    private DisplayMetrics metrics;


    private WindowManager window;

    public static ResolutionHelper getInstance(Context context){
        if (resolutionHelper == null){
            resolutionHelper = new ResolutionHelper(context);
        }
        return resolutionHelper;
    }

    private ResolutionHelper(Context context){
        this.context = context;
        config = Config.getInstance(context);
    }

    private DisplayMetrics getMetrics(){
        if (metrics == null)
            metrics = new DisplayMetrics();
        return metrics;
    }

    /* The PreferenceScreen save values as string and we save the user selected video resolution as
     * WIDTH x HEIGHT. Lets split the string on 'x' and retrieve width and height */
    public Resolution getWidthHeight() {
        Resolution resolution = new Resolution();
        String res = getResolution();
        String[] widthHeight = res.split("x");
        String orientationPrefs = config.getOrientation();
        int screenOrientation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (orientationPrefs) {
            case "auto":
                if (screenOrientation == 0 || screenOrientation == 2) {
                    resolution.setWIDTH(Integer.parseInt(widthHeight[0]));
                    resolution.setHEIGHT(Integer.parseInt(widthHeight[1]));
                    resolution.setOrientation(Const.Orientation.PORTRAIT);
                } else {
                    resolution.setWIDTH(Integer.parseInt(widthHeight[1]));
                    resolution.setHEIGHT(Integer.parseInt(widthHeight[0]));
                    resolution.setOrientation(Const.Orientation.LANDSCAPE);
                }
                break;
            case "portrait":
                resolution.setWIDTH(Integer.parseInt(widthHeight[0]));
                resolution.setHEIGHT(Integer.parseInt(widthHeight[1]));
                resolution.setOrientation(Const.Orientation.PORTRAIT);
                break;
            case "landscape":
                resolution.setHEIGHT(Integer.parseInt(widthHeight[0]));
                resolution.setWIDTH(Integer.parseInt(widthHeight[1]));
                resolution.setOrientation(Const.Orientation.LANDSCAPE);
                break;
        }
        Log.d(Const.TAG, "Width: " + resolution.getWIDTH() + ",Height:" + resolution.getHEIGHT());
        resolution.setDPI(getDPI());
        return resolution;
    }

    private int getDPI(){
        Log.d(Const.TAG, "Resolution Density: " + getMetrics().densityDpi);
        return getMetrics().densityDpi;
    }

    //Get the device resolution in pixels
    private String getResolution() {
        window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        window.getDefaultDisplay().getRealMetrics(getMetrics());
        int width = Integer.parseInt(config.getResolution());
        float aspectRatio = getAspectRatio(getMetrics());
        int height = calculateClosestHeight(width, aspectRatio);
        //String res = width + "x" + (int) (width * getAspectRatio(metrics));
        String res = width + "x" + height;
        Log.d(Const.TAG, "Resolution service: " + "[Width: "
                + width + ", Height: " + width * aspectRatio + ", aspect ratio: " + aspectRatio +
                ",dpi: " + metrics.densityDpi + "]");
        return res;
    }

    private float getAspectRatio(DisplayMetrics metrics) {
        float screen_width = metrics.widthPixels;
        float screen_height = metrics.heightPixels;
        float aspectRatio;
        if (screen_width > screen_height) {
            aspectRatio = screen_width / screen_height;
        } else {
            aspectRatio = screen_height / screen_width;
        }
        return aspectRatio;
    }

    private int calculateClosestHeight(int width, float aspectRatio) {
        int calculatedHeight = (int) (width * aspectRatio);
        Log.d(Const.TAG, "Calculated width=" + calculatedHeight);
        Log.d(Const.TAG, "Aspect ratio: " + aspectRatio);
        if (calculatedHeight / 16 != 0) {
            int quotient = calculatedHeight / 16;
            Log.d(Const.TAG, calculatedHeight + " not divisible by 16");

            calculatedHeight = 16 * quotient;

            Log.d(Const.TAG, "Maximum possible height is " + calculatedHeight);
        }
        return calculatedHeight;
    }
}
