// ViewHolder code for RecyclerView
package com.spot.alert.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spot.alert.R;

public class LocationViewHolder
	extends RecyclerView.ViewHolder {
	TextView examName;
	TextView examMessage;
	TextView examDate;
	View view;

	LocationViewHolder(View itemView)
	{
		super(itemView);
		examName
			= (TextView)itemView
				.findViewById(R.id.examName);
		examDate
			= (TextView)itemView
				.findViewById(R.id.examDate);
		examMessage
			= (TextView)itemView
				.findViewById(R.id.examMessage);

		view = itemView;
	}
}
