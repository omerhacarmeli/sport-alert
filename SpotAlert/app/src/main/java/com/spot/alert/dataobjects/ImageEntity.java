package com.spot.alert.dataobjects;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "images")
public class ImageEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] imageData;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}