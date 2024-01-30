package com.example.jetpackcomposeexample.aws.helper;

import android.util.Log;

import androidx.core.util.Consumer;

import com.example.jetpackcomposeexample.model.helper.AwsDataModel;
import com.example.jetpackcomposeexample.model.helper.dto.Post;
import com.example.jetpackcomposeexample.utils.UrlConstants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
    public static void connect(String url, Consumer<Post> callback){
        executor.execute(()->{
            Post post;
            if(UrlConstants.IS_LOCAL_HOST) {
                post = connectLocalServer(url);
            } else {
                post = connect(url);
            }
            callback.accept(post);
        });
    }
    public static Post connectLocalServer(String url){
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
                Log.d(TAG,"send data what have fetched from HTTP:"+result);
//                    AwsDataController.sendMessage(AWS_POST_API_RESPONSE, result);
                return AwsDataModel.parsePostContent(result);
            } else {
                Log.d(TAG,"Failed to fetch the car list. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AwsDataModel.parsePostContent(result);
    }
    public static Post connect(String url){
        JSONObject result = new JSONObject();
//        new Thread(()->{
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
                connectionHttps.setRequestMethod("GET");

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
                    Log.d(TAG,"send data what have fetched from HTTPs:"+result);
//                    AwsDataController.sendMessage(AWS_POST_API_RESPONSE, result);
                    return AwsDataModel.parsePostContent(result);
                } else {
                    Log.d(TAG,"Failed to fetch the car list. Response Code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return AwsDataModel.parsePostContent(result);
//        }).start();
    }
    public static void disConnect(){
        connectionHttps.disconnect();
    }
}
