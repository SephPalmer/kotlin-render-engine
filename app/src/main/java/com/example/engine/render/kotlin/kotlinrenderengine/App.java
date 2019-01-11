package com.example.engine.render.kotlin.kotlinrenderengine;

import android.app.Application;

public class App extends Application {

    private static App a;

    @Override
    public void onCreate() {
        super.onCreate();
        setApplicationStaticInstance(this);
    }

    @Override
    public void onTerminate() {
        setApplicationStaticInstance(null);
        super.onTerminate();
    }

    public static App get() {
        return a;
    }

    // Method necessary to bypass a findbugs warning for this specific case.
    // http://stackoverflow.com/a/21136731/1847449
    private static void setApplicationStaticInstance(App application) {
        a = application;
    }
}
