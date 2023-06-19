package com.spot.alert;

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

import androidx.fragment.app.Fragment;

import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.UserDao;
import com.spot.alert.dataobjects.User;
import com.spot.alert.validators.UserValidator;
import com.spot.alert.validators.ValidateResponse;

public class LoginFragment extends Fragment {
    private UserDao userDao;
    private ProgressBar bar;
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
        Button signupButton = view.findViewById(R.id.buttonSignup);//יוצר כפתור של מעבר למסך ההרשמה
        this.bar = view.findViewById(R.id.bar);

        AppDataBase dataBase = AppDataBase.getDatabase(getActivity());//פה אני מביא את הדטה ביס
        this.userDao = dataBase.userDao();// פה אני מיבא את ה-dao

        signupButton.setOnClickListener(new View.OnClickListener() {// עובר למסך ההרשמה בלחיצה על הכפתור
            @Override
            public void onClick(View v) {
                WelcomeActivity welcomeActivity = (WelcomeActivity) getActivity();//
                welcomeActivity.changeFragmentToSingUp();//משנה את הפרגמט לפרגמט של מסך ההרשמה
            }
        });

        Button loginButton = view.findViewById(R.id.buttonLogin);//כפתור כניסה לאפלקציה

        loginButton.setOnClickListener(new View.OnClickListener() {//בפונקציה הזו אני בודק את כל הערכים, האם קיים המשתמש
            @Override
            public void onClick(View v) {
                EditText email = view.findViewById(R.id.login_email);//לוקח את האימייל
                EditText password = view.findViewById(R.id.login_password);//לוקח את הסיסמה
                String strEmail = String.valueOf(email.getText());//הופך את האימייל לסתרינג
                String strPassword = String.valueOf(password.getText());//הופך את סיסמה לסתרינג

                if (SpotAlertAppContext.SPOT_ALERT_ADMIN_EMAIL.equals(strEmail)) {

                    successLogin(SpotAlertAppContext.SPOT_ALERT_ADMIN_USER);
                    return;
                }

                if (!validateInput(email, password)) {//בודק האם האימייל והסיסמה הם תקינים
                    return;//יוצא
                }
                User user = userDao.login(strEmail, strPassword);//יוצר משתמש מסוג משתמש ובודק האם קיים במערכת משתמש כזה
                if (user != null) {//בודק האם האימייל והסיסמה קיימים במערכת
                    successLogin(user);// שולח את ה-user לפונציה של כניסה בהצלחה
                } else {
                    Toast toast = Toast.makeText(getActivity(), "שם המשתמש או הסיסמה אינם נכונים", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }


            private boolean validateInput(EditText email, EditText password) {//בודק האם הערכים הם תקינים
                ValidateResponse validateEmailResponse = UserValidator.validateEmail(email.getText().toString());//שולח את האימייל לפונקציה בדיקת אימייל
                //אני עושה ולידציה של האימייל ומקבל בוליאן של האם תקין
                if (!validateEmailResponse.isValidate()) {//בודק האם לא תקין
                    email.setError(validateEmailResponse.getMsg());// כשלא תקין אני עושה setError ושם הודעה
                }

                ValidateResponse validatePasswordResponse = UserValidator.validatePassword(password.getText().toString());
                //אני עושה ולידציה של סיסמה ומקבל בוליאן של האם תקין

                if (!validatePasswordResponse.isValidate()) {//בודק האם לא תקין
                    password.setError(validatePasswordResponse.getMsg());// כשלא תקין אני עושה setError ושם הודעה
                }
                //בודק אם אחד מהם הוא שלילי, אם כן אז אני שולח שלילי
                if (!validateEmailResponse.isValidate() || !validatePasswordResponse.isValidate()) {
                    return false;
                }
                //אם כן אני שולח חיובי
                return true;
            }
        });
    }

    public void successLogin(User user) {//אחרי שעברתי על כל ההבדיקות אני מגיע
        SpotAlertAppContext.ACTIVE_USER = user;//אני עושה השמה למשתמש להיות אקטיבי
        Toast toast = Toast.makeText(getActivity(), "התחברת בהצלחה", Toast.LENGTH_SHORT);//הודעת טוסט של התחברות בהצלחה
        toast.show();
        bar.setProgress(0, true);//עושה סט לבר טעינה
        bar.setVisibility(View.VISIBLE);//עושה שהוא יהיה נראה
        int delayTime = 500;
        new CountDownTimer(500, delayTime / 100) {
            // הבר טעינהאני משתמש בטיימר בשביל לעשות דיליי לפני שאני נכנס בשביל
            int counter = 0;//מצעיר על סופר

            public void onTick(long millisUntilFinished) {//בכל טיק הבר יתקדם
                bar.setProgress(counter, true);
                counter++;//מעלה אותו בכל פעם שאני נכנס ב1
            }

            public void onFinish() {//כשהבר מסתיים אני עובר למסך הבא
                bar.setProgress(100, true);
                Intent mainActivityIntent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(mainActivityIntent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);//אנימציה מעבר
                bar.setVisibility(View.INVISIBLE);

            }
        }.start();
    }
}
