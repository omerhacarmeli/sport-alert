package com.spot.alert;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
//this screen is the first screen of the application, there is a lottie animetion and the name of app
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {//onCreate זה פונקציה ממשק של המערכת שמתחילה אובייקט
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);//הוא הולך לאקסמל והוא אומר שהוא מסוג אקטיביטי ספלאש
        TextView appNameTextView = findViewById(R.id.appName);//לוקח את הטקסט
        appNameTextView.animate().translationY(-1600).setDuration(700).setStartDelay(0);//הוא עושה אנימציה והוא אומר לו לזוז למעלה במסך המשך 700 מיליסקנס

        new CountDownTimer(1000, 1000){// הוא נכנס בספירה לטיק כמות הפעמים חלקי 1000
            public void onTick(long millisUntilFinished){

            }
            public  void onFinish(){
                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class)); // עובר לאולקם אקטיביטי
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);//אנימציה של מעבר
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
                                finishAffinity(); //פקודה זו סוגר את כל האפלקציה
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
