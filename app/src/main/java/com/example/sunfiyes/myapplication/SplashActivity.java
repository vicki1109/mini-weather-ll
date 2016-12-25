package com.example.sunfiyes.myapplication;

//import com.umeng.update.UmengUpdateAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {
	
	private final int SPLASH_DISPLAY_LENGHT = 3000;
	boolean isFirstIn = false;
    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    private static final String SHAREDPREFERENCES_NAME = "first_pref"; 
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_splash);  
    //UmengUpdateAgent.update(this);
        init(); 
  
    }
    
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case GO_HOME:
                goHome();
                break;
            case GO_GUIDE:
                goGuide();
                break;
            }
            super.handleMessage(msg);
        }
    };
    
    private void init() {

        SharedPreferences preferences = getSharedPreferences(
                SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        isFirstIn = preferences.getBoolean("isFirstIn", true);


        if (!isFirstIn) {

            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DISPLAY_LENGHT);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DISPLAY_LENGHT);
        }

    }
    
    private void goGuide() {
        Intent intent = new Intent(SplashActivity.this, Guide.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }
    private void goHome() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

}

