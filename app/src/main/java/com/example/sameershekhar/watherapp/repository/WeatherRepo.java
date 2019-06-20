package com.example.sameershekhar.watherapp.repository;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.sameershekhar.watherapp.backgroundtask.DownloadFreshWeatherDataWorker;
import com.example.sameershekhar.watherapp.db.WeatherDb;
import com.example.sameershekhar.watherapp.model.WeatherResponse;
import com.example.sameershekhar.watherapp.network.RetrofitClient;
import com.example.sameershekhar.watherapp.network.WheatherServiceApi;
import com.example.sameershekhar.watherapp.utils.Constant;
import com.example.sameershekhar.watherapp.view.HomeScreen;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WeatherRepo  {

    private WorkManager workManager = WorkManager.getInstance();
    private WheatherServiceApi wheatherServiceApi;
    private WeatherResponse weatherResponse;
    private static WeatherDb db;
    private static Context context;


    //update add dependency injection here
    public WeatherRepo(Context context){
        db=WeatherDb.getInstance(context);
        this.context=context;
    }


    //WorkManager syncing the local room db with server every hour if and only if internet is connected and
    //device is charging

    public  void fetchDataFromServerAndUpdateLocalDb(String cityName){
       // Log.v("test3",cityName);
       wheatherServiceApi=RetrofitClient.getInstance().getWeatherService();
       Call<WeatherResponse> weatherResponseCall = wheatherServiceApi.getWeatherDataByCityName(cityName, Constant.APP_ID);


        weatherResponseCall.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
               weatherResponse = response.body();
                //Toast.makeText(context,"Updating"+weatherResponse.getName(),Toast.LENGTH_LONG).show();

                updateLocalDb(weatherResponse);
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
               weatherResponse = null;
                Toast.makeText(context,Constant.ERROR_MESSAGE,Toast.LENGTH_LONG).show();

            }
        });

    }

    public void updateLocalDb(final WeatherResponse weatherResponse){
        new DbAsyncTask(weatherResponse).execute();
    }

    public LiveData<WeatherResponse> getOneCityWeatherResponseFromLocalDb(String cityName){
        return db.weatherDao().getOneCityWeatherResponseFromLocalDbByCityName(cityName);
    }

    public LiveData<List<WeatherResponse>> getAllCitiesWeatherResponseFromLocalDb(){
       // Log.v("test6",db.weatherDao().getAllCitiesWeatherResponseFromLocalDb().getValue().get(0).getName().toString());
        return db.weatherDao().getAllCitiesWeatherResponseFromLocalDb();
    }

    public String[] getSavedCityNames(){
        String[] savedCityNames=null;
        List<WeatherResponse> weatherResponseList=getAllCitiesWeatherResponseFromLocalDb().getValue();
        if(weatherResponseList!= null && weatherResponseList.size() > 0){
            savedCityNames=new String[weatherResponseList.size()];
            for(int i=0 ; i < weatherResponseList.size() ; i++){
                 savedCityNames[i]=weatherResponseList.get(i).getName();
            }
        }
        return savedCityNames;

    }


   private static class DbAsyncTask extends AsyncTask<Void,Void,WeatherResponse>{
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
           Toast.makeText(context,weatherResponse.getName(),Toast.LENGTH_LONG).show();

       }
   }
   }
