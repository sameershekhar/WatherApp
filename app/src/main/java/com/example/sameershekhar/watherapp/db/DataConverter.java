package com.example.sameershekhar.watherapp.db;

import androidx.room.TypeConverter;

import com.example.sameershekhar.watherapp.model.Weather;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataConverter {

    @TypeConverter
    public static List<Weather> fromString(String value) {
        Type listType = new TypeToken<List<Weather>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String fromArrayLisr(List<Weather> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }


}
