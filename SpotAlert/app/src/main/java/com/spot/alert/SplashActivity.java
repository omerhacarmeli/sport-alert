package com.spot.alert;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
        appNameTextView.animate().translationY(-1600).setDuration(700).setStartDelay(0);

        new CountDownTimer(1000, 1000){
            public void onTick(long millisUntilFinished){

            }
            public  void onFinish(){
                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setMessage("האם אתה מעוניין לצאת מהאפליקציה S?")
                .setCancelable(true).setPositiveButton(
                        "כן",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finishAffinity();
                            }
                        })
                .setNegativeButton(
                        " לא",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create().show();
    }

}
