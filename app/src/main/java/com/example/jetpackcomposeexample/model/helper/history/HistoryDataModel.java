package com.example.jetpackcomposeexample.model.helper.history;

import java.util.Arrays;
import java.util.List;

public class HistoryDataModel {
    public static PostHistoryData model1 = new PostHistoryData(System.currentTimeMillis(), "Nguyen Tran Anh Khoi1", "Test1");
    public static List<PostHistoryData> list = Arrays.asList(
            new PostHistoryData(System.currentTimeMillis(), "Nguyen Tran Anh Khoi1", "Test1"),
            new PostHistoryData(System.currentTimeMillis(), "Nguyen Tran Anh Khoi2", "Test2"),
            new PostHistoryData(System.currentTimeMillis(), "Nguyen Tran Anh Khoi3", "Test3"));


}
