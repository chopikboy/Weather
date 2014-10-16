package ru.azat.mbaweather.helpers;

import java.util.ArrayList;

import ru.azat.mbaweather.objects.WeatherObject;

/**
 * Created by azat on 15.10.14.
 */
public class MainWeatherHelper {


    public static boolean isHaveInetConnection() {

        return true;
    }

    public static ArrayList<WeatherObject> getCityWeeklyWeather(int cityID) {
        ArrayList<WeatherObject> list = null;
        if (isHaveInetConnection())
            list = WeatherNetworkHelper.getCityWeeklyWeather(cityID);

        if (list == null) {
            list = WeatherSQLiteHelper.getCityWeeklyWeather(cityID);
        }
        else {
            WeatherSQLiteHelper.updateCityWeeklyWeather(cityID, list);
        }
        return list;

    }

    public static ArrayList<WeatherObject> getCitiesCurrentWeather() {


        int cityIDs[] = WeatherSQLiteHelper.getCityIDs();


       ArrayList <WeatherObject> list = null;
        if (isHaveInetConnection())
            list = WeatherNetworkHelper.getCitiesCurrentWeather(cityIDs);

        if (list == null) {
            list = WeatherSQLiteHelper.getAllCitiesCurrentWeather();
        }
        else {
            WeatherSQLiteHelper.updateCitiesCurrentWeather(list);
        }
        return list;
    }


    public static WeatherObject getCityCurrentWeather(String cityName) {




        WeatherObject weatherObject = null;
        if (isHaveInetConnection())
            weatherObject = WeatherNetworkHelper.getCitiyCurrentWeather(cityName);

        if (weatherObject != null) {

            WeatherSQLiteHelper.addNewCity(weatherObject);
        }
        return weatherObject;
    }


}
