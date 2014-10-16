package ru.azat.mbaweather.ui;

import android.app.Application;
import android.content.Context;


public class WeatherApp extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();

    }
    public static Context getAppContext() {
        return context;
    }
}
