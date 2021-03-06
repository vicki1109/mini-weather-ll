package com.example.sunfiyes.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sunfiyes.bean.TodayWeather;
import com.example.sunfiyes.myapplication.R;

/**
 * Created by sunfiyes on 2016/12/26 0026.
 */

public class SecondWeatherFragment extends Fragment {
    private TextView weekTv1, weekTv2, weekTv3;
    private ImageView weather_imgIv1, weather_imgIv2, weather_imgIv3;
    private TextView temperatureTv1, temperatureTv2, temperatureTv3;
    private TextView climateTv1, climateTv2, climateTv3;
    private TextView windTv1, windTv2, windTv3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.weather_item,
                container, false);
        View view1 = view.findViewById(R.id.subitem1);
        View view2 = view.findViewById(R.id.subitem2);
        View view3 = view.findViewById(R.id.subitem3);

        weekTv1 = (TextView) view1.findViewById(R.id.week);
        weekTv2 = (TextView) view2.findViewById(R.id.week);
        weekTv3 = (TextView) view3.findViewById(R.id.week);

        weather_imgIv1 = (ImageView) view1.findViewById(R.id.weather_img);
        weather_imgIv2 = (ImageView) view2.findViewById(R.id.weather_img);
        weather_imgIv3 = (ImageView) view3.findViewById(R.id.weather_img);
        temperatureTv1 = (TextView) view1.findViewById(R.id.temperature);
        temperatureTv2 = (TextView) view2.findViewById(R.id.temperature);
        temperatureTv3 = (TextView) view3.findViewById(R.id.temperature);

        climateTv1 = (TextView) view1.findViewById(R.id.climate);
        climateTv2 = (TextView) view2.findViewById(R.id.climate);
        climateTv3 = (TextView) view3.findViewById(R.id.climate);

        windTv1 = (TextView) view1.findViewById(R.id.wind);
        windTv2 = (TextView) view2.findViewById(R.id.wind);
        windTv3 = (TextView) view3.findViewById(R.id.wind);

        weekTv1.setText("星期五");
        weekTv2.setText("星期六");
        weekTv3.setText("星期日");
        climateTv1.setText("N/A");
        climateTv2.setText("N/A");
        climateTv3.setText("N/A");

        temperatureTv1.setText("N/A");
        temperatureTv2.setText("N/A");
        temperatureTv3.setText("N/A");

        windTv1.setText("N/A");
        windTv2.setText("N/A");
        windTv3.setText("N/A");
        return view;
    }

    public void updateWeather(TodayWeather weatherinfo) {
        if (weatherinfo != null) {
            climateTv1.setText(weatherinfo.getQuality());
            climateTv2.setText(weatherinfo.getQuality());
            climateTv3.setText(weatherinfo.getQuality());

            temperatureTv1.setText(weatherinfo.getWendu());
            temperatureTv2.setText(weatherinfo.getWendu());
            temperatureTv3.setText(weatherinfo.getWendu());

            windTv1.setText(weatherinfo.getFengli());
            windTv2.setText(weatherinfo.getFengli());
            windTv3.setText(weatherinfo.getFengli());

        } else {
            climateTv1.setText("N/A");
            climateTv2.setText("N/A");
            climateTv3.setText("N/A");

            temperatureTv1.setText("N/A");
            temperatureTv2.setText("N/A");
            temperatureTv3.setText("N/A");

            windTv1.setText("N/A");
            windTv2.setText("N/A");
            windTv3.setText("N/A");
        }
    }

}

