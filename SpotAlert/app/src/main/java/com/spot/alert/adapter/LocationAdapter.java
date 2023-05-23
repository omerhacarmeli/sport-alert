package com.spot.alert.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.spot.alert.dataobjects.Location;

import java.util.Collections;
import java.util.List;

import com.spot.alert.R;

public class LocationAdapter
        extends RecyclerView.Adapter<LocationViewHolder> {

    List<Location> list = Collections.emptyList();

    Context context;
    ClickListener deleteListener;
    ClickListener editListener;

    public LocationAdapter(Context context, ClickListener deleteListener,ClickListener editListener) {
        this.context = context;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
    }

    @Override
    public LocationViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context
                = parent.getContext();
        LayoutInflater inflater
                = LayoutInflater.from(context);

        View locationView = inflater.inflate(R.layout.location_item, parent, false);

        LocationViewHolder viewHolder = new LocationViewHolder(locationView);

        return viewHolder;
    }

    @Override
    public void
    onBindViewHolder(final LocationViewHolder viewHolder,
                     final int position) {
        final int index = viewHolder.getAdapterPosition();
        viewHolder.locationName
                .setText(list.get(position).name);

        viewHolder.activeLocation
                .setText("(" + list.get(position).latitude + ", " + list.get(position).longitude + ")");


        viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation anim = AnimationUtils.loadAnimation(context.getApplicationContext(),
                        android.R.anim.slide_out_right);
                anim.setDuration(300);
                viewHolder.view.startAnimation(anim);

                new Handler().postDelayed(()-> {
                        deleteListener.click(list.get(position));

                }, anim.getDuration());
            }
        });

        viewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                viewHolder.editItem.setVisibility(View.VISIBLE);
                viewHolder.deleteItem.setVisibility(View.VISIBLE);

                return true;
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
                        ,3000);
                return true;
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