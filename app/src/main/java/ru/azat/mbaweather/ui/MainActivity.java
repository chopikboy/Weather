package ru.azat.mbaweather.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ru.azat.mbaweather.R;
import ru.azat.mbaweather.helpers.MainWeatherHelper;
import ru.azat.mbaweather.objects.WeatherObject;


public class MainActivity extends ActionBarActivity {

    ListView weatherListView;
    Button addBtn;
    ProgressBar progressBar;
    ArrayAdapter weatherListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherListView = (ListView) findViewById(R.id.weatherList);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        addBtn = (Button)findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CityAddingDialog dialog = new CityAddingDialog(MainActivity.this);
                dialog.setOnCityChosedListener(new CityAddingDialog.OnCityChosedListener() {
                    @Override
                    public void onCityChoose(String city) {

                        (new NewCityAddingTask()).execute(city);
                    }
                });
                dialog.show();
            }
        });

        (new CurrentWeatherLoadingTask()).execute();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class CurrentWeatherLoadingTask extends AsyncTask<Void, Void, ArrayList<WeatherObject>> {

        @Override
        protected void onPreExecute() {

            addBtn.setVisibility(View.INVISIBLE);
            weatherListView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<WeatherObject> doInBackground(Void... params) {

            return MainWeatherHelper.getCitiesCurrentWeather();
        }

        @Override
        protected void onPostExecute(ArrayList<WeatherObject> args) {

            progressBar.setVisibility(View.GONE);

            if(args!=null && args.size()!=0) {
                addBtn.setVisibility(View.VISIBLE);
                weatherListView.setVisibility(View.VISIBLE);
                weatherListViewAdapter = new WeatherAdapter(MainActivity.this, args);
                weatherListView.setAdapter(weatherListViewAdapter);
            }
            else {
                showAllCityAlertDialog();
            }




        }
    }

    private class NewCityAddingTask extends AsyncTask<String, Void, WeatherObject> {

        @Override
        protected void onPreExecute() {
            addBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();

        }

        @Override
        protected WeatherObject doInBackground(String... params) {

            return MainWeatherHelper.getCityCurrentWeather(params[0]);
        }

        @Override
        protected void onPostExecute(WeatherObject weatherObject) {

            addBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            if(weatherObject!=null) {
                weatherListViewAdapter.add(weatherObject);
                weatherListViewAdapter.notifyDataSetChanged();
            }
            else {
                showOneCityAlertDialog();
            }



        }
    }


    public class WeatherAdapter extends ArrayAdapter<WeatherObject> {
        private final Activity context;
        private final ArrayList<WeatherObject> weathers;

        public WeatherAdapter(Activity context, ArrayList<WeatherObject> weathers) {
            super(context, R.layout.list_item_daily_weather, weathers);
            this.context = context;
            this.weathers = weathers;
        }

        private class ViewHolder {
            public int cityID;
            public TextView cityTV;
            public TextView tTV;
            public TextView minMaxTV;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.list_item_current_weather, null, true);
                holder = new ViewHolder();
                holder.cityTV = (TextView) rowView.findViewById(R.id.cityTV);
                holder.tTV = (TextView) rowView.findViewById(R.id.tTV);
                holder.minMaxTV = (TextView) rowView.findViewById(R.id.minMaxTV);
                rowView.setOnClickListener(onWeatherItemClick);
                rowView.setTag(holder);
            } else {
                holder = (ViewHolder) rowView.getTag();
            }

            holder.cityID = weathers.get(position).cityID;
            holder.cityTV.setText(weathers.get(position).cityName);
            holder.tTV.setText(weathers.get(position).t+"");
            holder.minMaxTV.setText(weathers.get(position).minT + "..." + weathers.get(position).maxT);

            return rowView;
        }

        private View.OnClickListener onWeatherItemClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cityID = ((ViewHolder)view.getTag()).cityID;

                Intent intent = new Intent(MainActivity.this,CityWeeklyWeatherActivity.class);

                intent.putExtra("cityID", cityID);

                startActivity(intent);
            }
        };

    }

    private void showAllCityAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Проблемы!")
                .setMessage("Не удалось загрузить данные о погоде!")

                .setCancelable(true)
                .setNegativeButton("Попробовать еще",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                (new CurrentWeatherLoadingTask()).execute();

                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void showOneCityAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Проблемы!")
                .setMessage("Не удалось загрузить данные о погоде в данном городе!")

                .setCancelable(true)
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
