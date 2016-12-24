package com.example.sunfiyes.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
    private EditText mEditText;
    private TextView mTitleName;
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
        mTitleName = (TextView)findViewById(R.id.title_name);
        set_eSearch_TextChanged();
        //直接选择城市
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,cityName);
        mlistView.setAdapter(adapter);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getCityNum = data.get(i).getNumber();
                Toast.makeText(SelectCity.this, "你选择了：" + data.get(i).getCity(), Toast.LENGTH_SHORT).show();
                mTitleName.setText("当前城市：" + data.get(i).getCity());
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

    public void set_eSearch_TextChanged()
    {

        mEditText = (EditText) findViewById(R.id.search_edit);
        mEditText.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int editStart,editEnd;
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // 在文本框改变时，实时匹配
                Log.d("SelectCity","on editText changed:"+arg0);
                final List<City> newCityList = new ArrayList<City>();
                List<String> newNameList = new ArrayList<String>();
                String input_info = mEditText.getText().toString();
                if(mEditText.getText()!=null) {
                    for (int i = 0; i < data.size(); i++) {
                        String city = data.get(i).getCity();
                        String allpyString = data.get(i).getAllPY();
                        // 如果遍历到的名字包含所输入字符串
                        if (city.contains(input_info)
                                || (allpyString.toLowerCase()).contains(input_info
                                .toLowerCase())) {
                            // 将遍历到的元素重新组成一个list
                            newCityList.add(data.get(i));
                            newNameList.add(data.get(i).getCity());
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this, android.R.layout.simple_list_item_1, newNameList);
                    mlistView.setAdapter(adapter);
                }
                mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        getCityNum = newCityList.get(i).getNumber();
                        Toast.makeText(SelectCity.this, "你选择了：" + newCityList.get(i).getCity(), Toast.LENGTH_SHORT).show();
                        mTitleName.setText("当前城市：" + newCityList.get(i).getCity());
                    }
                });
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
                //这是文本框改变之前会执行的动作
                temp = arg0;
                Log.d("SelectCity", "before editText changed:" + temp);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                /**这是文本框改变之后 会执行的动作
                 * 因为我们要做的就是，在文本框改变的同时，我们的listview的数据也进行相应的变动，并且如一的显示在界面上。
                 * 所以这里我们就需要加上数据的修改的动作了。
                 */
                editStart = mEditText.getSelectionStart();
                editEnd = mEditText.getSelectionEnd();
                if(temp.length()>10)
                {
                    Toast.makeText(SelectCity.this,"你输入的字数已经超过了限制！",Toast.LENGTH_SHORT).show();
                    s.delete(editStart-1,editEnd);
                    int tempSelection = editStart;
                    mEditText.setText(s);
                    mEditText.setSelection(tempSelection);
                }
                Log.d("SelectCity","afterTextChanged");

            }
        });

    }
}
