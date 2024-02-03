package com.example.jetpackcomposeexample.controller.helper;

import androidx.core.util.Consumer;

import com.example.jetpackcomposeexample.model.post.AwsDataModel;
import com.example.jetpackcomposeexample.model.post.dto.Post;
import com.example.jetpackcomposeexample.model.post.dto.PostRequest;
import com.example.jetpackcomposeexample.utils.TLog;
import com.example.jetpackcomposeexample.utils.UrlConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class AwsConnectHelper {
    static HttpsURLConnection connectionHttps;
    static HttpURLConnection connectionHttp;
    public static final String TAG = AwsConnectHelper.class.getSimpleName();
    static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    public static void fetchContent(String url, Consumer<Post> callback){
        executor.execute(()->{
            Post post;
            if(UrlConstants.IS_LOCAL_HOST) {
                post = fetchContentByHTTP(url);
            } else {
                post = fetchContentByHTPPs(url);
            }
            callback.accept(post);
        });
    }
    public static Post fetchContentByHTTP(String url){
        JSONObject result = new JSONObject();
        try {
            URL urlConnect = new URL(url);
            connectionHttp = (HttpURLConnection) urlConnect.openConnection();
            connectionHttp.setRequestMethod("GET");

            int responseCode = connectionHttp.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connectionHttp.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                // Output the response
                result = new JSONObject(String.valueOf(response));
                TLog.d(TAG,"Received data what have fetched from HTTPs:"+result);
//                    AwsDataController.sendMessage(AWS_POST_API_RESPONSE, result);
                return AwsDataModel.deserializePost(result);
            } else {
                TLog.d(TAG,"Failed when fetch data. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AwsDataModel.deserializePost(result);
    }
    static JSONObject get() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", 1);
            object.put("machine", "Samsung");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return object;
    }
    public static Post fetchContentByHTPPs(String url){
        JSONObject result = new JSONObject();
            try {
                URL urlConnect = new URL(url);
                SSLContext sslContext = null;
                try {
                    TrustManager[] tm = {
                            new X509TrustManager() {
                                @Override
                                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                                @Override
                                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                                @Override
                                public X509Certificate[] getAcceptedIssuers() { return null; }
                            }
                    };
                    sslContext = SSLContext.getInstance("SSL");
                    sslContext.init(null, tm, null);
                    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                } catch(Exception e) {
                    e.printStackTrace();
                }

                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                connectionHttps = (HttpsURLConnection) urlConnect.openConnection();
                connectionHttps.setRequestMethod("PUT");
                // 3.リクエスとボディに書き込みを行う
                //HttpURLConnectionからOutputStreamを取得し、json文字列を書き込む
                //リクエスト形式をJsonに指定
                connectionHttps.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                PostRequest request = new PostRequest().fill();
                PrintStream ps = new PrintStream(connectionHttps.getOutputStream());
                ps.print(request.serialize());
                ps.close();

                int responseCode = connectionHttps.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connectionHttps.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    in.close();

                    // Output the response
                    result = new JSONObject(String.valueOf(response));
                    TLog.d(TAG,"Received data what have fetched from HTTPs:"+result);
//                    AwsDataController.sendMessage(AWS_POST_API_RESPONSE, result);
                    return AwsDataModel.deserializePost(result);
                } else {
                    TLog.d(TAG,"Failed when fetch data. Response Code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return AwsDataModel.deserializePost(result);
    }
    public static void disConnect(){
        connectionHttps.disconnect();
    }
}
