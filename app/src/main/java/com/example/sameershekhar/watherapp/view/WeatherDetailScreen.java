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
import java.util.Date;

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
            weatherDetailScreenViewModel.setCityName(getIntent().getStringExtra(Constant.USER_SELECTED_CITY_NAME));
            weatherDetailScreenViewModel.getCityWeatherInfoByCityName().observe(this, new Observer<WeatherResponse>() {
                @Override
                public void onChanged(WeatherResponse weatherResponse) {
                    maxTemp.setText(weatherResponse.getMain().getTempMax().toString()+(char) 0x00B0+" c");
                    minTemp.setText(weatherResponse.getMain().getTempMin().toString()+(char) 0x00B0+" c");
                    pressure.setText(weatherResponse.getMain().getPressure().toString()+" hPa");
                    humidity.setText(weatherResponse.getMain().getHumidity().toString()+" %");
                    windSpeed.setText(weatherResponse.getWind().getSpeed().toString()+" m/s");
                    lastUpdated.setText(getFormatedTime(weatherResponse.getDt()));
                }
            });
        }else {
            Log.v("test8","nothing");

        }

    }


    private String getFormatedTime(long time){
        String[] strDays = new String[] { "Sunday", "Monday", "Tuesday","Wednesday", "Thursday","Friday", "Saturday" };
        String dateformate="";
        if(time!=0){
            Timestamp ts=new Timestamp(time);
            Date date=new Date(time*1000);
            int hours=date.getHours();
            int minutes=date.getMinutes();
            int day=date.getDay();
            dateformate+=strDays[day]+", ";
            if(hours<10){
                dateformate+="0"+hours+": ";
            }else {
                dateformate+=hours+": ";
            }

            if(minutes<10){
                dateformate+="0"+minutes;
            }else {
                dateformate+=minutes;
            }

            if(hours<12)
            {
                dateformate+=" AM";
            }else {
                dateformate+=" PM";
            }
            return  dateformate;

        }else {
            return "";
        }

    }
}
