package com.example.sameershekhar.watherapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateVMFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sameershekhar.watherapp.R;
import com.example.sameershekhar.watherapp.adapter.HomeScreenAdapter;
import com.example.sameershekhar.watherapp.backgroundtask.DownloadFreshWeatherDataWorker;
import com.example.sameershekhar.watherapp.interfaces.HomeScreenCityClickListner;
import com.example.sameershekhar.watherapp.model.WeatherResponse;
import com.example.sameershekhar.watherapp.utils.Constant;
import com.example.sameershekhar.watherapp.viewmodel.HomeScreenViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeScreen extends AppCompatActivity implements HomeScreenCityClickListner{
    private Button button;
    private EditText cityName;
    private HomeScreenViewModel weatherViewModel;
    private RecyclerView recyclerView;
    private HomeScreenAdapter homeScreenAdapter;
    private RecyclerView.LayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        button=findViewById(R.id.searchBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(cityName.getText().toString())){
                    weatherViewModel.setSearchedPlaceName(cityName.getText().toString());
                    cityName.setText("");
                }
            }
        });
        cityName=findViewById(R.id.city_name);
        recyclerView=findViewById(R.id.home_screen_recycler_view);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        homeScreenAdapter=new HomeScreenAdapter(this);
        recyclerView.setAdapter(homeScreenAdapter);


        weatherViewModel= ViewModelProviders.of(this).get(HomeScreenViewModel.class);
        weatherViewModel.getAllSavedCitiesWeatherInfo().observe(this, new Observer<List<WeatherResponse>>() {
            @Override
            public void onChanged(List<WeatherResponse> weatherResponses) {
                 homeScreenAdapter.setData(weatherResponses);

            }
        });
        weatherViewModel.startWorker();


    }

    @Override
    public void onSavedCityClick(String cityName) {
        Intent intent=new Intent(HomeScreen.this,WeatherDetailScreen.class);
        intent.putExtra(Constant.USER_SELECTED_CITY_NAME,cityName);
        startActivity(intent);

    }
    @Override
    public void onSavedCityDelete(String cityName) {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("confirm deletion");
        builder.setMessage("All saved weather information will be deleted for city "+cityName );
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                weatherViewModel.deleteCityWeatherInfoByName(cityName);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        weatherViewModel.stopWorker();

    }
}
