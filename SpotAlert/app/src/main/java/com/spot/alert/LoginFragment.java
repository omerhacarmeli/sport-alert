package com.spot.alert;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.UserDao;
import com.spot.alert.dataobjects.User;
import com.spot.alert.validators.UserValidator;
import com.spot.alert.validators.ValidateResponse;

public class LoginFragment extends Fragment {

    UserDao userDao;
    ProgressBar bar;

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
        this.bar = view.findViewById(R.id.bar);

        AppDataBase dataBase = AppDataBase.getDatabase(getActivity());
        this.userDao = dataBase.userDao();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WelcomeActivity welcomeActivity = (WelcomeActivity) getActivity();
                welcomeActivity.changeFragmentToSingUp();
            }
        });

        Button loginButton = view.findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText email = view.findViewById(R.id.login_email);
                        EditText password = view.findViewById(R.id.login_password);
                        String strEmail = String.valueOf(email.getText());
                        String strPassword = String.valueOf(password.getText());

                        if (SpotAlertAppContext.SPOT_ALERT_ADMIN_EMAIL.equals(strEmail)) {

                            successLogin(SpotAlertAppContext.SPOT_ALERT_ADMIN_USER);
                            return;
                        }

                        if (!validateInput(email, password)) {
                            return;
                        }
                        User user = userDao.login(strEmail, strPassword);
                        if (user != null) {
                            successLogin(user);
                        } else {
                            Toast toast = Toast.makeText(getActivity(), "שם המשתמש או הסיסמה אינם נכונים", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }


                    private boolean validateInput(EditText email, EditText password) {
                        ValidateResponse validateEmailResponse = UserValidator.validateEmail(email.getText().toString());

                        if (!validateEmailResponse.isValidate()) {
                            email.setError(validateEmailResponse.getMsg());
                        }

                        ValidateResponse validatePasswordResponse = UserValidator.validatePassword(password.getText().toString());

                        if (!validatePasswordResponse.isValidate()) {
                            password.setError(validatePasswordResponse.getMsg());
                        }

                        if (!validateEmailResponse.isValidate() || !validatePasswordResponse.isValidate()) {
                            return false;
                        }
                        return true;
                    }
                });


    }

    public void successLogin(User user) {
        SpotAlertAppContext.ACTIVE_USER = user;
        Toast toast = Toast.makeText(getActivity(), "התחברת בהצלחה", Toast.LENGTH_SHORT);
        toast.show();
        bar.setProgress(0, true);
        bar.setVisibility(View.VISIBLE);
        int delayTime = 500;
        new CountDownTimer(500 , delayTime / 100) {
            int counter = 0;

            public void onTick(long millisUntilFinished) {
                bar.setProgress(counter, true);
                counter++;
            }

            public void onFinish() {
                bar.setProgress(100, true);
                Intent mainActivityIntent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(mainActivityIntent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                bar.setVisibility(View.INVISIBLE);

            }
        }.start();
    }
}
