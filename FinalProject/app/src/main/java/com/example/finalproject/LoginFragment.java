package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.finalproject.database.AppDataBase;
import com.example.finalproject.database.UserDao;
import com.example.finalproject.dataobjects.User;

public class LoginFragment extends Fragment {

    UserDao userDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.login_fragment, container, false);
        return inflate;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button signupButton = view.findViewById(R.id.buttonSignup);

        AppDataBase dataBase = AppDataBase.getDatabase(getActivity());
        this.userDao = dataBase.userDao();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WelcomeActivity welcomeActivity = (WelcomeActivity) getActivity();
                welcomeActivity.changeFragmentToSingUp();
            }
        });

        TextView logIN = view.findViewById(R.id.logIN);

        Button loginButton = view.findViewById(R.id.buttonLogin);

        loginButton
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText userName = view.findViewById(R.id.login_email);
                        EditText password = view.findViewById(R.id.login_password);
                        String strUserName = String.valueOf(userName.getText());
                        String strPassword = String.valueOf(password.getText());
                        User user = userDao.login(strUserName, strPassword);

                        if (user != null) {
                            Toast toast = Toast.makeText(getActivity(), "שלום", Toast.LENGTH_SHORT);
                            toast.show();

                            Intent locationIntent  = new Intent(getActivity().getApplicationContext(), LocationActivity.class);
                            startActivity(locationIntent);
                        } else {
                            Toast toast = Toast.makeText(getActivity(), "שם המשתמש או הסיסמה אינם נכונים", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });

    }
}


