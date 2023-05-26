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

                ValidateResponse validateUserNameResponse = validateUserName(user, userName);
                ValidateResponse validatePasswordResponse = validatePassword(user, password);
                ValidateResponse validateVerifyPasswordResponse = validateVerifyPassword(user, verifyPassword, password);
                ValidateResponse validateEmailResponse = validateEmail(user, email);
                ValidateResponse validatePhoneResponse = validatePhone(user, phone);

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

    private ValidateResponse validateUserName(User user, EditText userName) {

        String strUserName = String.valueOf(userName.getText());

        ValidateResponse validateResponse = UserValidator.validateUserName(strUserName);

        if (!validateResponse.isValidate()) {
            userName.setError(validateResponse.getMsg());
        } else {
            user.setUserName(strUserName);
        }

        return validateResponse;
    }

    private ValidateResponse validatePassword(User user, EditText password) {

        String strPassword = String.valueOf(password.getText());

        ValidateResponse validateResponse = UserValidator.validatePassword(strPassword);

        if (!validateResponse.isValidate()) {
            password.setError(validateResponse.getMsg());
        } else {
            user.setPassword(strPassword);
        }

        return validateResponse;
    }

    private ValidateResponse validateVerifyPassword(User user, EditText verifyPassword, EditText password) {

        String strVerifyPassword = String.valueOf(verifyPassword.getText());
        String strPassword = String.valueOf(password.getText());

        ValidateResponse validateResponse = new ValidateResponse();
        if (!strVerifyPassword.equals(strPassword)) {
            validateResponse.setValidate(false);
            validateResponse.setMsg("הסיסמה אינה תואמת");
            verifyPassword.setError(validateResponse.getMsg());

        } else {
            validateResponse.setValidate(true);
        }
        return validateResponse;
    }

    private ValidateResponse validateEmail(User user, EditText email) {

        String strEmail = String.valueOf(email.getText());

        ValidateResponse validateResponse = UserValidator.validateEmail(strEmail);

        if (!validateResponse.isValidate()) {
            email.setError(validateResponse.getMsg());
        } else {
            long userExist = userDao.isEmailExist(email.getText().toString());

            if (userExist > 0) {
                validateResponse.setValidate(false);
                validateResponse.setMsg("משתמש זה קיים במערכת");
                email.setError(validateResponse.getMsg());
            } else {
                user.setEmail(strEmail);
            }
        }

        return validateResponse;
    }

    private ValidateResponse validatePhone(User user, EditText phone) {
        String strPhone = String.valueOf(phone.getText());

        ValidateResponse validateResponse = UserValidator.validatePhoneNumber(strPhone);

        if (!validateResponse.isValidate()) {
            phone.setError(validateResponse.getMsg());
        } else {
            user.setPhoneNumber(strPhone);
        }

        return validateResponse;
    }
}

