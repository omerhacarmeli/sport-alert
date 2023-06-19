package com.spot.alert.adapter.user;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spot.alert.R;

public class UserViewHolder
        extends RecyclerView.ViewHolder {
    TextView userName;
    ImageButton deleteItem;
    ImageButton editItem;
    View view;

    UserViewHolder(View itemView) {
        super(itemView);
        userName = (TextView) itemView.findViewById(R.id.userName);
        deleteItem = (ImageButton) itemView.findViewById(R.id.deleteItemButton);
        editItem = (ImageButton) itemView.findViewById(R.id.editItemButton);
        view = itemView;
    }
}

