package com.example.acer.zebdashop;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class start_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                startService(new Intent(start_screen.this,LocationSetService.class));

                startActivity(new Intent(start_screen.this, MainActivity.class));
                finish();
            }
        }.start();
    }
}
