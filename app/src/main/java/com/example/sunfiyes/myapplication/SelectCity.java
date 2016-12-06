package com.example.sunfiyes.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sunfiyes.app.MyApp;
import com.example.sunfiyes.bean.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunfiyes on 2016/10/18 0018.
 */
public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;
    private ListView mlistView;
    private MyApp app;
    private List<City> data;
    String getCityNum;
    ArrayList<String> cityName = new ArrayList<String>();
    ArrayList<String> cityId = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);
        app = (MyApp)getApplication();
        data = app.getCityList();
        int i = 0;

        while(i<data.size())
        {
            cityName.add(data.get(i).getCity());
            cityId.add(data.get(i).getNumber());
            i++;
        }

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        mlistView = (ListView)findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,cityName);
        mlistView.setAdapter(adapter);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getCityNum = data.get(i).getNumber();
                Toast.makeText(SelectCity.this, "你单击了：" + data.get(i).getCity(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode", getCityNum);
                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }
}
