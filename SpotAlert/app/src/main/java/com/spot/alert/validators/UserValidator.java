package com.spot.alert.validators;

import java.util.regex.Pattern;

public class UserValidator {
    public static Pattern emailPattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}");
    public static Pattern phonePattern = Pattern.compile("^(\\(\\d{3}\\)|\\d{3})-?\\d{3}-?\\d{4}$");
    public static Pattern usernamePattern = Pattern.compile("[A-Za-z]\\w{2,20}$");

    public static ValidateResponse validateUserName(String username) {

        if (!usernamePattern.matcher(username).matches()) {

            return new ValidateResponse(false, "שם משתמש לא תקין");
        }

        return new ValidateResponse(true, "שם משתמש תקין");

    }

    public static ValidateResponse validatePassword(String strPassword) {

        if (strPassword.length() < 2) {

            return new ValidateResponse(false, "סיסמה קצרה מידיי");
        }

        return new ValidateResponse(true, "סיסמה תקינה");
    }

    public static ValidateResponse validateEmail(String email) {

        if (!emailPattern.matcher(email).matches()) {
            return new ValidateResponse(false, "אימייל לא תקין");
        }

        return new ValidateResponse(true, "אימייל תקין");
    }

    public static ValidateResponse validatePhoneNumber(String phoneNumber) {
        if (!phonePattern.matcher(phoneNumber).matches()) {

            return new ValidateResponse(false, "מספר טלפון לא תקין");
        }

        return new ValidateResponse(true, "מספר טלפון תקין");
    }
}
