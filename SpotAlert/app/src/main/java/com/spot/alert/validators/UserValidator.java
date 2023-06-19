package com.spot.alert.validators;

import java.util.regex.Pattern;
public class UserValidator {
    public static Pattern emailPattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}");//דפוס של בדיקת האם האימייל הוא תקין
    public static Pattern phonePattern = Pattern.compile("^(\\(\\d{3}\\)|\\d{3})-?\\d{3}-?\\d{4}$");//דפוס של בדיקת האם הטלפון הוא תקין
    public static Pattern usernamePattern = Pattern.compile("[A-Za-z\\u0590-\\u05fe]\\w{2,20}$");//דפוס של בדיקת האם השם משתמש  הוא תקין

    public static ValidateResponse validateUserName(String username) {//הפונקציה הזו בודקת האם השם משתמש הוא תקין

        String[] userNames = username.split(" ");

        for (String name : userNames) {
            if (!usernamePattern.matcher(name).matches()) {//כאן בודקים האם השם משמתמש שהכניס לא תואם לדפוס
                // אם לא תואם מכניסים את ההודעת השגיאה ושלילי
                return new ValidateResponse(false, "שם משתמש אינו תקין");
            }
        }


        return new ValidateResponse(true, "שם משתמש תקין");//אם כן תואם מכניסים הודעה של תקין וחיובי

    }

    public static ValidateResponse validatePassword(String strPassword) {//הפונקציה הזו בודקת האם הסיסמה הוא תקינה

        if (strPassword.length() < 2) {//פה בודקים האם אורך הסיסמה הוא גדול משני תווים

            return new ValidateResponse(false, "סיסמה קצרה מידיי");// אם לא תואם מכניסים את ההודעת השגיאה ושלילי
        }

        return new ValidateResponse(true, "סיסמה תקינה");//אם כן תואם מכניסים הודעה של תקין וחיובי
    }

    public static ValidateResponse validateEmail(String email) {//הפונקציה הזו בודקת האם האימייל הוא תקין

        if (!emailPattern.matcher(email).matches()) {//כאן בודקים האם האימייל שהמתמש הכניס לא תואם לדפוס
            return new ValidateResponse(false, "אימייל אינו תקין");// אם לא תואם מכניסים את ההודעת השגיאה ושלילי
        }

        return new ValidateResponse(true, "אימייל תקין");//אם כן תואם מכניסים הודעה של תקין וחיובי
    }

    public static ValidateResponse validatePhoneNumber(String phoneNumber) {//הפונקציה הזו בודקת האם הטלפון הוא תקין
        if (!phonePattern.matcher(phoneNumber).matches()) {//כאן בודקים האם הטלפון שהמתמש הכניס לא תואם לדפוס

            return new ValidateResponse(false, "מספר טלפון אינו תקין");// אם לא תואם מכניסים את ההודעת השגיאה ושלילי
        }

        return new ValidateResponse(true, "מספר טלפון תקין");//אם כן תואם מכניסים הודעה של תקין וחיובי  
    }
}
