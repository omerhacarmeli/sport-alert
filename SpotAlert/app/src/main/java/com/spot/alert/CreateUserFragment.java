package com.spot.alert;


import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spot.alert.adapter.ClickListener;
import com.spot.alert.adapter.timerange.ITimeRange;
import com.spot.alert.adapter.timerange.TimeRangeAdapter;
import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.ImageEntityDao;
import com.spot.alert.database.UserDao;
import com.spot.alert.database.UserTimeRangeDao;
import com.spot.alert.dataobjects.ImageEntity;
import com.spot.alert.dataobjects.User;
import com.spot.alert.dataobjects.UserTimeRange;
import com.spot.alert.utils.CameraOnClickListenerHandler;
import com.spot.alert.utils.UserUtils;
import com.spot.alert.validators.TimeRangeValidation;
import com.spot.alert.validators.ValidateResponse;

import java.util.ArrayList;
import java.util.List;

public class CreateUserFragment extends Fragment {
    private UserDao userDao;
    private UserTimeRangeDao userTimeRangeDao;
    private ImageEntityDao imageEntityDao;
    private User newUser;
    private ImageEntity imageEntity;
    private TimeRangeAdapter timeRangeAdapter;
    private RecyclerView recyclerView;
    private ClickListener deleteListener;
    private List<ITimeRange> userTimeRangeList = new ArrayList<>();
    private EditText userName;
    private EditText phone;
    private EditText email;

    private ImageView userImage;

    private CameraOnClickListenerHandler cameraOnClickListenerHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_user_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActivityResultLauncher<Intent> startCamera = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {

                        CameraOnClickListenerHandler.CameraImage cameraImage = cameraOnClickListenerHandler.onActivityResultGetCameraImage();
                        if (cameraImage != null) {
                            userImage.setImageBitmap(cameraImage.getBitmap());
                            imageEntity.setImageData(cameraImage.getImageData());
                        }
                    }
                }
        );

       this.cameraOnClickListenerHandler = new CameraOnClickListenerHandler(this.getActivity(), this, startCamera);

        this.userDao = AppDataBase.getDatabase(getActivity()).userDao();//bring the data base to userDao
        this.userTimeRangeDao = AppDataBase.getDatabase(getActivity()).userTimeRangeDao();//
        this.imageEntityDao = AppDataBase.getDatabase(getActivity()).imageEntityDao();

        userName = view.findViewById(R.id.signup_user);//taking the userName
        email = view.findViewById(R.id.signup_email);//taking the email
        phone = view.findViewById(R.id.signup_phonenumber);//taking the phone


        this.newUser = new User();//creating a new user of User object

        this.imageEntity = new ImageEntity();
        deleteListener = new ClickListener() {//after deleting the
            @Override
            public void click(Object obj) {//delete time
                if (obj instanceof ITimeRange) {

                    ITimeRange timeRange = (ITimeRange) obj;

                    userTimeRangeList.remove(timeRange);// removing the time range from the list

                    timeRangeAdapter.setDataChanged(userTimeRangeList);// setting the new list

                    Toast.makeText(getActivity(), "הגדרת שעה נמחקה בהצלחה", Toast.LENGTH_LONG).show();// toast message of
                }
            }
        };

        Button createUserApproval = view.findViewById(R.id.createUserApproval);// button save the data and create a new user
        Button createUserCancel = view.findViewById(R.id.createUserCancel);// button exit from CreateUserFragment and doesn't save any data
        createUserCancel.setOnClickListener(new View.OnClickListener() {//cancel new user
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity()).setMessage("האם אתה מעוניין לצאת ללא שמירת הנתונים?")//in here we ask the user he is sure
                        .setCancelable(true).setPositiveButton(//if yes
                                "כן",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ((MainActivity) getActivity()).moveUser();// the user creation is canceled and we go back to the previous screen
                                    }
                                })
                        .setNegativeButton(// if no
                                " לא",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();// the alert dialog is cancel
                                    }
                                })
                        .create().show();
            }
        });

        createUserApproval.setOnClickListener(new View.OnClickListener() {// button save the data and create a new user
            @Override
            public void onClick(View view) {//in here we check if all the inputs are validate
                ValidateResponse validateUserNameResponse = UserUtils.validateUserName(newUser, userName);
                ValidateResponse validateEmailResponse = UserUtils.validateEmail(userDao, newUser, email);
                ValidateResponse validatePhoneResponse = UserUtils.validatePhone(newUser, phone);
                ValidateResponse validateUserTimeRangeResponse = validateUserTimeRange();

                // we check if one of the inputs are not validate
                if (!validateUserNameResponse.isValidate() ||
                        !validateEmailResponse.isValidate() ||
                        !validatePhoneResponse.isValidate() ||
                        !validateUserTimeRangeResponse.isValidate()) {
                    Toast toast = Toast.makeText(getActivity(), "נתוני המשתמש אינם תקינים", Toast.LENGTH_SHORT);//a toast message: not validate
                    toast.show();
                    return;
                } else {// if they are all validate

                    if (imageEntity.getImageData() != null) { // check is there is a image in the data
                        Long imageId = imageEntityDao.insertImageEntity(imageEntity);// insert the image in to the data base and take the image id
                        newUser.setImageId(imageId);//connecting between the user and the image
                    }

                    long userId = userDao.insertUser(newUser);// insert the new user in to the data base and take the id

                    for (ITimeRange timeRange : userTimeRangeList) {// a loop of UserTimeRange list
                        UserTimeRange userTimeRange = (UserTimeRange) timeRange;//casting between the UserTimeRange and timeRange
                        userTimeRange.setUserId(userId);//connecting between the user and the timeRange
                        AppDataBase.databaseWriteExecutor.submit(() -> userTimeRangeDao.insertUserTimeRange(userTimeRange));// insert to the data base in async way
                    }

                    Toast toast = Toast.makeText(getActivity(), "המשתמש נשמר בהצלחה", Toast.LENGTH_SHORT);// toast text user has been save succesfuly
                    toast.show();
                    ((MainActivity) getActivity()).moveUser();
                }
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);// reference of recycleView
        timeRangeAdapter = new TimeRangeAdapter(getActivity(), deleteListener);
        recyclerView.setAdapter(timeRangeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton addTimeRangeFB = (FloatingActionButton) view.findViewById(
                R.id.addTimeRageFB);// fab of crating a new time range

        addTimeRangeFB.setOnClickListener(new View.OnClickListener() {// listener of creating a new time range
            @Override
            public void onClick(View v) {
                ITimeRange timeRange = new UserTimeRange();// object time range
                timeRange.setDayWeek(1);// setting the first day as day 1
                userTimeRangeList.add(timeRange);// adding the new time range to the list
                timeRangeAdapter.setDataChanged(userTimeRangeList);// setting the adapter
            }
        });

        userImage = view.findViewById(R.id.userImage);// reference of image
        userImage.setOnClickListener(cameraOnClickListenerHandler);
    }

    private ValidateResponse validateUserTimeRange() {// checking if the time range is validate
        ValidateResponse validateResponse = TimeRangeValidation.validateTimeRange(userTimeRangeList);

        if (!validateResponse.isValidate()) {
            Toast.makeText(getActivity(), validateResponse.getMsg(), Toast.LENGTH_LONG).show();
        }

        return validateResponse;
    }
}