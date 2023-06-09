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

        View userView = inflater.inflate(R.layout.user_item, parent, false);//create view of user item

        UserViewHolder viewHolder = new UserViewHolder(userView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserViewHolder viewHolder, final int position) {
        User user = list.get(position);// לוקח את המשתמש
        viewHolder.userName.setText(user.userName);//מציג אותו בריסקל

        viewHolder.editItem.setOnClickListener(new View.OnClickListener() {//שולח אותו לעריכה
            @Override
            public void onClick(View view) {

                editListener.click(list.get(position));
            }
        });
        viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {//כשלוחצים על כפתור המחיקה
            @Override
            public void onClick(View view) {
                //מציג את האנימציית המחיקה
                Animation anim = AnimationUtils.loadAnimation(context.getApplicationContext(),
                        android.R.anim.slide_out_right);
                anim.setDuration(300);
                viewHolder.view.startAnimation(anim);

                new Handler().postDelayed(() -> {
                    deleteListener.click(list.get(position));//כאן שולחים לפונקציה את המשתמש במיקום בשביל למחוק אותו

                }, anim.getDuration());
            }
        });

        viewHolder.view.setOnLongClickListener(new View.OnLongClickListener() { // נכנסים לפה אחרי שלוחצים לחיצה ארוכה על הitem
            @Override
            public boolean onLongClick(View view) {//בעט לחיצה ארוכה

                User user = list.get(position);// לוחקים את המשתמש במיקום
                //עושים את המחיקה והעריכה נראים
                viewHolder.editItem.setVisibility(View.VISIBLE);
                viewHolder.deleteItem.setVisibility(View.VISIBLE);

                new Handler().postDelayed(() -> {//אחרי 4 שניות הם יהפכו שוב ללא נראים
                            viewHolder.editItem.setVisibility(View.INVISIBLE);
                            viewHolder.deleteItem.setVisibility(View.INVISIBLE);
                        }
                        , 4000);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {// הפונקציה של האדפטר ומקבלת את הרשימת האיברים ברשימה
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setDataChanged(List<User> userList) {

        this.list = userList;// מעדכן את הרשימה ברשימה מעודכנת

        this.notifyDataSetChanged();//פונקציה שמעדכנת את ריסקל
    }
}

