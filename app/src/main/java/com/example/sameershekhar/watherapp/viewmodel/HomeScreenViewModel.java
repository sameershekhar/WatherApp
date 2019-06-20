package com.example.sameershekhar.watherapp.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.sameershekhar.watherapp.backgroundtask.DownloadFreshWeatherDataWorker;
import com.example.sameershekhar.watherapp.model.WeatherResponse;
import com.example.sameershekhar.watherapp.repository.WeatherRepo;
import com.example.sameershekhar.watherapp.utils.Constant;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class HomeScreenViewModel extends AndroidViewModel {

    private String cityName;
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

       // Do an asynchronous operation to fetch users.
    }


    public void setSearchedPlaceName(String searcheCotyName) {
        cityName=searcheCotyName;
        Log.v("test2",searcheCotyName);
        weatherRepo.fetchDataFromServerAndUpdateLocalDb(cityName);
    }

    public void startWorker(){
           // String[] savedCityNames=weatherRepo.getSavedCityNames();
            String[] savedCityNames={"Bangalore"};
                Log.v("test20",savedCityNames[0]);
                if(savedCityNames!=null && savedCityNames.length >0){
                    Data savedCityNamesData = new Data.Builder()
                            .putStringArray(Constant.SAVED_CITY_NAMES,savedCityNames )
                            .build();

                    Constraints constraints = new Constraints.Builder()
                            .setRequiresCharging(true)
                            .build();
                    PeriodicWorkRequest fetchNewsData =
                            new PeriodicWorkRequest.Builder(DownloadFreshWeatherDataWorker.class, 30, TimeUnit.SECONDS)
                                    .setConstraints(constraints)
                                    .setInputData(savedCityNamesData)
                                    .build();

//                    WorkManager.getInstance().enqueue(fetchNewsData);
                    WorkManager.getInstance().enqueueUniquePeriodicWork(Constant.WORKER_TAG, ExistingPeriodicWorkPolicy.KEEP , fetchNewsData);
                }

    }

    public void stopWorker() {
        WorkManager.getInstance().cancelAllWorkByTag(Constant.WORKER_TAG);

    }
}
