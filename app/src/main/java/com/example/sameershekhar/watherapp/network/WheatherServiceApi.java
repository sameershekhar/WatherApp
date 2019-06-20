package com.example.sameershekhar.watherapp.network;

import com.example.sameershekhar.watherapp.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WheatherServiceApi {

  @GET("data/2.5/weather?")
  Call<WeatherResponse> getWeatherDataByCityName(@Query("q") String cityName,@Query("units") String units, @Query("appid") String app_id);

}
