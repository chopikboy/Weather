package ru.azat.mbaweather.helpers;

/**
 * Created by azat on 15.10.14.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

import ru.azat.mbaweather.objects.WeatherObject;
import ru.azat.mbaweather.ui.WeatherApp;


public class WeatherSQLiteHelper extends SQLiteOpenHelper {

    private static final String WEATHER_DATABASE = "weather_database.db";
    private static final int WEATHER_DATABASE_VERSION = 1;


    private static final String CURRENT_WEATHER_TABLE = "current_weather";
    private static final String DAILY_WEATHER_TABLE = "daily_weather";
    private static final String CITIES_TABLE = "cities";

    private static final String CITY_NAME_COLUMN = "city_name";
    private static final String CITY_ID_COLUMN = "city_id";
    private static final String MAX_T_COLUMN = "max_t";
    private static final String MIN_T_COLUMN = "min_t";
    private static final String T_COLUMN = "t";
    private static final String DATE_COLUMN = "date";


    private static final String CREATE_DAILY_WEATHER_TABLE_SCRIPT = "CREATE TABLE "
            + DAILY_WEATHER_TABLE + " (" + BaseColumns._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE_COLUMN
            + " LONG NOT NULL, " + CITY_ID_COLUMN + " INTEGER NOT NULL, " + MAX_T_COLUMN
            + " INTEGER NOT NULL, " + MIN_T_COLUMN + " INTEGER NOT NULL);";

    private static final String CREATE_CURRENT_WEATHER_TABLE_SCRIPT = "CREATE TABLE "
            + CURRENT_WEATHER_TABLE + " (" + BaseColumns._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CITY_ID_COLUMN + " INTEGER  NOT NULL, " + MAX_T_COLUMN
            + " INTEGER  NOT NULL, " + MIN_T_COLUMN + " INTEGER  NOT NULL, " + T_COLUMN + " INTEGER NOT NULL);";


    private static final String CREATE_CITIES_TABLE_SCRIPT = "create table "
            + CITIES_TABLE + " (" + BaseColumns._ID
            + " integer primary key, " + CITY_NAME_COLUMN
            + " TEXT  NOT NULL);";
    private static int[] cityIDs;

    public WeatherSQLiteHelper(Context context, String name, CursorFactory factory,
                               int version) {
        super(context, name, factory, version);


        // TODO Auto-generated constructor stub
    }

    public static int[] getCityIDs() {

        WeatherSQLiteHelper dbh = new WeatherSQLiteHelper(WeatherApp.getAppContext(), WEATHER_DATABASE, null, WEATHER_DATABASE_VERSION);
        SQLiteDatabase db = dbh.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT " + BaseColumns._ID + ", " + CITY_NAME_COLUMN + " FROM " +
                CITIES_TABLE, null);


        if (c != null) {
            int cityIDs[] = new int[c.getCount()];
            int i = 0;
            if (c.moveToFirst()) {
                String str;
                do {
                    int id = c.getInt(c.getColumnIndex(BaseColumns._ID));
                    cityIDs[i++] = id;
                    // String name = c.getString(c.getColumnIndex(CITY_NAME_COLUMN));

                } while (c.moveToNext());
            }
            return cityIDs;
        }
        c.close();
        db.close();
        return null;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(CREATE_CITIES_TABLE_SCRIPT);

        db.execSQL(CREATE_DAILY_WEATHER_TABLE_SCRIPT);
        db.execSQL(CREATE_CURRENT_WEATHER_TABLE_SCRIPT);

        ContentValues moscowValues = new ContentValues();
        moscowValues.put(BaseColumns._ID, "524901");
        moscowValues.put(CITY_NAME_COLUMN, "Москва");
        db.insert(CITIES_TABLE, null, moscowValues);


        ContentValues spbValues = new ContentValues();
        spbValues.put(BaseColumns._ID, "498817");
        spbValues.put(CITY_NAME_COLUMN, "Санкт-Петербург");
        db.insert(CITIES_TABLE, null, spbValues);


    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }


    public static ArrayList<WeatherObject> getAllCitiesCurrentWeather() {
        WeatherSQLiteHelper dbh = new WeatherSQLiteHelper(WeatherApp.getAppContext(), WEATHER_DATABASE, null, WEATHER_DATABASE_VERSION);
        SQLiteDatabase db = dbh.getWritableDatabase();


        Cursor c = db.rawQuery("SELECT " + CITIES_TABLE + "." + BaseColumns._ID + " as " + BaseColumns._ID + ", "
                + CITIES_TABLE + "." + CITY_NAME_COLUMN + " as " + CITY_NAME_COLUMN + ", "
                + CURRENT_WEATHER_TABLE + "." + T_COLUMN + " as " + T_COLUMN + ", "
                + CURRENT_WEATHER_TABLE + "." + MIN_T_COLUMN + " as " + MIN_T_COLUMN + ", "
                + CURRENT_WEATHER_TABLE + "." + MAX_T_COLUMN + " as " + MAX_T_COLUMN
                + " FROM " + CURRENT_WEATHER_TABLE
                + " INNER JOIN " + CITIES_TABLE
                + " ON " + CITIES_TABLE + "." + BaseColumns._ID + " = " + CURRENT_WEATHER_TABLE + "." + CITY_ID_COLUMN, null);


        ArrayList<WeatherObject> weatherObjects = new ArrayList<WeatherObject>();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    WeatherObject weatherObject = new WeatherObject();
                    weatherObject.cityID = c.getInt(c.getColumnIndex(BaseColumns._ID));
                    weatherObject.cityName = c.getString(c.getColumnIndex(CITY_NAME_COLUMN));
                    weatherObject.t = c.getInt(c.getColumnIndex(T_COLUMN));
                    weatherObject.maxT = c.getInt(c.getColumnIndex(MAX_T_COLUMN));
                    weatherObject.minT = c.getInt(c.getColumnIndex(MIN_T_COLUMN));

                    weatherObjects.add(weatherObject);
                } while (c.moveToNext());
            }
            return weatherObjects;
        }
        logCursor(c);
        c.close();
        db.close();
        return null;
    }

    public static ArrayList<WeatherObject> getCityWeeklyWeather(int cityID) {

        WeatherSQLiteHelper dbh = new WeatherSQLiteHelper(WeatherApp.getAppContext(), WEATHER_DATABASE, null, WEATHER_DATABASE_VERSION);
        SQLiteDatabase db = dbh.getWritableDatabase();


        Cursor c = db.rawQuery("SELECT " + CITIES_TABLE + "." + BaseColumns._ID + " as " + BaseColumns._ID + ", "
                + CITIES_TABLE + "." + CITY_NAME_COLUMN + " as " + CITY_NAME_COLUMN + ", "
                + DAILY_WEATHER_TABLE + "." + DATE_COLUMN + " as " + DATE_COLUMN + ", "
                + DAILY_WEATHER_TABLE + "." + MIN_T_COLUMN + " as " + MIN_T_COLUMN + ", "
                + DAILY_WEATHER_TABLE + "." + MAX_T_COLUMN + " as " + MAX_T_COLUMN
                + " FROM " + DAILY_WEATHER_TABLE
                + " INNER JOIN " + CITIES_TABLE
                + " ON " + CITIES_TABLE + "." + BaseColumns._ID + " = " + DAILY_WEATHER_TABLE + "." + CITY_ID_COLUMN
                + " WHERE " + DAILY_WEATHER_TABLE + "." + CITY_ID_COLUMN + " = " + cityID
                , null);


        ArrayList<WeatherObject> weatherObjects = new ArrayList<WeatherObject>();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    WeatherObject weatherObject = new WeatherObject();
                    weatherObject.cityID = c.getInt(c.getColumnIndex(BaseColumns._ID));
                    weatherObject.cityName = c.getString(c.getColumnIndex(CITY_NAME_COLUMN));
                    weatherObject.maxT = c.getInt(c.getColumnIndex(MAX_T_COLUMN));
                    weatherObject.minT = c.getInt(c.getColumnIndex(MIN_T_COLUMN));
                    weatherObject.date = c.getLong(c.getColumnIndex(DATE_COLUMN));

                    weatherObjects.add(weatherObject);
                } while (c.moveToNext());
            }
            return weatherObjects;
        }
//        logCursor(c);
        c.close();
        db.close();
        return null;
    }


    private static void setCurrentWeatherTableLastUpdateTime(long time) {


    }

    private static long getCurrentWeatherTableLastUpdateTime() {

        return 0;
    }

    private static void logCursor(Cursor c) {
        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d("main", str);
                } while (c.moveToNext());
            }
        } else
            Log.d("main", "Cursor is null");
    }

    public static void updateCitiesCurrentWeather(ArrayList<WeatherObject> list) {


        WeatherSQLiteHelper dbh = new WeatherSQLiteHelper(WeatherApp.getAppContext(), WEATHER_DATABASE, null, WEATHER_DATABASE_VERSION);
        SQLiteDatabase db = dbh.getWritableDatabase();

        db.delete(CURRENT_WEATHER_TABLE, null, null);


        for (int i = 0; i < list.size(); i++) {

            WeatherObject weatherObject = list.get(i);
            ContentValues rowValues = new ContentValues();
            rowValues.put(CITY_ID_COLUMN, weatherObject.cityID);
            rowValues.put(MIN_T_COLUMN, weatherObject.minT);
            rowValues.put(MAX_T_COLUMN, weatherObject.maxT);
            rowValues.put(T_COLUMN, weatherObject.t);
            db.insert(CURRENT_WEATHER_TABLE, null, rowValues);

        }

        db.close();

    }

    public static void updateCityWeeklyWeather(int cityID, ArrayList<WeatherObject> list) {


        WeatherSQLiteHelper dbh = new WeatherSQLiteHelper(WeatherApp.getAppContext(), WEATHER_DATABASE, null, WEATHER_DATABASE_VERSION);
        SQLiteDatabase db = dbh.getWritableDatabase();

        db.delete(DAILY_WEATHER_TABLE, CITY_ID_COLUMN + " = " + cityID, null);


        for (int i = 0; i < list.size(); i++) {

            WeatherObject weatherObject = list.get(i);
            ContentValues rowValues = new ContentValues();
            rowValues.put(CITY_ID_COLUMN, cityID);
            rowValues.put(DATE_COLUMN, weatherObject.date);
            rowValues.put(MIN_T_COLUMN, weatherObject.minT);
            rowValues.put(MAX_T_COLUMN, weatherObject.maxT);
            db.insert(DAILY_WEATHER_TABLE, null, rowValues);


        }

        db.close();
    }


    public static void addNewCity(WeatherObject weatherObject) {

        WeatherSQLiteHelper dbh = new WeatherSQLiteHelper(WeatherApp.getAppContext(), WEATHER_DATABASE, null, WEATHER_DATABASE_VERSION);
        SQLiteDatabase db = dbh.getWritableDatabase();

        ContentValues cityValues = new ContentValues();
        cityValues.put(BaseColumns._ID, weatherObject.cityID);
        cityValues.put(CITY_NAME_COLUMN, weatherObject.cityName);
        db.insert(CITIES_TABLE,null,cityValues);

        ContentValues currentWeatherValues = new ContentValues();
        currentWeatherValues.put(CITY_ID_COLUMN, weatherObject.cityID);
        currentWeatherValues.put(MIN_T_COLUMN, weatherObject.minT);
        currentWeatherValues.put(MAX_T_COLUMN, weatherObject.maxT);
        currentWeatherValues.put(T_COLUMN, weatherObject.t);
        db.insert(CURRENT_WEATHER_TABLE,null,currentWeatherValues);



    }
}




