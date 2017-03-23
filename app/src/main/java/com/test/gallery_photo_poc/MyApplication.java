package com.test.gallery_photo_poc;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import java.util.Locale;

/**
 *
 */
public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Adneom","in on create ");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("Adneom","in on configuration changed");
        setLocale();
    }

    private void setLocale() {

        final Resources resources = getResources();
        final Configuration configuration = resources.getConfiguration();
        final Locale locale = configuration.locale;
        if (!configuration.locale.equals(locale)) {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, null);
        }
    }
}
