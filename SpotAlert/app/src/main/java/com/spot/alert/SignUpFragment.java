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
    private UserDao userDao;
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
        Button backToLoginButtom = view.findViewById(R.id.backToLoginButtom);//מצעיר על הכפתור בשביל
        ProgressBar bar = view.findViewById(R.id.bar); //מצהיר על הבר טעינה

        AppDataBase dataBase = AppDataBase.getDatabase(getActivity());//מביא את הדטה ביס
        this.userDao = dataBase.userDao();

        backToLoginButtom.setOnClickListener(new View.OnClickListener() {//כפתור זה שולח אותי בחזרה למסך הכניסה
            @Override
            public void onClick(View v) {
                WelcomeActivity welcomeActivity = (WelcomeActivity) getActivity();
                welcomeActivity.changeFragmentToLogin();
            }
        });
        //פה אני לוקח את כל הקלטים מהמשתמש
        Button signInButton = view.findViewById(R.id.signIn);
        TextView title = view.findViewById(R.id.title);

        EditText userName = view.findViewById(R.id.signup_user);
        EditText password = view.findViewById(R.id.signup_password);
        EditText verifyPassword = view.findViewById(R.id.signup_passwordVerify);
        EditText email = view.findViewById(R.id.signup_email);
        EditText phone = view.findViewById(R.id.signup_phonenumber);
        signInButton.setOnClickListener(new View.OnClickListener() {//לאחר שלוחצים על כפתור הכניסה
            @Override
            public void onClick(View view) {
                User user = new User();//יוצר משתמש חדש
                //בכל אחד מהם אני בודק האם הקלטים הם תקינים
                ValidateResponse validateUserNameResponse = UserUtils.validateUserName(user, userName);
                ValidateResponse validatePasswordResponse = UserUtils.validatePassword(user, password);
                ValidateResponse validateVerifyPasswordResponse = UserUtils.validateVerifyPassword(user, verifyPassword, password);
                ValidateResponse validateEmailResponse = UserUtils.validateEmail(userDao,user, email);
                ValidateResponse validatePhoneResponse = UserUtils.validatePhone(user, phone);
                //כאן אני בודק האם יש אחד שהוא לא תקין
                if (!validateUserNameResponse.isValidate() ||
                        !validatePasswordResponse.isValidate() ||
                        !validateVerifyPasswordResponse.isValidate() ||
                        !validateEmailResponse.isValidate() ||
                        !validatePhoneResponse.isValidate()) {
                    return;//אם כן אני יוצר מהכניסה לאפליקציה
                } else {
                    try {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());//פה אני יוצר אלרט ואני שואל האם הוא בטוח לגבי יצירת המשתמש
                        builder1.setMessage("האם אתה מאשר את יצירת המשתמש?");//ההודעה
                        builder1.setCancelable(true);//אם הוא לוחץ ביטול

                        builder1.setPositiveButton(//במקרה של אם כן
                                "כן",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        long createdUserId = userDao.insertUser(user);//מכניסים את הפרטים יוצרים משתמש חדש
                                        SpotAlertAppContext.ACTIVE_USER = userDao.getUser(createdUserId);//לוקח את המספר הזהות ועושה אותו משתמש ראשי
                                        Toast toast = Toast.makeText(getActivity(), "משתמש נרשם בהצלחה", Toast.LENGTH_SHORT);
                                        toast.show();// הודעה של היחברות בהצלחה
                                        bar.setProgress(0, true);//מגדיר את הבר
                                        bar.setVisibility(View.VISIBLE);//עושה את הבר נראה
                                        int delayTime = 2000;//עושה זמן דיליי

                                        new CountDownTimer(delayTime, delayTime / 100) {//מגדיר את הסופר ואת כמות הפעמים שהוא יכנס אליה
                                            int counter = 0;//סופר את ההיתקדמות של הבר

                                            public void onTick(long millisUntilFinished) {// בכל טיק הבר היתקדם
                                                bar.setProgress(counter, true);//היתקדמות הבר
                                                counter++;//מגדיל באחד
                                            }

                                            public void onFinish() {//בסיום
                                                bar.setProgress(100, true);//משלים את הבר
                                                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);//אנימציית מעבר
                                                Intent mainActivityIntent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                                                startActivity(mainActivityIntent);//מעבר לאקטיביטי הראשי
                                            }
                                        }.start();
                                    }
                                });

                        builder1.setNegativeButton(//במקרה שהמשתמש לוחץ לא, האפליקציה לא עושה כלום
                                " לא",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();//ביטול
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();


                    } catch (Exception e) {//במקרה שהמשתמש בחריגה
                        e.printStackTrace();
                        Toast toast = Toast.makeText(getActivity(), "משתמש לא תקין, נסה שנית", Toast.LENGTH_SHORT);
                        toast.show();//הודעת חריגה
                    }
                }
            }
        });
    }

}

