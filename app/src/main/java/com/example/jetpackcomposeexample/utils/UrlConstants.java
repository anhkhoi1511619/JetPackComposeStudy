package com.example.jetpackcomposeexample.utils;

public class UrlConstants {
    public static final boolean IS_LOCAL_HOST = false;
    public static String POST_CONTENT_API_URL = getUrl();

    public static final String POST_CONTENT_API_URL_HTTP = "http://192.168.254.249:3000/postContent";
    public static final String POST_CONTENT_API_URL_HTTPS = "https://192.168.33.25:4000/postContent";

    static String getUrl() {
        if(IS_LOCAL_HOST) return POST_CONTENT_API_URL_HTTP;
        return POST_CONTENT_API_URL_HTTPS;
    }

}
