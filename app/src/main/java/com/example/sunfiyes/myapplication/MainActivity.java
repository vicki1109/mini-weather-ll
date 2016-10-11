package com.example.sunfiyes.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunfiyes.bean.TodayWeather;
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

/**
 * Created by sunfiyes on 2016/9/27 0027.
 */
public class MainActivity extends Activity implements View.OnClickListener{
    private ImageView mUpdateaBtn;

    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,temperatureTv,temperature_range_Tv,climateTv,windTv,city_name_Tv;
    private ImageView weatherImg,pmImg;

    //通过消息机制，将解析的天气对象，通过消息发送给主线程，主线程接收到消息数据后，调用函数更新UI界面上的数据。
    private static final int UPDATE_TODAY_WEATHER = 1;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    void initView()
    {
        city_name_Tv = (TextView)findViewById(R.id.title_city_name);
        cityTv = (TextView)findViewById(R.id.city);
        timeTv = (TextView)findViewById(R.id.time);
        humidityTv = (TextView)findViewById(R.id.humidity);
        weekTv = (TextView)findViewById(R.id.week_today);
        pmDataTv = (TextView)findViewById(R.id.pm_data);
        pmQualityTv = (TextView)findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView)findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView)findViewById(R.id.temperature);
        climateTv = (TextView)findViewById(R.id.climate);
        windTv = (TextView)findViewById(R.id.wind);
        weatherImg = (ImageView)findViewById(R.id.weather_img);
        temperature_range_Tv = (TextView)findViewById(R.id.temperature_range);

        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        weekTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        temperatureTv.setText("N/A");
        temperature_range_Tv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateaBtn = (ImageView)findViewById(R.id.title_update_btn);
        mUpdateaBtn.setOnClickListener(this);

        if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE)
        {
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this,"网络OK！", Toast.LENGTH_LONG).show();
        }
        else
        {
            Log.d("myWeather","网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
        }

        initView();
    }

    public void onClick(View view)
    {
        if(view.getId()==R.id.title_update_btn)
        {
            //通过SharedPreferences读取城市id,如果没有定义缺省为北京的编号
            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            Log.d("myWeather", cityCode);

            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
                Toast.makeText(MainActivity.this,"网络OK!",Toast.LENGTH_LONG).show();
            }
            else
            {
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void queryWeatherCode(String cityCode)
    {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
        Log.d("myWeather",address);
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
                    while((str=reader.readLine())!=null)
                    {
                        response.append(str);
                        Log.d("myWeather",str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather",responseStr);

                    todayWeather = parseXML(responseStr);
                    if(todayWeather!=null)
                    Log.d("myWeather",todayWeather.toString());

                    Message msg = new Message();
                    msg.what = UPDATE_TODAY_WEATHER;
                    msg.obj = todayWeather;
                    mHandler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(con!=null)
                        con.disconnect();
                }
            }
        }).start();
    }

    private TodayWeather parseXML(String xmldata)
    {
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
            while(eventType!=xmlPullParser.END_DOCUMENT)
            {
                switch(eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp"))
                        {
                            todayWeather = new TodayWeather();
                        }
                        if(todayWeather!=null)
                        {
                            if(xmlPullParser.getName().equals("city"))
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("updatetime"))
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("shidu"))
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("wendu"))
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("pm25"))
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("quality"))
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("fengxiang")&&fengxiangCount==0)
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            }
                            else if(xmlPullParser.getName().equals("fengli")&&fengliCount==0)
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            }
                            else if(xmlPullParser.getName().equals("date")&&dateCount==0)
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            }
                            else if(xmlPullParser.getName().equals("high")&&highCount==0)
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                            }
                            else if(xmlPullParser.getName().equals("low")&&lowCount==0)
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                            }
                            else if(xmlPullParser.getName().equals("type")&&typeCount==0)
                            {
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
//    private void parseXML(String xmldata)
//    {
//        int fengxiangCount = 0;
//        int fengliCount = 0;
//        int dateCount = 0;
//        int highCount = 0;
//        int lowCount = 0;
//        int typeCount = 0;
//        try{
//            //构造工厂实例
//            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
//            //创建解析器
//            XmlPullParser xmlPullParser = fac.newPullParser();
//            //将xml文件以流的形式加入
//            xmlPullParser.setInput(new StringReader(xmldata));
//            int eventType = xmlPullParser.getEventType();
//            Log.d("myWeather","parseXML");
//            while(eventType!=xmlPullParser.END_DOCUMENT)
//            {
//                switch(eventType) {
//                    //判断是否为文档开始事件
//                    case XmlPullParser.START_DOCUMENT:
//                        break;
//                    //判断是否为标签元素开始事件
//                    case XmlPullParser.START_TAG:
//                        if (xmlPullParser.getName().equals("city")) {
//                            Log.d("myWeather","parseXML2");
//                            eventType = xmlPullParser.next();
//                            Log.d("myWeather", "city:     " + xmlPullParser.getText());
//                        } else if (xmlPullParser.getName().equals("updatetime")) {
//                            eventType = xmlPullParser.next();
//                            Log.d("myWeather", "updatetime:     " + xmlPullParser.getText());
//                        } else if (xmlPullParser.getName().equals("shidu")) {
//                            eventType = xmlPullParser.next();
//                            Log.d("myWeather", "shidu:      " + xmlPullParser.getText());
//                        } else if (xmlPullParser.getName().equals("wendu")) {
//                            eventType = xmlPullParser.next();
//                            Log.d("myWeather", "wendu:      " + xmlPullParser.getText());
//                        } else if (xmlPullParser.getName().equals("pm25")) {
//                            eventType = xmlPullParser.next();
//                            Log.d("myWeather", "pm25:       " + xmlPullParser.getText());
//                        } else if (xmlPullParser.getName().equals("quality")) {
//                            eventType = xmlPullParser.next();
//                            Log.d("myWeather", "quality:      " + xmlPullParser.getText());
//                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
//                            eventType = xmlPullParser.next();
//                            Log.d("myWeather", "fengxiang:      " + xmlPullParser.getText());
//                            fengxiangCount++;
//                        } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0)
//                        {
//                            eventType = xmlPullParser.next();
//                            Log.d("myWeather","fengli:      "+xmlPullParser.getText());
//                            fengliCount++;
//                        }
//                        else if(xmlPullParser.getName().equals("data")&&dateCount==0)
//                        {
//                            eventType = xmlPullParser.next();
//                            Log.d("myWeather","date:      "+xmlPullParser.getText());
//                            dateCount++;
//                        }
//                        else if(xmlPullParser.getName().equals("high")&&highCount==0)
//                        {
//                            eventType = xmlPullParser.next();
//                            Log.d("myWeather","high:      "+xmlPullParser.getText());
//                            highCount++;
//                        }
//                        else if(xmlPullParser.getName().equals("low")&&lowCount==0)
//                        {
//                            eventType = xmlPullParser.next();
//                            Log.d("myWeather","low:     "+xmlPullParser.getText());
//                            lowCount++;
//                        }
//                        else if(xmlPullParser.getName().equals("type")&&typeCount==0)
//                        {
//                            eventType = xmlPullParser.next();
//                            Log.d("myWeather","type:      "+xmlPullParser.getText());
//                            typeCount++;
//                        }
//                        break;
//                    //判断当前事件是否为标签元素结束事件
//                    case XmlPullParser.END_TAG:
//                        break;
//                }
//                //进入下一个元素并触发相应事件
//                eventType = xmlPullParser.next();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    void updateTodayWeather(TodayWeather todayWeather)
    {
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        temperatureTv.setText("温度："+todayWeather.getWendu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperature_range_Tv.setText(todayWeather.getHigh().substring(todayWeather.getHigh().lastIndexOf(' '))+"~"+todayWeather.getLow().substring(todayWeather.getHigh().lastIndexOf(' ')));
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力："+todayWeather.getFengli());
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }
}
