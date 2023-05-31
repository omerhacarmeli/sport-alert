// ViewHolder code for RecyclerView
package com.spot.alert.adapter.location;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spot.alert.R;

public class LocationViewHolder
	extends RecyclerView.ViewHolder {
	TextView locationName;

	TextView activeLocation;

	ImageButton deleteItem;

	ImageButton editItem;

	View view;

	LocationViewHolder(View itemView)
	{
		super(itemView);
		locationName
			= (TextView)itemView
				.findViewById(R.id.locationName);

		deleteItem
				= (ImageButton)itemView
				.findViewById(R.id.deleteItemButton);
		editItem
			= (ImageButton)itemView
				.findViewById(R.id.editItemButton);


		view = itemView;
	}
}
