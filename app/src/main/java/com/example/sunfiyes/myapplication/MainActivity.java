package com.example.sunfiyes.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunfiyes.app.MyApp;
import com.example.sunfiyes.bean.City;
import com.example.sunfiyes.bean.TodayWeather;
import com.example.sunfiyes.fragment.FirstWeatherFragment;
import com.example.sunfiyes.fragment.SecondWeatherFragment;
import com.example.sunfiyes.util.NetUtil;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDNotifyListener;//假如用到位置提醒功能，需要import该类
import com.baidu.location.Poi;
/**
 * Created by sunfiyes on 2016/9/27 0027.
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private ImageView mUpdateaBtn;//图标控件
    private ImageView mCitySelect;

    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, temperature_range_Tv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;

    private WeatherPagerAdapter mWeatherPagerAdapter;
    private ViewPager mViewPager;
    private List<Fragment> fragments;
    public LocationClient mLocationClient = null;

    //通过消息机制，将解析的天气对象，通过消息发送给主线程，主线程接收到消息数据后，调用函数更新UI界面上的数据。
    private static final int UPDATE_TODAY_WEATHER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateaBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateaBtn.setOnClickListener(this);

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
        }

        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        initView();

        fragments = new ArrayList<Fragment>();
        fragments.add(new FirstWeatherFragment());
        fragments.add(new SecondWeatherFragment());
        mViewPager = (ViewPager) this.findViewById(R.id.viewpager);
        mWeatherPagerAdapter = new WeatherPagerAdapter(
                getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mWeatherPagerAdapter);

        mLocationClient = new LocationClient(this); // 声明LocationClient类
        // 注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setOpenGps(true);
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(500000);// 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        mLocationClient.setLocOption(option);
        //未完待续。。。

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    void initView() {
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        temperature_range_Tv = (TextView) findViewById(R.id.temperature_range);

        city_name_Tv.setText("北京天气");
        cityTv.setText("北京");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        weekTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        temperatureTv.setText("N/A");
        temperature_range_Tv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
        pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
    }


    public void onClick(View view) {
        if (view.getId() == R.id.title_update_btn) {
            Toast.makeText(this, "正在进行刷新...", Toast.LENGTH_SHORT).show();
            //通过SharedPreferences读取城市id,如果没有定义缺省为北京的编号
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("myWeather", cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
                Toast.makeText(MainActivity.this, "网络OK!", Toast.LENGTH_LONG).show();
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
            }
        }

        if (view.getId() == R.id.title_city_manager) {

            Intent i = new Intent(this, SelectCity.class);
            startActivityForResult(i, 1);
        }

    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        if(requestCode==1 && resultCode==RESULT_OK)
        {
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("myWeather","选择的城市代码为"+newCityCode);

            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE)
            {
                Log.d("myWeather","网络OK");
                if(newCityCode!=null)
                {

                    queryWeatherCode(newCityCode);
                }
            }
            else{
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));//从字节(8bit)到字符(支持16bits)，转到缓冲区
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);

                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null)
                        Log.d("myWeather", todayWeather.toString());

                    Message msg = new Message();
                    msg.what = UPDATE_TODAY_WEATHER;
                    msg.obj = todayWeather;
                    mHandler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null)
                        con.disconnect();
                }
            }
        }).start();
    }

    private TodayWeather parseXML(String xmldata) {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != xmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    void updateTodayWeather(TodayWeather todayWeather) {
        initView();
        if(todayWeather.getCity()==null)
        {
            Toast.makeText(MainActivity.this, "未获取该城市天气", Toast.LENGTH_SHORT).show();
        }
        if(todayWeather.getCity()!=null) {
            city_name_Tv.setText(todayWeather.getCity() + "天气");
            cityTv.setText(todayWeather.getCity());
            timeTv.setText(todayWeather.getUpdatetime() + "发布");
            if (todayWeather.getShidu() != null) {
                humidityTv.setText("湿度：" + todayWeather.getShidu());
            }
            if (todayWeather.getWendu() != null) {
                temperatureTv.setText("温度：" + todayWeather.getWendu());
            }
            weekTv.setText(todayWeather.getDate());
            temperature_range_Tv.setText(todayWeather.getHigh().substring(todayWeather.getHigh().lastIndexOf(' ')) + "~" + todayWeather.getLow().substring(todayWeather.getHigh().lastIndexOf(' ')));
            climateTv.setText(todayWeather.getType());
            if (todayWeather.getPm25() != null) {
                pmDataTv.setText(todayWeather.getPm25());
                pmQualityTv.setText(todayWeather.getQuality());
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
                int pmdata = Integer.parseInt(todayWeather.getPm25());
                if (pmdata >= 0 && pmdata <= 50) {
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
                }
                if (pmdata >= 51 && pmdata <= 100) {
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
                }
                if (pmdata >= 101 && pmdata <= 150) {
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
                }
                if (pmdata >= 151 && pmdata <= 200) {
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
                }
                if (pmdata >= 201 && pmdata <= 300) {
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
                }
                if (pmdata > 300) {
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
                }
                switch (todayWeather.getType()) {
                    case "晴":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                        break;
                    case "阴":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                        break;
                    case "暴雪":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                        break;
                    case "暴雨":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                        break;
                    case "大暴雨":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                        break;
                    case "大雪":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                        break;
                    case "大雨":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                        break;
                    case "多云":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                        break;
                    case "雷阵雨":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                        break;
                    case "雷阵雨冰雹":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                        break;
                    case "沙尘暴":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                        break;
                    case "特大暴雨":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                        break;
                    case "雾":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                        break;
                    case "小雪":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                        break;
                    case "小雨":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                        break;
                    case "雨夹雪":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                        break;
                    case "阵雪":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                        break;
                    case "阵雨":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                        break;
                    case "中雪":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                        break;
                    case "中雨":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                        break;
                    default:
                        break;
                }
            }
            if (todayWeather.getFengli() != null) {
                windTv.setText("风力：" + todayWeather.getFengli());
            }
            Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
        }
    }



}
