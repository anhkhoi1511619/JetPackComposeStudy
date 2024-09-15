package com.example.jetpackcomposeexample.utils;

public class UrlConstants {
    public static final boolean IS_LOCAL_HOST = true;
    public static String DETAIL_PROFILE_API_URL = getDetailProfileUrl();
    public static String UPLOAD_API_URL = getUploadUrl();
    public static String LOGIN_API_URL = loginUrl();

    public static final String DETAIL_PROFILE_API_URL_LOCAL_HOST = "http://192.168.192.42:5173/profile/detail";
    public static final String UPLOAD_LOG_API_URL_HTTP = "http://192.168.254.249:3000/uploadLog";
    public static final String UPLOAD_LOG_API_URL_HTTPS = "https://192.168.0.42:4000/uploadLog";
    public static final String LOGIN_API_URL_HTTP = "http://192.168.192.42:5173/login";
    public static final String LOGIN_API_URL_HTTPS = "https://192.168.125.42:4000/login";
    public static final String POST_CONTENT_API_URL_HTTPS = "https://192.168.0.42:4000/postContent";

    static String getDetailProfileUrl() {
        if(IS_LOCAL_HOST) return DETAIL_PROFILE_API_URL_LOCAL_HOST;
        return POST_CONTENT_API_URL_HTTPS;
    }
    static String getUploadUrl() {
        if(IS_LOCAL_HOST) return UPLOAD_LOG_API_URL_HTTP;
        return UPLOAD_LOG_API_URL_HTTPS;
    }
    static String loginUrl() {
        if(IS_LOCAL_HOST) return LOGIN_API_URL_HTTP;
        return LOGIN_API_URL_HTTPS;
    }

}
