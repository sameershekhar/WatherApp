package com.example.sameershekhar.watherapp.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.work.WorkManager;

import com.example.sameershekhar.watherapp.db.WeatherDb;
import com.example.sameershekhar.watherapp.model.WeatherResponse;
import com.example.sameershekhar.watherapp.network.RetrofitClient;
import com.example.sameershekhar.watherapp.network.WheatherServiceApi;
import com.example.sameershekhar.watherapp.utils.Constant;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepo  {

    private WorkManager workManager = WorkManager.getInstance();
    private WheatherServiceApi wheatherServiceApi;
    private WeatherResponse weatherResponse;
    private static WeatherDb db;
    private static Context context;
    String[] saveedCityName=null;



    //update add dependency injection here
    public WeatherRepo(Context context){
        db=WeatherDb.getInstance(context);
        this.context=context;
    }

    public  void fetchDataFromServerAndUpdateLocalDb(String cityName){
       wheatherServiceApi=RetrofitClient.getInstance().getWeatherService();
       Call<WeatherResponse> weatherResponseCall = wheatherServiceApi.getWeatherDataByCityName(cityName,Constant.UINTS, Constant.APP_ID);
       weatherResponseCall.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
               weatherResponse = response.body();
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
        new DbAsyncTask(weatherResponse,0).execute();
    }

    public LiveData<WeatherResponse> getOneCityWeatherResponseFromLocalDb(String cityName){
        return db.weatherDao().getOneCityWeatherResponseFromLocalDbByCityName(cityName);
    }

    public LiveData<List<WeatherResponse>> getAllCitiesWeatherResponseFromLocalDb(){
        return db.weatherDao().getAllCitiesWeatherResponseFromLocalDb();
    }
    public void deleteCityWeatherInofByCityNameFormLocalDb(String cityName){
        new DbAsyncTask(cityName,1).execute();

    }

    public String[] getAllCitiesNamesWeatherResponseFromLocalDb() {
        return db.weatherDao().getAllCitiesNamesWeatherResponseFromLocalDb();
    }


    private static class DbAsyncTask extends AsyncTask<Void, Void, WeatherResponse> {
        WeatherResponse weatherResponse;
        int state;
        String cityName;
        public DbAsyncTask(WeatherResponse weatherResponse,int state){
            this.weatherResponse=weatherResponse;
            this.state=state;
        }
        public DbAsyncTask(String cityName,int state){
            this.cityName=cityName;
            this.state=state;
        }

       @Override
       protected WeatherResponse doInBackground(Void... voids) {
           try {
                    if(state==0){
                        db.weatherDao().insertWeatherInfo(weatherResponse);
                    }else if(state==1){
                        db.weatherDao().deleteCityWeatherInfoByCityName(cityName);
                    }
                } catch (Exception e){
                    Log.v("test8",e.getMessage());

                }

               return weatherResponse;
       }

       @Override
       protected void onPostExecute(WeatherResponse weatherResponse) {
           super.onPostExecute(weatherResponse);

       }
   }
   }
