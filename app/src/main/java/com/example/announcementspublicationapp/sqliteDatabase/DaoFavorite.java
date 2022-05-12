package com.example.announcementspublicationapp.sqliteDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.announcementspublicationapp.mymodels.AnnouncementModel;
import com.example.announcementspublicationapp.mymodels.FavoriteModel;

import java.util.List;

@Dao
public interface DaoFavorite {

    @Query("SELECT * FROM favorite ORDER BY id DESC")
    List<FavoriteModel> getAllFavorites();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavorite(FavoriteModel favoriteModel);

    @Delete
    void deleteFavorite(FavoriteModel favoriteModel);

    @Query("DELETE FROM favorite WHERE firebase_id = :id")
    void deleteById(String id);

    @Query("DELETE FROM favorite")
    void deleteAll();

}
