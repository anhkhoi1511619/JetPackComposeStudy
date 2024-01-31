package com.example.jetpackcomposeexample.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {PostHistoryEntity.class}, version = 2, exportSchema = false)
public abstract class PostHistoryDatabase extends RoomDatabase {
    public abstract PostHistoryDao postHistoryDao();
    private static PostHistoryDatabase postHistoryDatabase;

    public static synchronized PostHistoryDatabase getDatabase(Context context) {
        if(postHistoryDatabase == null) {
            postHistoryDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    PostHistoryDatabase.class, "PostHistoryDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return postHistoryDatabase;
    }
}
