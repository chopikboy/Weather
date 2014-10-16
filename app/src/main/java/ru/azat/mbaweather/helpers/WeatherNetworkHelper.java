package ru.azat.mbaweather.helpers;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.azat.mbaweather.objects.WeatherObject;

/**
 * Created by azat on 15.10.14.
 */
public class WeatherNetworkHelper {


    public static ArrayList<WeatherObject> getCitiesCurrentWeather(int[] cityIDs) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        String arrayString = Arrays.toString(cityIDs);
        arrayString = arrayString.substring(1, arrayString.length() - 1);
        Log.d("main", "ARRAY STRING" + arrayString);
        params.add(new BasicNameValuePair("id", arrayString));
        StringBuilder response = executeGetResponse(GROUP_WEATHER_URL, params);

        if (response == null) {
            return null;
        }
        ArrayList<WeatherObject> weatherList = new ArrayList<WeatherObject>();
        try {
            JSONObject mainJSON = new JSONObject(response.toString());
            JSONArray mainArray = mainJSON.getJSONArray("list");
            for (int i = 0; i < mainArray.length(); i++) {
                JSONObject itemJSON = mainArray.getJSONObject(i);
                JSONObject weatherJSON = itemJSON.getJSONObject("main");


                WeatherObject weatherObject = new WeatherObject();
                weatherObject.maxT = weatherJSON.getInt("temp_max");
                weatherObject.minT = weatherJSON.getInt("temp_min");
                weatherObject.t = weatherJSON.getInt("temp");
                weatherObject.cityID = itemJSON.getInt("id");
                weatherObject.cityName = itemJSON.getString("name");

                weatherList.add(weatherObject);
            }

            return weatherList;

        } catch (JSONException e) {

            return null;
        }

    }


    public static WeatherObject getCitiyCurrentWeather(String cityName) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("q", cityName));
        StringBuilder response = executeGetResponse(CURRENT_WEATHER_URL, params);

        if (response == null) {
            return null;
        }
        try {
            JSONObject mainJSON = new JSONObject(response.toString());

            JSONObject weatherJSON = mainJSON.getJSONObject("main");


            WeatherObject weatherObject = new WeatherObject();
            weatherObject.maxT = weatherJSON.getInt("temp_max");
            weatherObject.minT = weatherJSON.getInt("temp_min");
            weatherObject.t = weatherJSON.getInt("temp");
            weatherObject.cityID = mainJSON.getInt("id");
            weatherObject.cityName = mainJSON.getString("name");


            return weatherObject;

        } catch (JSONException e) {

            return null;
            // e.printStackTrace();
        }

    }

    public static ArrayList<WeatherObject> getCityWeeklyWeather(int cityID) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", String.valueOf(cityID)));
        StringBuilder response = executeGetResponse(WEEKLY_WEATHER_URL, params);

        if (response == null) {
            return null;
        }
        ArrayList<WeatherObject> weatherList = new ArrayList<WeatherObject>();
        try {
            JSONObject mainJSON = new JSONObject(response.toString());
            JSONArray mainArray = mainJSON.getJSONArray("list");
            for (int i = 0; i < mainArray.length(); i++) {
                JSONObject itemJSON = mainArray.getJSONObject(i);
                JSONObject weatherJSON = itemJSON.getJSONObject("temp");


                WeatherObject weatherObject = new WeatherObject();
                weatherObject.maxT = weatherJSON.getInt("max");
                weatherObject.minT = weatherJSON.getInt("min");
                weatherObject.date = itemJSON.getLong("dt");
                weatherList.add(weatherObject);
            }

            return weatherList;

        } catch (JSONException e) {
            return null;
        }
    }


    private static final String CURRENT_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?units=metric&";
    private static final String GROUP_WEATHER_URL = "http://api.openweathermap.org/data/2.5/group?units=metric&";
    private static final String WEEKLY_WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?cnt=7&units=metric&";


    public static StringBuilder executeGetResponse(String methodURL, List<NameValuePair> params) {

        HttpClient httpCient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(methodURL + createUrlValuesFromPairs(params));

        try {

            HttpResponse response = httpCient.execute(httpGet);
            StringBuilder sb = createStringFromInputStream(response.getEntity().getContent());
            Log.w("main", sb.toString());

            return sb;

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return null;
    }


    private static StringBuilder createStringFromInputStream(InputStream is) {
        String line;
        StringBuilder total = new StringBuilder();

        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return total;
    }

    private static String createUrlValuesFromPairs(List<NameValuePair> params) {
        try {
            StringBuilder result = new StringBuilder();


            if (params == null || params.size() == 0)
                return "";

            String key = params.get(0).getName();
            String value = params.get(0).getValue();
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value, "UTF-8"));

            for (int i = 0; i < params.size(); i++) {

                result.append("&");

                key = params.get(i).getName();
                value = params.get(i).getValue();


                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value, "UTF-8"));

            }


            return result.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


}
