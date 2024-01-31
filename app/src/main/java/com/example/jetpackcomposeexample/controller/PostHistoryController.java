package com.example.jetpackcomposeexample.controller;

import android.content.Context;
import android.util.Log;

import androidx.core.util.Consumer;

import com.example.jetpackcomposeexample.database.PostHistoryDao;
import com.example.jetpackcomposeexample.database.PostHistoryDatabase;
import com.example.jetpackcomposeexample.database.PostHistoryEntity;
import com.example.jetpackcomposeexample.model.helper.dto.Post;
import com.example.jetpackcomposeexample.model.helper.history.PostHistoryData;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class PostHistoryController {
    static PostHistoryDatabase db;
    static PostHistoryDao dao;
    static final ScheduledExecutorService historyExecutor = Executors.newScheduledThreadPool(2);


    public PostHistoryController(Context context) {
        db = PostHistoryDatabase.getDatabase(context);
        dao = db.postHistoryDao();
    }


    public static void set(Post post, long currentTime) {
        if(post == null) return;
        PostHistoryEntity entity = new PostHistoryEntity(currentTime, post.getMetaData().getAuthor().getName(), post.getTitle());
        Log.d("PostHistoryController","data base set "+ entity);
        historyExecutor.execute(()->dao.insertPost(entity));
    }

    public static void get(int postNumber, Consumer<List<PostHistoryData>> callback) {
        historyExecutor.execute(()->{
            List<PostHistoryData> list = dao.getPostHistoryList(postNumber).stream()
                    .map(e->new PostHistoryData(e.getDate(), e.getAuthor(), e.getTitle()))
                    .collect(Collectors.toList());
            Log.d("PostHistoryController","data base get "+ list);
            callback.accept(list);
        });
    }
}
