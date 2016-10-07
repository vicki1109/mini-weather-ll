package com.example.sunfiyes.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
    }

    public void onClick(View view)
    {
        if(view.getId()==R.id.title_update_btn)
        {
            //通过SharedPreferences读取城市id,如果没有定义缺省为北京的编号
            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101011100");
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
                    parseXML(responseStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(con!=null)
                        con.disconnect();
                }
            }
        }).start();
    }

    private void parseXML(String xmldata)
    {
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try{
            //构造工厂实例
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            //创建解析器
            XmlPullParser xmlPullParser = fac.newPullParser();
            //将xml文件以流的形式加入
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather","parseXML");
            while(eventType!=xmlPullParser.END_DOCUMENT)
            {
                switch(eventType) {
                    //判断是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("city")) {
                            Log.d("myWeather","parseXML2");
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "city:     " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("updatetime")) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "updatetime:     " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("shidu")) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "shidu:      " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("wendu")) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "wendu:      " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("pm25")) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "pm25:       " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("quality")) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "quality:      " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "fengxiang:      " + xmlPullParser.getText());
                            fengxiangCount++;
                        } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0)
                        {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather","fengli:      "+xmlPullParser.getText());
                            fengliCount++;
                        }
                        else if(xmlPullParser.getName().equals("data")&&dateCount==0)
                        {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather","date:      "+xmlPullParser.getText());
                            dateCount++;
                        }
                        else if(xmlPullParser.getName().equals("high")&&highCount==0)
                        {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather","high:      "+xmlPullParser.getText());
                            highCount++;
                        }
                        else if(xmlPullParser.getName().equals("low")&&lowCount==0)
                        {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather","low:     "+xmlPullParser.getText());
                            lowCount++;
                        }
                        else if(xmlPullParser.getName().equals("type")&&typeCount==0)
                        {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather","type:      "+xmlPullParser.getText());
                            typeCount++;
                        }
                        break;
                    //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                //进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
