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
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.UserDao;
import com.spot.alert.dataobjects.User;
import com.spot.alert.utils.UserUtils;
import com.spot.alert.validators.UserValidator;
import com.spot.alert.validators.ValidateResponse;

public class SignUpFragment extends Fragment {
    UserDao userDao;

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
        Button backToLoginButtom = view.findViewById(R.id.backToLoginButtom);
        ProgressBar bar = view.findViewById(R.id.bar);

        AppDataBase dataBase = AppDataBase.getDatabase(getActivity());
        this.userDao = dataBase.userDao();

        backToLoginButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WelcomeActivity welcomeActivity = (WelcomeActivity) getActivity();
                welcomeActivity.changeFragmentToLogin();
            }
        });
        Button signInButton = view.findViewById(R.id.signIn);
        TextView title = view.findViewById(R.id.title);

        EditText userName = view.findViewById(R.id.signup_user);
        EditText password = view.findViewById(R.id.signup_password);
        EditText verifyPassword = view.findViewById(R.id.signup_passwordVerify);
        EditText email = view.findViewById(R.id.signup_email);
        EditText phone = view.findViewById(R.id.signup_phonenumber);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();

                ValidateResponse validateUserNameResponse = UserUtils.validateUserName(user, userName);
                ValidateResponse validatePasswordResponse = UserUtils.validatePassword(user, password);
                ValidateResponse validateVerifyPasswordResponse = UserUtils.validateVerifyPassword(user, verifyPassword, password);
                ValidateResponse validateEmailResponse = UserUtils.validateEmail(userDao,user, email);
                ValidateResponse validatePhoneResponse = UserUtils.validatePhone(user, phone);

                if (!validateUserNameResponse.isValidate() ||
                        !validatePasswordResponse.isValidate() ||
                        !validateVerifyPasswordResponse.isValidate() ||
                        !validateEmailResponse.isValidate() ||
                        !validatePhoneResponse.isValidate()) {
                    return;
                } else {
                    try {


                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        builder1.setMessage("האם אתה מאשר את יצירת המשתמש?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "כן",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        long createdUserId = userDao.insertUser(user);
                                        SpotAlertAppContext.ACTIVE_USER = userDao.getUser(createdUserId);
                                        Toast toast = Toast.makeText(getActivity(), "משתמש נרשם בהצלחה", Toast.LENGTH_SHORT);
                                        toast.show();
                                        bar.setProgress(0, true);
                                        bar.setVisibility(View.VISIBLE);
                                        int delayTime = 2000;

                                        new CountDownTimer(delayTime, delayTime / 100) {
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

                                            }
                                        }.start();
                                    }
                                });

                        builder1.setNegativeButton(
                                " לא",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast toast = Toast.makeText(getActivity(), "משתמש לא תקין, נסה שנית", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
    }

}

