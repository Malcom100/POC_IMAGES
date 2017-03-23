package com.test.gallery_photo_poc;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Locale;

/**
 *
 */
public class BaseActivity extends AppCompatActivity{

    private Locale mCurrentLocale;

    @Override
    protected void onStart() {
        super.onStart();

        mCurrentLocale = getResources().getConfiguration().locale;
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        //get the language from settings
        Locale locale = Locale.getDefault();
        Log.i("Adneom","(onRestart) "+locale.getLanguage());

        if(!locale.equals(mCurrentLocale)){

            mCurrentLocale = locale;
            recreate();
        }
    }
}
