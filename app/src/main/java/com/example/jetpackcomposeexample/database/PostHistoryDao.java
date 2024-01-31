package com.example.jetpackcomposeexample.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PostHistoryDao {
    @Insert
    void insertPost(PostHistoryEntity postHistoryEntity);
    @Query("SELECT PostHistoryEntity.* FROM PostHistoryEntity ORDER BY `no` DESC LIMIT :number")
    List<PostHistoryEntity> getPostHistoryList(int number);
}
