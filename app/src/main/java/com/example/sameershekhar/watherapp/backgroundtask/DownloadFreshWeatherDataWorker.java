package com.example.sameershekhar.watherapp.backgroundtask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
        db=WeatherDb.getInstance(getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {

        String[] savedCitiesName=getInputData().getStringArray(Constant.SAVED_CITY_NAMES);
        for(int i=0 ;i <savedCitiesName.length ;i++){
            //weatherRepo.fetchDataFromServerAndUpdateLocalDb(savedCitiesName[i]);
            Toast.makeText(getApplicationContext(),savedCitiesName[i],Toast.LENGTH_LONG).show();
            wheatherServiceApi= RetrofitClient.getInstance().getWeatherService();
            Call<WeatherResponse> weatherResponseCall = wheatherServiceApi.getWeatherDataByCityName(savedCitiesName[i], Constant.APP_ID);

            weatherResponseCall.enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    try {
                        Log.v("test81",response.body().getName());
                        Toast.makeText(getApplicationContext(),response.body().getName()+response.body().getDt(),Toast.LENGTH_LONG).show();

                        // db.weatherDao().insertWeatherInfo(response.body());
                        new DbAsyncTask(response.body()).execute();
                    } catch (Exception e){
                        Log.v("test82",e.getMessage());

                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    Log.v("test80",t.getMessage());
                    Toast.makeText(getApplicationContext(),Constant.ERROR_MESSAGE,Toast.LENGTH_LONG).show();

                }
            });

        }
        return Result.success();
    }


    private static class DbAsyncTask extends AsyncTask<Void,Void,WeatherResponse> {
        WeatherResponse weatherResponse;
        public DbAsyncTask(WeatherResponse weatherResponse){
            this.weatherResponse=weatherResponse;
        }
        @Override
        protected WeatherResponse doInBackground(Void... voids) {
            try {
                db.weatherDao().insertWeatherInfo(weatherResponse);
            } catch (Exception e){
                Log.v("test8",e.getMessage());

            }

            return weatherResponse;
        }

        @Override
        protected void onPostExecute(WeatherResponse weatherResponse) {
            super.onPostExecute(weatherResponse);
            Log.v("test7",weatherResponse.getName());
           // Toast.makeText(context,weatherResponse.getName(),Toast.LENGTH_LONG).show();

        }
    }
}
