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

        FragmentManager fragmentManager = getSupportFragmentManager();

        LogInFragment logInFragment = new LogInFragment();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.myFragmentContainerView, logInFragment,
                "logInFragment");

        transaction.commit();
    }
}