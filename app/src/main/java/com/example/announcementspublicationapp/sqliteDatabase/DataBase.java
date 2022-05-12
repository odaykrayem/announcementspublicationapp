package com.example.announcementspublicationapp.sqliteDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.announcementspublicationapp.mymodels.AnnouncementModel;
import com.example.announcementspublicationapp.mymodels.FavoriteModel;
import com.example.announcementspublicationapp.mymodels.UserModel;


@Database(entities = {UserModel.class, AnnouncementModel.class, FavoriteModel.class}, version = 1, exportSchema = false)
public abstract  class DataBase extends RoomDatabase {

    private static DataBase dataBase;
    public static synchronized DataBase getDataBase(Context ctx){
        if(dataBase == null){
            dataBase = Room.databaseBuilder(
                    ctx,
                    DataBase.class,
                    "main_db"
            ).allowMainThreadQueries()
                    .build();
        }
        return dataBase;
    }

    public abstract DaoUser userDao();
    public abstract DaoAnnouncement announcementDao();
    public abstract DaoFavorite favoriteDao();
}
