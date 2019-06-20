package com.example.sameershekhar.watherapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;

import com.example.sameershekhar.watherapp.model.WeatherResponse;

import java.util.List;

@Dao
@TypeConverters(DataConverter.class)
public interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWeatherInfo(WeatherResponse weatherResponses);

    @Query("SELECT * FROM WeatherResponse WHERE name = :cityName ")
    LiveData<WeatherResponse> getOneCityWeatherResponseFromLocalDbByCityName(String cityName);

    @Query("SELECT * FROM WeatherResponse ORDER BY id DESC")
    LiveData<List<WeatherResponse>> getAllCitiesWeatherResponseFromLocalDb();

    @Query("SELECT name FROM WeatherResponse ORDER BY id DESC")
    String[] getAllCitiesNamesWeatherResponseFromLocalDb();



    @Query("DELETE FROM WeatherResponse WHERE name = :cityName")
    void deleteCityWeatherInfoByCityName(String cityName);


}
