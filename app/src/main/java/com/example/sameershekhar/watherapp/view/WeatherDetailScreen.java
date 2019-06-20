package com.example.sameershekhar.watherapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sameershekhar.watherapp.R;
import com.example.sameershekhar.watherapp.model.WeatherResponse;
import com.example.sameershekhar.watherapp.utils.Constant;
import com.example.sameershekhar.watherapp.viewmodel.HomeScreenViewModel;
import com.example.sameershekhar.watherapp.viewmodel.WeatherDetailScreenViewModel;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherDetailScreen extends AppCompatActivity {

    @BindView(R.id.max_temp_value)
    TextView maxTemp;

    @BindView(R.id.min_temp_value)
    TextView minTemp;

    @BindView(R.id.pressure_value)
    TextView pressure;

    @BindView(R.id.wind_speed_value)
    TextView windSpeed;

    @BindView(R.id.last_updated_value)
    TextView lastUpdated;

    @BindView(R.id.humidity_value)
    TextView humidity;

    private WeatherDetailScreenViewModel weatherDetailScreenViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail_screen);
        ButterKnife.bind(this);
        weatherDetailScreenViewModel= ViewModelProviders.of(this).get(WeatherDetailScreenViewModel.class);


        if(getIntent()!=null){
          //  Toast.makeText(this,getIntent().getStringExtra(Constant.USER_SELECTED_CITY_NAME),Toast.LENGTH_LONG).show();
            //Log.v("test9",getIntent().getStringExtra(Constant.USER_SELECTED_CITY_NAME));
            weatherDetailScreenViewModel.setCityName(getIntent().getStringExtra(Constant.USER_SELECTED_CITY_NAME));
            weatherDetailScreenViewModel.getCityWeatherInfoByCityName().observe(this, new Observer<WeatherResponse>() {
                @Override
                public void onChanged(WeatherResponse weatherResponse) {

                    Log.v("test10",weatherResponse.getName());
                    maxTemp.setText(weatherResponse.getMain().getTempMax().toString());
                    minTemp.setText(weatherResponse.getMain().getTempMin().toString());
                    pressure.setText(weatherResponse.getMain().getPressure().toString());
                    humidity.setText(weatherResponse.getMain().getHumidity().toString());
                    windSpeed.setText(weatherResponse.getWind().getSpeed().toString());
                    lastUpdated.setText(getFormatedTime(weatherResponse.getDt()));
                }
            });
        }else {
           // Toast.makeText(this,"nothing",Toast.LENGTH_LONG).show();
            Log.v("test8","nothing");

        }

    }


    private String getFormatedTime(long time){
        if(time!=0){
            Timestamp ts=new Timestamp(time);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.format(ts);
        }else {
            return "";
        }

    }
}
