package com.spot.alert.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.spot.alert.dataobjects.UserTimeRange;

import java.util.List;

@Dao
public interface UserTimeRangeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUserTimeRange(UserTimeRange userTimeRange);
    
    @Delete
    void deleteUserTimeRange(UserTimeRange userTimeRange);

    @Update
    void updateUserTimeRange(UserTimeRange userTimeRange);

    @Query("SELECT * FROM UserTimeRange WHERE id =:id")
    UserTimeRange getUserTimeRange(Long id);

    @Query("SELECT * FROM UserTimeRange WHERE userId =:userId")
    List<UserTimeRange> getUserRangesByUserId(Long userId);

    @Query("SELECT DISTINCT(userId) from UserTimeRange where dayWeek=:dayWeek")
    List<Long> getUserIdsAndDay(int dayWeek);

    @Query("SELECT * FROM UserTimeRange WHERE dayWeek=:dayWeek and userId IN (:ids)")
    List<UserTimeRange> getTimeRangesByUserAndDay(List<Long> ids,int dayWeek);

}
