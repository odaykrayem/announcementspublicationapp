package com.example.announcementspublicationapp.sqliteDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.announcementspublicationapp.mymodels.UserModel;

import java.util.List;

@Dao
public interface DaoUser {

    @Query("SELECT * FROM users ORDER BY id DESC")
    List<UserModel> getAllUsers();

    @Query("SELECT * FROM users")
    UserModel getUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserModel userModel);

    @Query("DELETE FROM users")
    void deleteAll();
}
