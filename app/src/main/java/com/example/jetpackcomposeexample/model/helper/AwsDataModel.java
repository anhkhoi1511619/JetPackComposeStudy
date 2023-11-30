package com.example.jetpackcomposeexample.model.helper;

import com.example.jetpackcomposeexample.R;
import com.example.jetpackcomposeexample.model.helper.dto.MetaData;
import com.example.jetpackcomposeexample.model.helper.dto.Paragraph;
import com.example.jetpackcomposeexample.model.helper.dto.ParagraphType;
import com.example.jetpackcomposeexample.model.helper.dto.Post;
import com.example.jetpackcomposeexample.model.helper.dto.PostAuthor;
import com.example.jetpackcomposeexample.model.helper.dto.Publication;
import com.example.jetpackcomposeexample.model.helper.dto.Chart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AwsDataModel {
    public static Post post;
    public static List<Chart> chartList;
    public static void parsePostContent(JSONObject jsonObject) {
        String id_data;
        String title_data;
        String subtitle_data;
        String url;
        JSONObject publication;
        String name_publication;
        String url_publication;
        JSONObject metaData;
        JSONObject author;
        String name_author;
        String url_author;
        String date_metaData;
        JSONArray paragraphArray;
        List<Paragraph> paragraphs = new ArrayList<>();
        try {
            id_data = (String) jsonObject.get("id");
            title_data =  (String) jsonObject.get("title");
            subtitle_data = (String) jsonObject.get("subtitle");
            url = (String) jsonObject.get("url");
            publication = (JSONObject) jsonObject.get("publication");
            name_publication = (String) publication.get("name");
            url_publication = (String) publication.get("url");
            metaData = (JSONObject) jsonObject.get("metaData");
            author = (JSONObject) metaData.get("author");
            name_author = (String) author.get("name");
            url_author = (String) author.get("url");
            date_metaData = (String) metaData.get("date");
            paragraphArray = (JSONArray) jsonObject.get("paragraph");

            for (int i=0; i < paragraphArray.length(); i++) {
                int type = (int)paragraphArray.getJSONObject(i).get("type");
                String text = (String) paragraphArray.getJSONObject(i).get("text");
                int markups = (int)paragraphArray.getJSONObject(i).get("markups");
                Paragraph paragraph = new Paragraph(
                        ParagraphType.Header,
                        text,
                        new ArrayList<>()
                );
                paragraphs.add(paragraph);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        post =  new Post(
                id_data,
                title_data,
                subtitle_data,
                url,
                new Publication(
                        name_publication,
                        url_publication
                ),
                new MetaData(
                        new PostAuthor(
                                name_author,
                                url_author
                        ),
                        date_metaData,
                        1
                ),
                paragraphs,
                R.drawable.post_3,
                R.drawable.post_3_thumb
        );
    }
    public static void parseChartData(JSONObject jsonObject) {
        JSONArray array;
        try {
            array = (JSONArray) jsonObject.get("chart");
            for (int i=0; i < array.length(); i++) {
                int x = (int)array.getJSONObject(i).get("x");
                int y = (int)array.getJSONObject(i).get("y");
                Chart chart = new Chart(x,y);
                chartList.add(chart);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }
}
