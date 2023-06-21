package com.spot.alert.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraOnClickListenerHandler implements View.OnClickListener {

    public static int CAMERA_REQUEST_CODE = 111111123;

    private ActivityResultLauncher<Intent> startCamera;

    private Context context;

    private Fragment fragment;

    private File imageFile;


    public CameraOnClickListenerHandler(Context context, Fragment fragment, ActivityResultLauncher<Intent> startCamera) {
        this.context = context;
        this.fragment = fragment;
        this.startCamera = startCamera;
    }

    @Override
    public void onClick(View v) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(context)
                    .setMessage("אין הרשאות לשימוש במצלמה")
                    .setPositiveButton(
                            "סגור",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();
                                }
                            })

                    .create()
                    .show();

            return;
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(this.context.getPackageManager()) != null) {
            // Create a file to save the image
            imageFile = createImageFile();

            if (imageFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this.context, "com.spot.alert.fileprovider", imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                startCamera.launch(cameraIntent);
            }
        }
    }

    private File 0createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageFile;
    }

    public CameraImage onActivityResultGetCameraImage() {

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());

            Bitmap scaledBitmap = BitMapUtils.scaleBitmap(bitmap);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            byte[] imageData = outputStream.toByteArray();

            return new CameraImage(imageData, scaledBitmap);
        } catch (Exception e) {
            return null;
        }
    }

    public class CameraImage {
        byte[] imageData;

        Bitmap bitmap;

        public CameraImage(byte[] imageData, Bitmap bitmap) {
            this.imageData = imageData;
            this.bitmap = bitmap;
        }

        public byte[] getImageData() {
            return imageData;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
    }
}
