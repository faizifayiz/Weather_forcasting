package com.faizi_faiz.weatherforcasting_tranzmeo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass.HourlyWeatherModel;
import com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass.WeatherModel;
import com.faizi_faiz.weatherforcasting_tranzmeo.R;

import java.util.List;
import java.util.Locale;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {

    private final List<HourlyWeatherModel> hourlyList;
    private final Context context;

    public HourlyAdapter(Context context, List<HourlyWeatherModel> hourlyList) {
        this.context = context;
        this.hourlyList = hourlyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hourly, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HourlyWeatherModel weather = hourlyList.get(position);

        holder.tvHour.setText(weather.getTime());
        holder.tvTemp.setText(String.format(Locale.getDefault(), "%.0f°", weather.getTemp()));
        holder.tvPrecip.setText(weather.getPrecipitation() + "%");

        String iconUrl = "https://openweathermap.org/img/wn/" + weather.getIcon() + "@2x.png";
        Glide.with(context)
                .load(iconUrl)
                .placeholder(R.drawable.ic_baseline_wb_sunny_24)
                .into(holder.ivIcon);
    }

    @Override
    public int getItemCount() {
        return hourlyList != null ? hourlyList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHour, tvTemp, tvPrecip;
        ImageView ivIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            tvHour = itemView.findViewById(R.id.tv_hour);
            tvTemp = itemView.findViewById(R.id.tv_hour_temp);
            tvPrecip = itemView.findViewById(R.id.tv_hour_precip);
            ivIcon = itemView.findViewById(R.id.iv_hour_icon);
        }
    }
}
