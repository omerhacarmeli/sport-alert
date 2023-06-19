package com.spot.alert.adapter.location;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.spot.alert.R;
import com.spot.alert.SpotAlertAppContext;
import com.spot.alert.adapter.ClickListener;
import com.spot.alert.dataobjects.Location;

import java.util.Collections;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationViewHolder> {
    List<Location> list = Collections.emptyList();
    Context context;
    ClickListener deleteListener;
    ClickListener editListener;
    ClickListener clickListener;
    ClickListener testLocationListener;

    public LocationAdapter(Context context, ClickListener deleteListener, ClickListener editListener, ClickListener clickListener, ClickListener testLocationListener) {
        this.context = context;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
        this.clickListener = clickListener;
        this.testLocationListener = testLocationListener;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View locationView = inflater.inflate(R.layout.location_item, parent, false);

        LocationViewHolder viewHolder = new LocationViewHolder(locationView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final LocationViewHolder viewHolder, final int position) {
        final int index = viewHolder.getAdapterPosition();

        viewHolder.locationName.setText(list.get(position).name);

        viewHolder.editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editListener.click(list.get(position));
            }
        });
        viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation anim = AnimationUtils.loadAnimation(context.getApplicationContext(),
                        android.R.anim.slide_out_right);
                anim.setDuration(300);
                viewHolder.view.startAnimation(anim);

                new Handler().postDelayed(() -> {



                }, anim.getDuration());
            }
        });

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.click(list.get(position));
            }
        });

        viewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Location location = list.get(position);

                if (SpotAlertAppContext.CENTER_POINT_STRING.equals(location.getName())) {
                    return true;
                }

                viewHolder.editItem.setVisibility(View.VISIBLE);
                viewHolder.deleteItem.setVisibility(View.VISIBLE);

                new Handler().postDelayed(() -> {
                            viewHolder.editItem.setVisibility(View.INVISIBLE);
                            viewHolder.deleteItem.setVisibility(View.INVISIBLE);
                        }
                        , 4000);
                return true;
            }
        });

        viewHolder.testLocationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                testLocationListener.click(list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setDataChanged(List<Location> locations) {

        this.list = locations;

        this.notifyDataSetChanged();
    }
}