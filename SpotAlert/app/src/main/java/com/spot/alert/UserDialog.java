package com.spot.alert;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

public class UserDialog extends Dialog {
    Bitmap bitmap;

    public UserDialog(Context  context, Bitmap bitmap) {
        super(context,R.style.UserDialogStyle);

        this.bitmap = bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_dialog);

        ImageView imageView = findViewById(R.id.userImage);
        // Set your image resource to the ImageView
        imageView.setImageBitmap(bitmap);
    }
}