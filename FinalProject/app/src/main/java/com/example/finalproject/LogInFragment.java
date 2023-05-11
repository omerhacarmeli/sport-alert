package com.example.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class LogInFragment extends Fragment {
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
        Button signupBttn = view.findViewById(R.id.bottunSignin);
        signupBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WelcomeActivity welcomeActivity = (WelcomeActivity) getActivity();
                welcomeActivity.changeFragmentToSingUp();
            }
        });
        TextView logIN = view.findViewById(R.id.logIN);
        Button bottunLogIn = view.findViewById(R.id.bottunLogIn);
        FinalProjectDataBase dataBase = FinalProjectDataBase.getdatabase(getActivity());
        UserDao userDao = dataBase.userDao();


        bottunLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userName = view.findViewById(R.id.userName);
                EditText password = view.findViewById(R.id.password);
                String strUserName = String.valueOf(userName.getText());
                String strPassword = String.valueOf(password.getText());
                User user = new User();
                if (userDao.isUserExist(strUserName, strPassword)) {
                    user.setUserName(strUserName);
                    user.setUserPassword(strPassword);
                    Toast toast = Toast.makeText(getActivity(), "שלום", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getActivity(), "הסיסמה או שם המשתמש אינם נכונים", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }
}
