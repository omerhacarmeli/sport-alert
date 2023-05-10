package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView logIN = findViewById(R.id.logIN);
        Button bottunLogIn = findViewById(R.id.bottunLogIn);
        Button bottunSignin = findViewById(R.id.bottunSignin);
        FinalProjectDataBase dataBase = FinalProjectDataBase.getdatabase(this);
        UserDao userDao = dataBase.userDao();


        bottunLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userName = findViewById(R.id.userName);
                EditText password = findViewById(R.id.password);
                String strUserName = String.valueOf(userName.getText());
                String strParssword = String.valueOf(password.getText());
                User user = new User();
                if (userDao.isUserExist(strUserName, strParssword)) {
                    user.setUserName(strUserName);
                    user.setUserPassword(strParssword);
                    Toast toast = Toast.makeText(getApplicationContext(), "שלום", Toast.LENGTH_SHORT);
                    toast.show();

                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "הסיסמה או שם המשתמש אינם נכונים", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });


    }
}