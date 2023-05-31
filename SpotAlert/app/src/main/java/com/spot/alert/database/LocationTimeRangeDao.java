package com.spot.alert.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.spot.alert.dataobjects.LocationTimeRange;

import java.util.List;

@Dao
public interface LocationTimeRangeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertLocationTimeRange(LocationTimeRange locationTimeRange);
    
    @Delete
    void deleteLocationTimeRange(LocationTimeRange locationTimeRange);
    @Update
    void updateLocationTimeRange(LocationTimeRange locationTimeRange);

    @Query("SELECT * FROM LocationTimeRange WHERE id =:id")
    LocationTimeRange getLocationTimeRange(Long id);

    @Query("SELECT * FROM LocationTimeRange WHERE locationId =:locationId")
    List<LocationTimeRange> getLocationTimeRangesByLocationId(Long locationId);
}
