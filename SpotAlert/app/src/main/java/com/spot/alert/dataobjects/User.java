package com.spot.alert.dataobjects;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity()
public class User {
    @PrimaryKey(autoGenerate = true)
    public int userId;
    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "imageId")
    private Long imageId;
    @ColumnInfo(name = "userName", defaultValue = "")
    public String userName;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "phoneNumber")
    public String phoneNumber;


    public User() {
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }
    public User(int userId, String email, Long imageId, String userName, String password, String phoneNumber) {
        this.userId = userId;
        this.email = email;
        this.imageId = imageId;
        this.userName = userName;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
