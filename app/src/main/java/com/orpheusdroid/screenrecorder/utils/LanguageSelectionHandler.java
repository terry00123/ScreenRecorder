package com.orpheusdroid.screenrecorder.utils;

import android.content.Context;

import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;

import java.util.ArrayList;
import java.util.Locale;

public class LanguageSelectionHandler {
    private static LanguageSelectionHandler languageSelectionHandler;
    private Context mContext;

    private LanguageSelectionHandler(Context mContext) {
        this.mContext = mContext;
    }

    public static LanguageSelectionHandler getInstance(Context mContext){
        if (languageSelectionHandler == null)
            languageSelectionHandler = new LanguageSelectionHandler(mContext);
        return languageSelectionHandler;
    }

    public String[] buildLanguageStrings(){
        ArrayList<String> languages = new ArrayList<>();

        String[] locales = mContext.getResources().getStringArray(R.array.languageEntries);
        languages.add(mContext.getString(R.string.default_language));

        Log.d(Const.TAG, "Default: " + Locale.getDefault().toString());

        for (String localeCode : locales) {
            if (localeCode.equals("Default"))
                continue;
            Locale locale;
            if (localeCode.contains("_")) {
                String[] language = localeCode.split("_");
                locale = new Locale(language[0].toLowerCase(), language[1].toLowerCase());
            } else
                locale = new Locale(localeCode);
            Log.d(Const.TAG, locale.getDisplayName(locale));
            languages.add(locale.getDisplayName(locale) + " (" + locale.getDisplayName() + ")");
        }

        return languages.toArray(new String[0]);
    }
}
