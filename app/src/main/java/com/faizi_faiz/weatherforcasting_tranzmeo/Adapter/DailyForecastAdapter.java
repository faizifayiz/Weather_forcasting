package com.faizi_faiz.weatherforcasting_tranzmeo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass.DailyWeatherModel;
import com.faizi_faiz.weatherforcasting_tranzmeo.R;
import com.faizi_faiz.weatherforcasting_tranzmeo.WeatherDetailActivity;

import java.util.List;
import java.util.Locale;

public class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.ViewHolder> {

    private Context context;
    private List<DailyWeatherModel> dailyList;

    public DailyForecastAdapter(Context context, List<DailyWeatherModel> dailyList) {
        this.context = context;
        this.dailyList = dailyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_daily_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DailyWeatherModel model = dailyList.get(position);

        holder.tvDate.setText(model.getDate());
        holder.tvTemp.setText(String.format(Locale.getDefault(), "↑%.0f° ↓%.0f°", model.getMaxTemp(), model.getMinTemp()));
        holder.tvDescription.setText(model.getDescription());

        String iconUrl = "https://openweathermap.org/img/wn/" + model.getIcon() + "@2x.png";
        Glide.with(context).load(iconUrl).into(holder.ivIcon);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WeatherDetailActivity.class);
            intent.putExtra("daily_weather", model);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dailyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTemp, tvDescription;
        ImageView ivIcon;

        ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTemp = itemView.findViewById(R.id.tv_temp);
            tvDescription = itemView.findViewById(R.id.tv_description);
            ivIcon = itemView.findViewById(R.id.iv_icon);
        }
    }
}
