package com.spot.alert.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.User;

import java.util.List;
//data access object
@Dao

public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(User user);

    @Delete
    void deleteUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM User WHERE userId =:userId")
    LiveData<User> getUserInfo(int userId);

    @Query("SELECT * FROM User WHERE email = :email AND password = :password")// צחפש אימייל שיש לו אימייל וסיסמה מהקלט שקיבלנו
    public User login(String email, String password);

    @Query("SELECT COUNT(*) FROM User WHERE email = :email")//פה אני מביא את מספר המופעים של האיימיל
    public long isEmailExist(String email);

    @Query("SELECT * FROM User WHERE userId= :userId ")
    User getUser(long userId);

    @Query("SELECT * FROM User")
    LiveData<List<User>> getUsers();//מחזיר לייב דטה שיודע לטפל ברשימה של משתמשים
    //בכל פעם שיש עידכון על המשתמשים הלייב דטה יביא לי רשימה חדשה

    @Query("SELECT * FROM User WHERE userId IN (:ids)")
    List<User> getAllUserByIds(List<Long> ids);
}
