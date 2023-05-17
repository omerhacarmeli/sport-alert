package com.example.finalproject.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.finalproject.dataobjects.User;

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

    @Query("SELECT * FROM User WHERE email = :email AND password = :password")
    public User login(String email, String password);

    @Query("SELECT COUNT(*) FROM User WHERE email = :email")
    public long
    isEmailExist(String email);
    @Query("SELECT * FROM User WHERE userId= :userId ")
    User getUser(long userId);

}
