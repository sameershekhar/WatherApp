package com.example.sameershekhar.watherapp.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sameershekhar.watherapp.model.WeatherResponse;
import com.example.sameershekhar.watherapp.repository.WeatherRepo;
import com.example.sameershekhar.watherapp.utils.Constant;

import java.util.List;

public class WeatherDetailScreenViewModel extends AndroidViewModel {
    LiveData<WeatherResponse> oneCityWeatherInfo;
    private WeatherRepo weatherRepo;
    private String cityName;

    public WeatherDetailScreenViewModel(@NonNull Application application) {
        super(application);
        weatherRepo=new WeatherRepo(application.getApplicationContext());
    }



    public void setCityName(String cityName){
        this.cityName=cityName;
    }


    public LiveData<WeatherResponse> getCityWeatherInfoByCityName(){
        if(oneCityWeatherInfo == null){
            oneCityWeatherInfo = new MutableLiveData<>();
            loadOneCityWeatherInof();
        }
        return oneCityWeatherInfo;

    }

    private void loadOneCityWeatherInof(){
        oneCityWeatherInfo=weatherRepo.getOneCityWeatherResponseFromLocalDb(cityName);
    }

}
