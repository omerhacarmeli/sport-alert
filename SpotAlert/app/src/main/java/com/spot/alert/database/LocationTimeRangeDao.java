package com.spot.alert.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.LocationTimeRange;

import java.util.List;

@Dao
public interface LocationTimeRangeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertLocation(LocationTimeRange locationTimeRange);
    
    @Delete
    void deleteLocation(LocationTimeRange locationTimeRange);
    @Update
    void updateLocation(LocationTimeRange locationTimeRange);

    @Query("SELECT * FROM LocationTimeRange WHERE id =:id")
    LiveData<LocationTimeRange> getLocationRange(Long id);

    @Query("SELECT * FROM LocationTimeRange WHERE locationId =:locationId")
    LiveData<List<LocationTimeRange>> getLocationRangesByLocationId(Long locationId);
}
