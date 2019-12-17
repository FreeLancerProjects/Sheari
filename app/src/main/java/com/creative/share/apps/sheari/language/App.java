package com.creative.share.apps.sheari.language;

import android.app.Application;
import android.content.Context;


public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.updateResources(base,"ar"));
    }

  public void onCreate() {
        super.onCreate();

        TypefaceUtil.setDefaultFont(this, "DEFAULT", "fonts/ar_font.ttf");
        TypefaceUtil.setDefaultFont(this, "MONOSPACE", "fonts/ar_font.ttf");
        TypefaceUtil.setDefaultFont(this, "SERIF", "fonts/ar_font.ttf");
        TypefaceUtil.setDefaultFont(this, "SANS_SERIF", "fonts/ar_font.ttf");



    }
}
