package com.example.sameershekhar.watherapp.backgroundtask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sameershekhar.watherapp.db.WeatherDb;
import com.example.sameershekhar.watherapp.model.WeatherResponse;
import com.example.sameershekhar.watherapp.network.RetrofitClient;
import com.example.sameershekhar.watherapp.network.WheatherServiceApi;
import com.example.sameershekhar.watherapp.repository.WeatherRepo;
import com.example.sameershekhar.watherapp.utils.Constant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadFreshWeatherDataWorker extends Worker {
    private WheatherServiceApi wheatherServiceApi;
    private static WeatherDb db;



    public DownloadFreshWeatherDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

    }

    @NonNull
    @Override
    public Result doWork() {

        String[] savedCitiesName=getInputData().getStringArray(Constant.SAVED_CITY_NAMES);
        db=WeatherDb.getInstance(getApplicationContext());
        final Data[] outpustData = new Data[1];
        WeatherResponse weatherResponse;
        for(int i=0 ;i <savedCitiesName.length ;i++){

            wheatherServiceApi= RetrofitClient.getInstance().getWeatherService();
            Call<WeatherResponse> weatherResponseCall = wheatherServiceApi.getWeatherDataByCityName(savedCitiesName[i], Constant.UINTS,Constant.APP_ID);
            weatherResponseCall.enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    try {
                        db.weatherDao().insertWeatherInfo(response.body());

                    } catch (Exception e){

                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {

                }
            });

        }
        return Result.success();
    }

}
