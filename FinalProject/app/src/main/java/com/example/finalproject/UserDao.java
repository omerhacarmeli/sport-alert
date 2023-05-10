package com.example.finalproject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(User user);
    @Delete
    void deleteUser(User user);
    @Update
    void updateUser(User user);

    @Query("SELECT * FROM User WHERE userId =:userId")
   LiveData<User>getUserInfo(int userId);

    @Query("SELECT COUNT(*) FROM User WHERE userName = :userName AND password = :password")
    public boolean isUserExist(String userName, String password);

}
