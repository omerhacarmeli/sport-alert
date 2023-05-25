// ViewHolder code for RecyclerView
package com.spot.alert.adapter.timerange;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spot.alert.R;

public class TimeRangeViewHolder
        extends RecyclerView.ViewHolder {
    TextView fromTime;

    TextView toTime;

    ImageButton deleteItem;

    ImageButton editItem;
    View view;

    TimeRangeViewHolder(View itemView) {
        super(itemView);

        fromTime
                = (TextView) itemView
                .findViewById(R.id.fromTime);

        toTime
                = (TextView) itemView
                .findViewById(R.id.toTime);

        deleteItem
                = (ImageButton) itemView
                .findViewById(R.id.deleteItemTimeRangeButton);
        editItem
                = (ImageButton) itemView
                .findViewById(R.id.editItemTimeRangeButton);

        view = itemView;
    }
}
