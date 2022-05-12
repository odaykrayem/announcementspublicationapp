package com.example.announcementspublicationapp.mymodels;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "users")
public class UserModel implements Serializable {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    String id;
    @ColumnInfo(name = "name")
    String name;
    @ColumnInfo(name = "email")
    String email;
    @ColumnInfo(name = "password")
    String password;
    @ColumnInfo(name = "phone")
    String phone;
    @ColumnInfo(name = "location")
    String location;
    @ColumnInfo(name = "age")
    long age;

    //this constructor to upload to firebase
    @Ignore
    public UserModel(String name, String email, String password, String phone, String location, int age) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.location = location;
        this.age = age;
    }

    //this constructor when we get announcer data from firebase
    @Ignore
    public UserModel(String name, String email, String phone, String location, long age) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.age = age;
    }

    //firebase always need empty constructor
    @Ignore
    public UserModel() {
    }

    public UserModel(String id, String name, String email, String password, String phone, String location, long age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.location = location;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }
}
