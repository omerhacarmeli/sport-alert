package com.spot.alert.utils;

import android.widget.EditText;

import com.spot.alert.database.UserDao;
import com.spot.alert.dataobjects.User;
import com.spot.alert.validators.UserValidator;
import com.spot.alert.validators.ValidateResponse;

public class UserUtils {
    public static ValidateResponse validateUserName(User user, EditText userName) {

        String strUserName = String.valueOf(userName.getText());

        ValidateResponse validateResponse = UserValidator.validateUserName(strUserName);

        if (!validateResponse.isValidate()) {
            userName.setError(validateResponse.getMsg());
        } else {
            user.setUserName(strUserName);
        }

        return validateResponse;
    }

    public static ValidateResponse validatePassword(User user, EditText password) {

        String strPassword = String.valueOf(password.getText());

        ValidateResponse validateResponse = UserValidator.validatePassword(strPassword);

        if (!validateResponse.isValidate()) {
            password.setError(validateResponse.getMsg());
        } else {
            user.setPassword(strPassword);
        }

        return validateResponse;
    }

    public static ValidateResponse validateVerifyPassword(User user, EditText verifyPassword, EditText password) {

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

    public static ValidateResponse validateEmail(UserDao userDao, User user, EditText email) {

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

    public static ValidateResponse validatePhone(User user, EditText phone) {
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
