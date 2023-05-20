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

@Dao
public interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertLocation(Location location);
    @Delete
    void deleteLocation(Location location);
    @Update
    void updateLocation(Location location);

    @Query("SELECT * FROM Location WHERE id =:id")
    LiveData<Location> getLocation(Long id);
}
