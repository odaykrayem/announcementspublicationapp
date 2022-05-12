package com.example.announcementspublicationapp.mymodels;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "announcements")
public class AnnouncementModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "image_path")
    String image_path;
    @ColumnInfo(name = "title")
    String ann_title;
    @ColumnInfo(name = "price")
    long ann_price;
    @ColumnInfo(name = "description")
    String ann_description;
    @ColumnInfo(name = "location")
    String ann_location;
    @ColumnInfo(name = "longitude")
    double ann_longitude;
    @ColumnInfo(name = "latitude")
    double ann_latitude;
    @ColumnInfo(name = "announcer_name")
    String announcer_name;

    //we use this id to know what announcements stored as favorites in firebase
    //@Ignore to not save it in sqlite we dont need it offline
    @Ignore
    String firebaseId;


    @Ignore
    UserModel announcer_data;

    public AnnouncementModel() { }

    public AnnouncementModel(String image_path, String ann_title, long ann_price, String ann_description, String ann_location, double ann_longitude, double ann_latitude, String firebaseId, UserModel announcer_data) {
        this.image_path = image_path;
        this.ann_title = ann_title;
        this.ann_price = ann_price;
        this.ann_description = ann_description;
        this.ann_location = ann_location;
        this.ann_longitude = ann_longitude;
        this.ann_latitude = ann_latitude;
        this.firebaseId = firebaseId;
        this.announcer_data = announcer_data;
    }

    //for sqlite
    public AnnouncementModel(String image_path, String ann_title, long ann_price, String ann_description, String ann_location, double longitude, double ann_latitude, String announcer_name) {
        this.image_path = image_path;
        this.ann_title = ann_title;
        this.ann_price = ann_price;
        this.ann_description = ann_description;
        this.ann_location = ann_location;
        this.ann_longitude = longitude;
        this.ann_latitude = ann_latitude;
        this.announcer_name = announcer_name;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public void setAnn_title(String ann_title) {
        this.ann_title = ann_title;
    }

    public void setAnn_price(long ann_price) {
        this.ann_price = ann_price;
    }

    public void setAnn_description(String ann_description) {
        this.ann_description = ann_description;
    }

    public void setAnn_location(String ann_location) {
        this.ann_location = ann_location;
    }

    public void setAnn_longitude(double ann_longitude) {
        this.ann_longitude = ann_longitude;
    }

    public void setAnn_latitude(double ann_latitude) {
        this.ann_latitude = ann_latitude;
    }

    public String getAnnouncer_name() {
        return announcer_name;
    }

    public void setAnnouncer_name(String announcer_name) {
        this.announcer_name = announcer_name;
    }


    public void setAnnouncer_data(UserModel announcer_data) {
        this.announcer_data = announcer_data;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getAnn_title() {
        return ann_title;
    }

    public long getAnn_price() {
        return ann_price;
    }

    public String getAnn_description() {
        return ann_description;
    }

    public String getAnn_location() {
        return ann_location;
    }

    public double getAnn_longitude() {
        return ann_longitude;
    }

    public double getAnn_latitude() {
        return ann_latitude;
    }

    public UserModel getAnnouncer_data() {
        return announcer_data;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        AnnouncementModel announcementModel = (AnnouncementModel) obj;
        return firebaseId.equals(announcementModel.getFirebaseId());
    }
}
