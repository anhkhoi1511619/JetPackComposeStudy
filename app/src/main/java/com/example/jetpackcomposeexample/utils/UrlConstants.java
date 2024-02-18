package com.example.jetpackcomposeexample.utils;

public class UrlConstants {
    public static final boolean IS_LOCAL_HOST = false;
    public static String POST_CONTENT_API_URL = getContentUrl();
    public static String UPLOAD_API_URL = getUploadUrl();

    public static final String POST_CONTENT_API_URL_HTTP = "http://192.168.254.249:3000/postContent";
    public static final String UPLOAD_LOG_API_URL_HTTP = "http://192.168.254.249:3000/uploadLog";
    public static final String UPLOAD_LOG_API_URL_HTTPS = "https://192.168.0.42:4000/uploadLog";
    public static final String POST_CONTENT_API_URL_HTTPS = "https://192.168.0.42:4000/postContent";

    static String getContentUrl() {
        if(IS_LOCAL_HOST) return POST_CONTENT_API_URL_HTTP;
        return POST_CONTENT_API_URL_HTTPS;
    }
    static String getUploadUrl() {
        if(IS_LOCAL_HOST) return UPLOAD_LOG_API_URL_HTTP;
        return UPLOAD_LOG_API_URL_HTTPS;
    }

}
