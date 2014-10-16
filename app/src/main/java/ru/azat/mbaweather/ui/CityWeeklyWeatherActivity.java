package ru.azat.mbaweather.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import ru.azat.mbaweather.R;
import ru.azat.mbaweather.helpers.MainWeatherHelper;
import ru.azat.mbaweather.objects.WeatherObject;


public class CityWeeklyWeatherActivity extends Activity {


    ListView weatherListView;
    ProgressBar progressBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_weekly_weather);
        weatherListView = (ListView) findViewById(R.id.weatherList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        int cityID = getIntent().getIntExtra("cityID", 0);


        (new WeeklyWeatherLoadingTask()).execute(cityID);
    }


    private class WeeklyWeatherLoadingTask extends AsyncTask<Integer, Void, ArrayList<WeatherObject>> {

        @Override
        protected void onPreExecute() {

            weatherListView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<WeatherObject> doInBackground(Integer... params) {

            return MainWeatherHelper.getCityWeeklyWeather(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<WeatherObject> args) {

            if (args != null) {
                weatherListView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                weatherListView.setAdapter(new WeatherAdapter(CityWeeklyWeatherActivity.this, args));
            } else {
                showAlertDialog();
            }
        }
    }

    public static class WeatherAdapter extends ArrayAdapter<WeatherObject> {
        private final Activity context;
        private final ArrayList<WeatherObject> weathers;

        public WeatherAdapter(Activity context, ArrayList<WeatherObject> weathers) {
            super(context, R.layout.list_item_daily_weather, weathers);
            this.context = context;
            this.weathers = weathers;
        }

        static class ViewHolder {

            public TextView dateTV;
            public TextView minMaxTV;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.list_item_daily_weather, null, true);
                holder = new ViewHolder();
                holder.dateTV = (TextView) rowView.findViewById(R.id.dateTV);
                holder.minMaxTV = (TextView) rowView.findViewById(R.id.minMaxTV);
                rowView.setTag(holder);
            } else {
                holder = (ViewHolder) rowView.getTag();
            }

            holder.dateTV.setText(DateFormat.format("dd.MM.yy", new Date(weathers.get(position).date * 1000)).toString());
            holder.minMaxTV.setText(weathers.get(position).minT + "..." + weathers.get(position).maxT);
            return rowView;
        }
    }


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CityWeeklyWeatherActivity.this);
        builder.setTitle("Проблемы!")
                .setMessage("Не удалось загрузить данные о погоде!")

                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
