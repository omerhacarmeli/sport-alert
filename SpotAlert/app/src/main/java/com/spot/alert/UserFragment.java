package com.spot.alert;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spot.alert.adapter.ClickListener;
import com.spot.alert.adapter.user.UserAdapter;
import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.ImageEntityDao;
import com.spot.alert.database.UserDao;
import com.spot.alert.dataobjects.ImageEntity;
import com.spot.alert.dataobjects.User;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {
    private UserDao userDao;
    private UserAdapter adapter;
    private ImageEntityDao imageEntityDao;

    private RecyclerView recyclerView;
    private ClickListener deleteListener;
    private ClickListener editListener;
    private List<com.spot.alert.dataobjects.User> users;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.userDao = AppDataBase.getDatabase(getActivity()).userDao();
        this.imageEntityDao = AppDataBase.getDatabase(getActivity()).imageEntityDao();
        List<User> list = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        deleteListener = new ClickListener() {
            @Override
            public void click(Object obj) {
                if (obj instanceof User) {//לבדוק האם האובייקט שקיבלנו הוא מסוג משתמש
                    User user = (User) obj;//כאן אנחנו עושים כאסטינג

                    userDao.deleteUser(user);

                    if (user.getImageId() != null) {
                        ImageEntity imageEntity = new ImageEntity();
                        imageEntity.setId(user.getImageId());
                        imageEntityDao.deleteImageEntity(imageEntity);
                    }

                    Toast.makeText(getActivity(), user.getUserName() + " נמחק בהצלחה", Toast.LENGTH_LONG).show();
                }
            }
        };

        editListener = new ClickListener() {
            @Override
            public void click(Object obj) {
                if (obj instanceof com.spot.alert.dataobjects.User) {

                    com.spot.alert.dataobjects.User user = (com.spot.alert.dataobjects.User) obj;

                    Bundle bundle = new Bundle();
                    bundle.putLong("userId", user.getUserId());
                    getActivity().getIntent().putExtras(bundle);

                    ((MainActivity) getActivity()).moveEditUser(user);

                    Toast.makeText(getActivity(), "Edit User " + user.getUserName(), Toast.LENGTH_LONG).show();
                }
            }
        };

        FloatingActionButton addUserFB = (FloatingActionButton) view.findViewById(
                R.id.addUserFB);


        addUserFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).moveCreateUser();
            }
        });


        //כאן יוצרים את המשתמש הדאפטר, נותנים לו את שני הליסנרים של מחיקה ועריכת משתמש
        //התפקיד של הדאפטר לעדכן את הריסיקל ברשימת המשתמשים ולעדן את התצוגה
        adapter = new UserAdapter(getActivity(), deleteListener, editListener);
        recyclerView.setAdapter(adapter);//כאן עושים סט לאדפטר
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {// זה עובד בזמןש הוא גוללים

            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) { //מעלים את הפאב בגלילה למטה ומחזיר בגלילה למעלה

                if (dy > 0) {
                    addUserFB.hide();//מסתירים אותו בגלילה למטה
                } else {
                    addUserFB.show();//מציגים אותו בגלילה למטה
                }
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        loadLiveData();
    }

    private void loadLiveData() {//טעינת דטה
        this.userDao.getUsers().observe(getActivity(), (userList) -> {//מחזיר לייב דטה שיודע לטפל ברשימה של משתמשים
            //בכל פעם שיש עידכון על המשתמשים הלייב דטה יביא לי רשימה חדשה
            users = userList;
            adapter.setDataChanged(users);//מעדכן את הדאפטר
        });
    }
}

