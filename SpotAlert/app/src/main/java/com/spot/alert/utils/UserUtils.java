package com.spot.alert.utils;

import android.widget.EditText;

import com.spot.alert.database.UserDao;
import com.spot.alert.dataobjects.User;
import com.spot.alert.validators.UserValidator;
import com.spot.alert.validators.ValidateResponse;

public class  UserUtils {
    //בדיקת שם משתמש
    public static ValidateResponse validateUserName(User user, EditText userName) {//שולח את משתמש ואת השם משתמש
        String strUserName = String.valueOf(userName.getText());//הופף את השם משתמש לסתרינג
        ValidateResponse validateResponse = UserValidator.validateUserName(strUserName);//בודק האם השם משתמש תקין

        if (!validateResponse.isValidate()) {//אם לא תקין
            userName.setError(validateResponse.getMsg());//עושים טעות ומציגים הערה
        } else {//אם כן תקין
            user.setUserName(strUserName);//מכניסים את השם משתמש
        }

        return validateResponse;// מחזירים את הולידציה
    }
    //בדיקת סיסמה
    public static ValidateResponse validatePassword(User user, EditText password) {//שולח את משתמש ואת סיסמה

        String strPassword = String.valueOf(password.getText());//הופף את סיסמה לסתרינג

        ValidateResponse validateResponse = UserValidator.validatePassword(strPassword);//בודק האם הסיסמה תקין

        if (!validateResponse.isValidate()) {//אם לא תקין
            password.setError(validateResponse.getMsg());//עושים טעות ומציגים הערה
        } else {//אם כן תקין
            user.setPassword(strPassword);//מכניסים את סיסמה
        }
        return validateResponse;// מחזירים את הולידציה
    }

    //בדיקת סיסמה שניה
    public static ValidateResponse validateVerifyPassword(User user, EditText verifyPassword, EditText password) {//שולח את משתמש, את סיסמה ואתה הבדיקת סיסמה

        String strVerifyPassword = String.valueOf(verifyPassword.getText());//הופף את בדיקת הסיסמה לסתרינג
        String strPassword = String.valueOf(password.getText());//הופף את סיסמה לסתרינג

        ValidateResponse validateResponse = new ValidateResponse();
        if (!strVerifyPassword.equals(strPassword)) {// בודק האם הסיסמאות הן אינם תואמות
            validateResponse.setValidate(false);//עושה ולידציה שהיא פולס
            validateResponse.setMsg("הסיסמה אינה תואמת");//מכניס את ההודעת שגיאה
            verifyPassword.setError(validateResponse.getMsg());//מכניס את הגיאה

        } else {// אם כן תקין
            validateResponse.setValidate(true);// מכניס את הערכים
        }
        return validateResponse;//מחזיר את הולידציה
    }

    //בדיקת אימייל
    public static ValidateResponse validateEmail(UserDao userDao, User user, EditText email) {//שולח את משתמש ואת האימייל

        String strEmail = String.valueOf(email.getText());//הופף את האימייל לסתרינג
        ValidateResponse validateResponse = UserValidator.validateEmail(strEmail);//בודק האם האימייל תקין
        if (!validateResponse.isValidate()) {//אם לא תקין
            email.setError(validateResponse.getMsg());//מציגים טעות והערה
        } else {//אם תקין
            long userExist = userDao.isEmailExist(email.getText().toString());//בודק אם האימייל כבר קיים במערכת

            if (userExist > 0) {//אם זה גדול מ-0 אז זה אומר שיש כבר אחד כזה מערכת
                validateResponse.setValidate(false);//מכניס שלילי
                validateResponse.setMsg("משתמש זה קיים במערכת");//מכניס הודעה שגיאה של קיים
                email.setError(validateResponse.getMsg());
            } else {//אם לא קיים ותקין
                user.setEmail(strEmail);//מכניס את האימייל
            }
        }

        return validateResponse;//שולח את הולידציה
    }

    //בדיקת טלפון
    public static ValidateResponse validatePhone(User user, EditText phone) {//שולח את משתמש ואת הטלפון
        String strPhone = String.valueOf(phone.getText());//הופף את הטלפון לסתרינג

        ValidateResponse validateResponse = UserValidator.validatePhoneNumber(strPhone);//בודק האם הטלפון תקין

        if (!validateResponse.isValidate()) {//בודק האם תקין
            phone.setError(validateResponse.getMsg());//עושים טעות ומציגים הערה
        } else {//אם כן תקין
            user.setPhoneNumber(strPhone);//מכניסים את הטלפון
        }

        return validateResponse;//שולחים את הולידציה
    }
}
