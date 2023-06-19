package com.spot.alert.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.spot.alert.dataobjects.ImageEntity;
import com.spot.alert.dataobjects.Location;
@Dao
public interface ImageEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertImageEntity(ImageEntity imageEntity);

    @Delete
    void deleteImageEntity(ImageEntity imageEntity);

    @Update
    void updateImageEntity(ImageEntity imageEntity);

    @Query("SELECT * FROM images WHERE id =:id")
    ImageEntity getImageEntity(Long id);
}
