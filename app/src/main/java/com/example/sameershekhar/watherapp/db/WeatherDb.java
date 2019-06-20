package com.example.sameershekhar.watherapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.sameershekhar.watherapp.model.WeatherResponse;

@Database(entities = {WeatherResponse.class},version = 1,exportSchema = false)
@TypeConverters({DataConverter.class})

public abstract class WeatherDb extends RoomDatabase {
    private static volatile WeatherDb weatherDbInstance;
    private static final String DATABASE_NAME = "WeatherDB";


    public abstract WeatherDao weatherDao();

    //we can have a single database instance for the entire application
    public static synchronized WeatherDb getInstance(Context context) {
        if (weatherDbInstance == null) {
            weatherDbInstance = Room.databaseBuilder(context,WeatherDb.class, DATABASE_NAME).build();
        }
        return weatherDbInstance;
    }
}

