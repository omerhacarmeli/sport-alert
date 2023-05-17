package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    TextView appname;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView appNameTextView = findViewById(R.id.appName);
        appNameTextView.animate().translationY(-1600).setDuration(2700).setStartDelay(0);

        new CountDownTimer(3000, 3000){
            public void onTick(long millisUntilFinished){

            }
            public  void onFinish(){
                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                overridePendingTransition(R.drawable.fade_in, R.drawable.fade_out);
            }
        }.start();
    }
}
