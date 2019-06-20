package com.example.sameershekhar.watherapp.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.sameershekhar.watherapp.R;
import com.example.sameershekhar.watherapp.interfaces.HomeScreenCityClickListner;
import com.example.sameershekhar.watherapp.model.WeatherResponse;
import com.example.sameershekhar.watherapp.view.HomeScreen;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;

public class HomeScreenAdapter extends RecyclerView.Adapter<HomeScreenAdapter.MyViewHolder> {

    private Context context;
    private List<WeatherResponse> weatherResponseList;
    private HomeScreenCityClickListner homeScreenCityClickListner;

    public HomeScreenAdapter(Context context) {
        this.context = context;
        homeScreenCityClickListner=(HomeScreen)context;

    }

    public void setData(List<WeatherResponse> newData) {
        this.weatherResponseList = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
     View view= LayoutInflater.from(context).inflate(R.layout.home_screen_recycler_item,viewGroup,false);
     return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {
         myViewHolder.cityName.setText(weatherResponseList.get(position).getName());
         myViewHolder.cityTemp.setText(weatherResponseList.get(position).getMain().getTemp().toString()+(char) 0x00B0+" c");
         myViewHolder.lastUpdatedDate.setText(getFormatedTime(weatherResponseList.get(position).getDt()));
         myViewHolder.view.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 homeScreenCityClickListner.onSavedCityClick(weatherResponseList.get(position).getName());
             }
         });
         myViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View view) {
                 homeScreenCityClickListner.onSavedCityDelete(weatherResponseList.get(position).getName());
                 return true;
             }
         });
    }

    @Override
    public int getItemCount() {
        return weatherResponseList!=null ? weatherResponseList.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cityName,cityTemp,lastUpdatedDate;
        View view;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            cityName =itemView.findViewById(R.id.cityName);
            cityTemp =itemView.findViewById(R.id.cityTemp);
            lastUpdatedDate =itemView.findViewById(R.id.latsUpdateDate);

        }
    }

    private String getFormatedTime(long time){
        String[] strDays = new String[] { "Sunday", "Monday", "Tuesday","Wednesday", "Thursday","Friday", "Saturday" };
        String dateformate="";
        if(time!=0){
            Timestamp ts=new Timestamp(time);
            Date date=new Date(time*1000);
            int hours=date.getHours();
            int minutes=date.getMinutes();
            int seconds=date.getSeconds();
            int day=date.getDay();
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            return formatter.format(ts);
            dateformate+=strDays[day]+", ";

            if(hours<10){
                dateformate+="0"+hours+": ";
            }else {
                dateformate+=hours+": ";
            }

            if(minutes<10){
                dateformate+="0"+minutes;
            }else {
                dateformate+=minutes;
            }

            if(hours<12)
            {
                dateformate+=" AM";
            }else {
                dateformate+=" PM";
            }
            return  dateformate;

        }else {
            return "";
        }

    }
}
