package com.spot.alert;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.FileProvider;
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
import com.spot.alert.utils.BitMapUtils;
import com.spot.alert.utils.UserUtils;
import com.spot.alert.validators.TimeRangeValidation;
import com.spot.alert.validators.ValidateResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateUserFragment extends Fragment implements OnRequestPermissionsResultCallback {
    private UserDao userDao;
    private UserTimeRangeDao userTimeRangeDao;
    private ImageEntityDao imageEntityDao;
    private User newUser;
    private ImageEntity imageEntity;
    private TimeRangeAdapter timeRangeAdapter;
    private RecyclerView recyclerView;
    private ClickListener deleteListener;
    private List<ITimeRange> userTimeRangeList = new ArrayList<>();
    private String imagePath;
    private EditText userName;
    private EditText phone;
    private EditText email;
    private static int CAMERA_REQUEST_CODE = 111111123;
    ImageView userImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_user_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        this.userDao = AppDataBase.getDatabase(getActivity()).userDao();
        this.userTimeRangeDao = AppDataBase.getDatabase(getActivity()).userTimeRangeDao();
        this.imageEntityDao = AppDataBase.getDatabase(getActivity()).imageEntityDao();

        userName = view.findViewById(R.id.signup_user);
        email = view.findViewById(R.id.signup_email);
        phone = view.findViewById(R.id.signup_phonenumber);


        this.newUser = new User();

        this.imageEntity = new ImageEntity();
        deleteListener = new ClickListener() {
            @Override
            public void click(Object obj) {
                if (obj instanceof ITimeRange) {

                    ITimeRange timeRange = (ITimeRange) obj;

                    userTimeRangeList.remove(timeRange);

                    timeRangeAdapter.setDataChanged(userTimeRangeList);

                    Toast.makeText(getActivity(), "הגדרת שעה נמחקה בהצלחה", Toast.LENGTH_LONG).show();
                }
            }
        };

        Button createUserApproval = view.findViewById(R.id.createUserApproval);
        Button createUserCancel = view.findViewById(R.id.createUserCancel);
        createUserCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity()).setMessage("האם אתה מעוניין לצאת ללא שמירת הנתונים?")
                        .setCancelable(true).setPositiveButton(
                                "כן",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ((MainActivity) getActivity()).moveUser();
                                    }
                                })
                        .setNegativeButton(
                                " לא",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .create().show();
            }
        });

        createUserApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateResponse validateUserNameResponse = UserUtils.validateUserName(newUser, userName);
                ValidateResponse validateEmailResponse = UserUtils.validateEmail(userDao, newUser, email);
                ValidateResponse validatePhoneResponse = UserUtils.validatePhone(newUser, phone);
                ValidateResponse validateUserTimeRangeResponse  = validateUserTimeRange();

                if (!validateUserNameResponse.isValidate() ||
                        !validateEmailResponse.isValidate() ||
                        !validatePhoneResponse.isValidate() ||
                        !validateUserTimeRangeResponse.isValidate()) {
                    Toast toast = Toast.makeText(getActivity(), "נתוני המשתמש אינם תקינים", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                } else {

                    if (imageEntity.getImageData() != null) {
                        Long imageId = imageEntityDao.insertImageEntity(imageEntity);
                        newUser.setImageId(imageId);
                    }

                    long userId = userDao.insertUser(newUser);

                    for (ITimeRange timeRange : userTimeRangeList) {
                        UserTimeRange userTimeRange = (UserTimeRange) timeRange;
                        userTimeRange.setUserId(userId);
                        AppDataBase.databaseWriteExecutor.submit(() -> userTimeRangeDao.insertUserTimeRange(userTimeRange));
                    }

                    Toast toast = Toast.makeText(getActivity(), "המשתמש נשמר בהצלחה", Toast.LENGTH_SHORT);
                    toast.show();
                    ((MainActivity) getActivity()).moveUser();
                }
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        timeRangeAdapter = new TimeRangeAdapter(getActivity(), deleteListener);
        recyclerView.setAdapter(timeRangeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton addTimeRangeFB = (FloatingActionButton) view.findViewById(
                R.id.addTimeRageFB);

        addTimeRangeFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ITimeRange timeRange = new UserTimeRange();
                timeRange.setDayWeek(1);
                userTimeRangeList.add(timeRange);
                timeRangeAdapter.setDataChanged(userTimeRangeList);
            }
        });

        userImage = view.findViewById(R.id.userImage);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create a file to save the image
                    File imageFile = createImageFile();
                    if (imageFile != null) {
                        Uri photoUri = FileProvider.getUriForFile(getActivity(), "com.spot.alert.fileprovider", imageFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    }
                }
            }

            private File createImageFile() {
                // Create an image file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File imageFile = null;
                try {
                    imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
                    imagePath = imageFile.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return imageFile;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE) {
            // Image captured successfully, you can now retrieve the image using the imagePath
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

            Bitmap scaledBitmap = BitMapUtils.scaleBitmap(bitmap);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            byte[] imageData = outputStream.toByteArray();

            userImage.setImageBitmap(scaledBitmap);
            imageEntity.setImageData(imageData);
        }
    }

    private ValidateResponse validateUserTimeRange() {
        ValidateResponse validateResponse = TimeRangeValidation.validateTimeRange(userTimeRangeList);

        if (!validateResponse.isValidate()) {
            Toast.makeText(getActivity(), validateResponse.getMsg(), Toast.LENGTH_LONG).show();
        }

        return validateResponse;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, requestCode);
            } else {
                // Permission denied, handle accordingly (e.g., display an error message)
            }
        }
    }
}