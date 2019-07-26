package com.orpheusdroid.screenrecorder.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.orpheusdroid.screenrecorder.Config;

import java.util.Locale;

public class LocaleHelper {

    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";

    public static Context onAttach(Context context) {
        return setLocale(context);
    }

    public static Context onAttach(Context context, String defaultLanguage) {
        return setLocale(context);
    }

    public static String getLanguage(Context context) {
        return getLocale(context).toString();
    }

    public static Context setLocale(Context context) {
        //persist(context, language);
        Locale language = getLocale(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }

        return updateResourcesLegacy(context, language);
    }

    public static Locale getLocale(Context context){
        Config config = Config.getInstance(context);
        config.buildConfig();
        String lang = config.getLanguage();
        if (lang.equalsIgnoreCase("Default")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                lang = Resources.getSystem().getConfiguration().getLocales().get(0).toString();
            } else
                lang = Resources.getSystem().getConfiguration().locale.toString();
        }
        Locale locale;
        if (lang.contains("_")){
            String[] language = lang.split("_");
            locale = new Locale(language[0], language[1]);
        } else
            locale = new Locale(lang);
        return locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, Locale language) {
        Locale.setDefault(language);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(language);
        configuration.setLayoutDirection(language);

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, Locale language) {
        Locale.setDefault(language);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = language;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(language);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }
}
