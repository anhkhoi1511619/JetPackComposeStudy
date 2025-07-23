package com.example.jetpackcomposeexample.utils;

public class UrlConstants {
    public static final boolean IS_LOCAL_HOST = false;
    public static String DETAIL_PROFILE_API_URL = getDetailProfileUrl();
    public static String UPLOAD_API_URL = getUploadUrl();
    public static String LOGIN_API_URL = loginUrl();
    public static String BALANCE_URL = getBalance();


    public static final String DETAIL_PROFILE_API_URL_LOCAL_HOST = "http://192.168.105.104:5174/profile/detail";
    public static final String BALANCE_API_URL_LOCAL_HOST = "http://192.168.105.104:5174/subtract-balance";
    public static final String BALANCE_API_URL_HTTPS = "https://5z732cnm-5173.asse.devtunnels.ms/subtract-balance";


    public static final String UPLOAD_LOG_API_URL_HTTP = "http://192.168.105.104:5174/upload-log";
    public static final String UPLOAD_LOG_API_URL_HTTPS = "https://5z732cnm-5173.asse.devtunnels.ms/upload-log";
    public static final String LOGIN_API_URL_HTTP = "http://192.168.105.104:5174/login";
    public static final String LOGIN_API_URL_HTTPS = "https://5z732cnm-5173.asse.devtunnels.ms/login";
    public static final String POST_CONTENT_API_URL_HTTPS = "https://5z732cnm-5173.asse.devtunnels.ms/profile/detail";
    static String getBalance() {
        if(IS_LOCAL_HOST) return BALANCE_API_URL_LOCAL_HOST;
        return BALANCE_API_URL_HTTPS;
    }
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
