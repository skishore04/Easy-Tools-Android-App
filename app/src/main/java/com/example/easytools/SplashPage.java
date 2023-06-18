package com.example.easytools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class SplashPage extends AppCompatActivity {

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        timer= new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashPage.this, Login.class);
                startActivity(intent);
            }
        },5000);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}