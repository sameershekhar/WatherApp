package com.example.sameershekhar.watherapp.viewmodel;

import android.app.Application;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.sameershekhar.watherapp.backgroundtask.DownloadFreshWeatherDataWorker;
import com.example.sameershekhar.watherapp.model.WeatherResponse;
import com.example.sameershekhar.watherapp.repository.WeatherRepo;
import com.example.sameershekhar.watherapp.utils.Constant;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeScreenViewModel extends AndroidViewModel {

    private String cityName;
    private String[] savedCityNames;
    LiveData<WeatherResponse> oneCityWeatherInfo;
    LiveData<List<WeatherResponse>> allSavedCitiesWeatherResponse;
    WeatherResponse weatherResponse;
    private SavedStateHandle mState;
    private WeatherRepo weatherRepo;


    public HomeScreenViewModel(@NonNull Application application) {
        super(application);
        weatherRepo=new WeatherRepo(getApplication().getApplicationContext());
    }

    public LiveData<List<WeatherResponse>> getAllSavedCitiesWeatherInfo() {
        if (allSavedCitiesWeatherResponse == null) {
            allSavedCitiesWeatherResponse = new MutableLiveData<List<WeatherResponse>>();
            loadAllCitiesWeatherInof();
        }
        return allSavedCitiesWeatherResponse;
    }

    private void loadAllCitiesWeatherInof() {
       allSavedCitiesWeatherResponse =  weatherRepo.getAllCitiesWeatherResponseFromLocalDb();

    }

    public void deleteCityWeatherInfoByName(String cityName){
        weatherRepo.deleteCityWeatherInofByCityNameFormLocalDb(cityName);
    }


    public void setSearchedPlaceName(String searcheCotyName) {
        cityName=searcheCotyName;
        weatherRepo.fetchDataFromServerAndUpdateLocalDb(cityName);
    }


    public void startWorker(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                savedCityNames = weatherRepo.getAllCitiesNamesWeatherResponseFromLocalDb();
            }
        });

        if(savedCityNames !=null && savedCityNames.length >0){
           Data savedCityNamesData = new Data.Builder()
                   .putStringArray(Constant.SAVED_CITY_NAMES, savedCityNames)
                   .build();

           Constraints constraints = new Constraints.Builder()
                   .setRequiredNetworkType(NetworkType.CONNECTED)
                   .setRequiresBatteryNotLow(true)
                   .build();
           PeriodicWorkRequest  fetchNewsData =
                   new PeriodicWorkRequest.Builder(DownloadFreshWeatherDataWorker.class, 10, TimeUnit.MINUTES)
                           .setConstraints(constraints)
                           .setInputData(savedCityNamesData)
                           .build();

           WorkManager.getInstance().enqueueUniquePeriodicWork(Constant.WORKER_TAG, ExistingPeriodicWorkPolicy.KEEP , fetchNewsData);
       }
   }

    public void stopWorker() {
        WorkManager.getInstance().cancelAllWorkByTag(Constant.WORKER_TAG);
    }




}
