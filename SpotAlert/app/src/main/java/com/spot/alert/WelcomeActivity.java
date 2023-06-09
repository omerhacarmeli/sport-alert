package com.spot.alert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.spot.alert.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        changeFragmentToLogin();//שולח לפונקציה הראשונה של ששולחת אותי למסך של כניסה למערכת
    }

    public void changeFragmentToLogin() {
        LoginFragment logInFragment = new LoginFragment();//יוצר אובייקט מסוג של loginFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();//מבצע עברה של פרגמת לפרגמת אחר
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);//עושה את האנימציה של פאיד במעבר למסך הבא
        transaction.replace(R.id.myFragmentContainerView, logInFragment,
                "logInFragment");// עובר למסך של כניסה

        transaction.commit();
    }

    public void changeFragmentToSingUp() {//פונקציה זו היא זו ששולחת אותי למסך של הרשמה

        SignUpFragment signUpFragment = new SignUpFragment();//יוצר אובייקט מסוג של signUpFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();//מבצע עברה של פרגמת לפרגמת אחר
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);//מבצעת את המעבר אם אנימציה

        transaction.replace(R.id.myFragmentContainerView, signUpFragment,
                "signUpFragment");//מבצעת את העברה למסך של הרשמה
        transaction.commit();
    }

    @Override
    public void onBackPressed() {// פונקציה זו לאחר שלוחצים על כפתור החזור שואל האם לצאת מהאפלקציה
        new AlertDialog.Builder(this).setMessage("האם אתה מעוניין לצאת מהאפליקציה?")//פה אני מכין את ההודעה למשתמש
                .setCancelable(true).setPositiveButton(//בודק האם התשובה היא כן
                        "כן",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finishAffinity();// פקודה זו סוגרת את האפלקציה
                            }
                        })
                .setNegativeButton(//בודק האם התשובה היא שלילית
                        " לא",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {//אם התשובה היא שלילית אני לא עושה כלום
                                dialog.cancel();//מבטל את ה-alertDialog
                            }
                        })
                .create().show();//פקודה זו מראה את הכל על המסך
    }
}