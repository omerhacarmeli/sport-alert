package com.spot.alert.adapter.timerange;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.spot.alert.R;
import com.spot.alert.adapter.ClickListener;
import com.spot.alert.adapter.location.LocationViewHolder;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.LocationTimeRange;

import java.util.Collections;
import java.util.List;

public class TimeRangeAdapter
        extends RecyclerView.Adapter<TimeRangeViewHolder> {

    List<LocationTimeRange> locationTimeRangeList = Collections.emptyList();

    Context context;
    ClickListener deleteListener;
    ClickListener editListener;

    ClickListener clickListener;

    public TimeRangeAdapter(Context context, ClickListener deleteListener, ClickListener editListener, ClickListener clickListener) {
        this.context = context;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
        this.clickListener = clickListener;
    }

    @Override
    public TimeRangeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context
                = parent.getContext();
        LayoutInflater inflater
                = LayoutInflater.from(context);

        View timeRangeView = inflater.inflate(R.layout.timerange_item, parent, false);

        TimeRangeViewHolder viewHolder = new TimeRangeViewHolder(timeRangeView);

        return viewHolder;
    }

    @Override
    public void  onBindViewHolder(final TimeRangeViewHolder viewHolder, final int position) {
        final int index = viewHolder.getAdapterPosition();

        viewHolder.fromTime
                .setText("10:00");

        viewHolder.toTime
                .setText("10:30");

        viewHolder.editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation anim = AnimationUtils.loadAnimation(context.getApplicationContext(),
                        android.R.anim.slide_out_right);
                anim.setDuration(300);
                viewHolder.view.startAnimation(anim);

                new Handler().postDelayed(()-> {
                        deleteListener.click(locationTimeRangeList.get(position));

                }, anim.getDuration());
            }
        });

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.click(locationTimeRangeList.get(position));
            }
        });

        viewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                viewHolder.editItem.setVisibility(View.VISIBLE);
                viewHolder.deleteItem.setVisibility(View.VISIBLE);

                new Handler().postDelayed(() -> {
                            viewHolder.editItem.setVisibility(View.INVISIBLE);
                            viewHolder.deleteItem.setVisibility(View.INVISIBLE);
                        }
                        ,4000);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return locationTimeRangeList.size();
    }

    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setDataChanged(List<LocationTimeRange> locationTimeRangeList) {

        this.locationTimeRangeList = locationTimeRangeList;

        this.notifyDataSetChanged();
    }
}