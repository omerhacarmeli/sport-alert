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

public class SignUpFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.signup_fragment, container, false);
        return inflate;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button loginBttn = view.findViewById(R.id.backToLoginButtom);
        FinalProjectDataBase dataBase = FinalProjectDataBase.getdatabase(getActivity());
        UserDao userDao = dataBase.userDao();

        loginBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WelcomeActivity welcomeActivity = (WelcomeActivity) getActivity();
                welcomeActivity.changeFragmentToSingIn();
            }
        });
        Button signInButtom = view.findViewById(R.id.signIn);
        TextView title = view.findViewById(R.id.title);

        EditText email = view.findViewById(R.id.gettingTheEmail);
        EditText password = view.findViewById(R.id.gettingThePassword);
        EditText userName = view.findViewById(R.id.gettingTheUserName);

        signInButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strPassword = String.valueOf(password.getText());
                String strUserName = String.valueOf(userName.getText());
                String strEmail = String.valueOf(email.getText());
                User user = new User();
                boolean checkPass = checkingThePassword(strPassword);
                if (checkPass == false) {
                    password.setText("");
                } else {
                    user.setUserPassword(strPassword);
                }
                boolean checkUser = checkingTheUserName(strUserName);
                if (checkUser == false) {
                    userName.setText("");
                } else {
                    user.setUserName(strUserName);

                }
                user.setEmail(strEmail);

                try {
                    userDao.insertUser(user);
                    Toast toast = Toast.makeText(getActivity(), "משתמש תקין", Toast.LENGTH_SHORT);
                    toast.show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(getActivity(), "משתמש לא תקין, נסה שנית", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }

            private boolean checkingThePassword(String strPassword) {
                if (strPassword.length() < 2) {
                    Toast toast = Toast.makeText(getActivity(), "סיסמה קצרה מידיי", Toast.LENGTH_SHORT);
                    toast.show();
                    return false;
                }
                return true;
            }

            private boolean checkingTheUserName(String strUserName) {
                if (strUserName.length() < 2) {
                    Toast toast = Toast.makeText(getActivity(), "שם משתמש קצר מידיי", Toast.LENGTH_SHORT);
                    toast.show();
                    return false;
                }
                return true;
            }
        });

    }
}

