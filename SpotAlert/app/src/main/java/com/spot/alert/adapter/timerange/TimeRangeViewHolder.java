// ViewHolder code for RecyclerView
package com.spot.alert.adapter.timerange;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spot.alert.R;

public class TimeRangeViewHolder
        extends RecyclerView.ViewHolder {
    TextView fromTime;

    TextView toTime;

    TextView errorView;

    ImageButton deleteItem;

    ImageButton fromTimePickerImage;

    ImageButton toTimePickerImage;

    Spinner spinnerDays;
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

        fromTimePickerImage =  (ImageButton) itemView
                .findViewById(R.id.fromTimeImage);

        toTimePickerImage =  (ImageButton) itemView.findViewById(R.id.toTimeImage);

        spinnerDays= itemView.findViewById(R.id.spinner_days);

        errorView= itemView.findViewById(R.id.errorItem);

        view = itemView;
    }
}
