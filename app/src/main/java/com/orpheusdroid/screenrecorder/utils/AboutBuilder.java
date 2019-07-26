package com.orpheusdroid.screenrecorder.utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.orpheusdroid.screenrecorder.BuildConfig;
import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.ScreenCamBaseApp;
import com.orpheusdroid.screenrecorder.adapter.models.AboutModel;
import com.orpheusdroid.screenrecorder.adapter.models.FAQModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class AboutBuilder {
    private static AboutBuilder aboutBuilder;
    private Context context;

    private AboutBuilder() {
    }

    public static AboutBuilder getInstance(Context context) {
        if (aboutBuilder == null) {
            aboutBuilder = new AboutBuilder();
            aboutBuilder.context = context;
        }
        return aboutBuilder;
    }

    public ArrayList<AboutModel> buildAbout() {
        String version = BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")";
        boolean isMagisk = ((ScreenCamBaseApp)((AppCompatActivity)context).getApplication()).checkMagiskMode();
        boolean isRoot = false;
        String releaseType;
        if (BuildConfig.VERSION_NAME.contains("Beta")) {
            releaseType = "Beta Build. Not to be released or shared";
        } else {
            releaseType = "Stable release";
        }

        ArrayList<AboutModel> abouts = new ArrayList<>();
        abouts.add(new AboutModel(context.getString(R.string.about_info), AboutModel.TYPE.HEADER));
        abouts.add(new AboutModel(version, isMagisk, isRoot, releaseType, AboutModel.TYPE.INFO));
        abouts.add(new AboutModel(context.getString(R.string.about_credits), AboutModel.TYPE.HEADER));
        abouts.add(new AboutModel(context.getString(R.string.app_icon_credit_Niko, "Niko Hörkkö", "https://lumotypemedia.fi"), AboutModel.TYPE.DATA));
        abouts.add(new AboutModel(context.getString(R.string.about_libraries), AboutModel.TYPE.HEADER));
        abouts.add(new AboutModel(context.getString(R.string.video_editor_library_credit, "knowledge4life",
                "https://github.com/knowledge4life/k4l-video-trimmer",
                "MIT Opensource License"), AboutModel.TYPE.DATA));
        abouts.add(new AboutModel(context.getString(R.string.analytics_library_credit, "Countly",
                "https://github.com/Countly/countly-sdk-android",
                "MIT Opensource License"), AboutModel.TYPE.DATA));
        abouts.add(new AboutModel(context.getString(R.string.opensource_info, "https://gitlab.com/vijai/screenrecorder", "GNU AGPLv3"), AboutModel.TYPE.DATA));
        abouts.add(new AboutModel(context.getString(R.string.iap_library_credit, "serso",
                "https://github.com/serso/android-checkout",
                "Apache 2.0"), AboutModel.TYPE.DATA));
        abouts.add(new AboutModel(context.getString(R.string.changelog_library_credit, "MFlisar",
                "https://github.com/MFlisar/changelog",
                "Apache 2.0"), AboutModel.TYPE.DATA));

        return abouts;
    }
}
