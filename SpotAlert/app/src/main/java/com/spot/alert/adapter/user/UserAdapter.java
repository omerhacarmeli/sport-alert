package com.spot.alert.adapter.user;

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
import com.spot.alert.adapter.location.LocationViewHolder;
import com.spot.alert.dataobjects.User;

import java.util.Collections;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

        List<User> list = Collections.emptyList();
        Context context;
        ClickListener deleteListener;
        ClickListener editListener;
        ClickListener clickListener;

    public UserAdapter(Context context, ClickListener deleteListener, ClickListener editListener) {
            this.context = context;
            this.deleteListener = deleteListener;
            this.editListener = editListener;
            //this.clickListener = clickListener;
        }

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View userView = inflater.inflate(R.layout.user_item, parent, false);

            UserViewHolder viewHolder = new UserViewHolder(userView);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final UserViewHolder viewHolder, final int position) {
            final int index = viewHolder.getAdapterPosition();

            viewHolder.userName
                    .setText(list.get(position).userName);

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
                        deleteListener.click(list.get(position));

                    }, anim.getDuration());
                }
            });

            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* clickListener.click(list.get(position));*/
                }
            });

            viewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    User user = list.get(position);

                    if (SpotAlertAppContext.CENTER_POINT_STRING.equals(user.getUserName()))
                    {
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

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public void setDataChanged(List<User> userList) {

            this.list = userList;

            this.notifyDataSetChanged();
        }
    }
