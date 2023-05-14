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

import com.example.finalproject.database.AppDataBase;
import com.example.finalproject.database.UserDao;
import com.example.finalproject.dataobjects.User;
import com.example.finalproject.validators.UserValidator;
import com.example.finalproject.validators.ValidateResponse;

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
        Button signupButton = view.findViewById(R.id.backToLoginButtom);

        AppDataBase dataBase = AppDataBase.getDatabase(getActivity());
        this.userDao = dataBase.userDao();

        signupButton.setOnClickListener(new View.OnClickListener() {
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
        EditText email = view.findViewById(R.id.signup_email);
        EditText phone = view.findViewById(R.id.signup_phonenumber);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strPassword = String.valueOf(password.getText());

                String strEmail = String.valueOf(email.getText());
                User user = new User();

                ValidateResponse validateUserNameResponse = validateUserName(user, userName);
                ValidateResponse validatePasswordResponse = validatePassword(user, password);
                ValidateResponse validateEmailResponse =  validateEmail(user, email);
                ValidateResponse validatePhoneResponse = validatePhone(user, phone);

                if(!validateUserNameResponse.isValidate() ||
                        !validatePasswordResponse.isValidate() ||
                        !validateEmailResponse.isValidate() ||
                        !validatePhoneResponse.isValidate())
                {
                    return;
                }
                else {
                    try {
                        userDao.insertUser(user);
                        Toast toast = Toast.makeText(getActivity(), "משתמש תקין", Toast.LENGTH_SHORT);
                        toast.show();
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
            userName.setText("");
            Toast toast = Toast.makeText(getActivity(), validateResponse.getMsg(), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            user.setUserName(strUserName);
        }

        return validateResponse;
    }

    private ValidateResponse validatePassword(User user, EditText password) {

        String strPassword = String.valueOf(password.getText());

        ValidateResponse validateResponse = UserValidator.validatePassword(strPassword);

        if (!validateResponse.isValidate()) {
            password.setText("");
            Toast toast = Toast.makeText(getActivity(), validateResponse.getMsg(), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            user.setPassword(strPassword);
        }

        return validateResponse;
    }

    private ValidateResponse validateEmail(User user, EditText email) {

        String strEmail = String.valueOf(email.getText());

        ValidateResponse validateResponse = UserValidator.validateEmail(strEmail);

        if (!validateResponse.isValidate()) {
            email.setText("");
            Toast toast = Toast.makeText(getActivity(), validateResponse.getMsg(), Toast.LENGTH_SHORT);
            toast.show();

        } else {
            long userExist =  userDao.isEmailExist(email.getText().toString());

            if(userExist>0)
            {
                validateResponse.setValidate(false);
                validateResponse.setMsg("משתמש זה קיים במערכת");

                Toast toast = Toast.makeText(getActivity(), validateResponse.getMsg(), Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                user.setEmail(strEmail);
            }
        }

        return validateResponse;
    }

    private ValidateResponse validatePhone(User user, EditText phone) {
        String strPhone = String.valueOf(phone.getText());

        ValidateResponse validateResponse = UserValidator.validatePhoneNumber(strPhone);

        if (!validateResponse.isValidate()) {
            phone.setText("");
            Toast toast = Toast.makeText(getActivity(), validateResponse.getMsg(), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            user.setPhoneNumber(strPhone);
        }

        return validateResponse;
    }
}

