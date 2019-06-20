package com.example.sameershekhar.watherapp.network;

import com.example.sameershekhar.watherapp.BuildConfig;
import com.example.sameershekhar.watherapp.utils.Constant;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instance = null;
    private Retrofit retrofit;
    private OkHttpClient client;

    private WheatherServiceApi wheatherServiceApi;

    public RetrofitClient() {
        retrofit = new Retrofit.Builder().baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        wheatherServiceApi = retrofit.create(WheatherServiceApi.class);
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public WheatherServiceApi getWeatherService() {
        return wheatherServiceApi;
    }

}
