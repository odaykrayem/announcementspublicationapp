package com.example.announcementspublicationapp.sqliteDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.announcementspublicationapp.mymodels.AnnouncementModel;
import java.util.List;

@Dao
public interface DaoAnnouncement {

    @Query("SELECT * FROM announcements ORDER BY id DESC")
    List<AnnouncementModel> getAllAnnouncements();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAnnouncement(AnnouncementModel announcementModel);

    @Query("DELETE FROM announcements")
    void deleteAll();

}
