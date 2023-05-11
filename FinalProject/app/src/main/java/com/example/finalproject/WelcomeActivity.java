package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        changeFragmentToSingIn();
    }

    public void changeFragmentToSingIn() {
        LogInFragment logInFragment = new LogInFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.myFragmentContainerView, logInFragment,
                "logInFragment");

        transaction.commit();
    }

    public void changeFragmentToSingUp() {

        SignUpFragment signUpFragment = new SignUpFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        transaction.replace(R.id.myFragmentContainerView, signUpFragment,
                "signUpFragment");
        transaction.commit();
    }
}