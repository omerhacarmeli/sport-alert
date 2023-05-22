package com.spot.alert.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.spot.alert.dataobjects.Location;

import java.util.Collections;
import java.util.List;

import com.spot.alert.R;

public class LocationAdapter
        extends RecyclerView.Adapter<LocationViewHolder> {

    List<Location> list = Collections.emptyList();

    Context context;
    ClickListener clickListener;

    public LocationAdapter(Context context, ClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    @Override
    public LocationViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context
                = parent.getContext();
        LayoutInflater inflater
                = LayoutInflater.from(context);

        View photoView = inflater.inflate(R.layout.location_item, parent, false);

        LocationViewHolder viewHolder = new LocationViewHolder(photoView);

        return viewHolder;
    }

    @Override
    public void
    onBindViewHolder(final LocationViewHolder viewHolder,
                     final int position) {
        final int index = viewHolder.getAdapterPosition();
        viewHolder.examName
                .setText(list.get(position).name);

        viewHolder.examDate
                .setText("(" + list.get(position).latitude + ", " + list.get(position).longitude + ")");
        viewHolder.examMessage
                .setText(String.valueOf(list.get(position).level));
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.click(index);
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