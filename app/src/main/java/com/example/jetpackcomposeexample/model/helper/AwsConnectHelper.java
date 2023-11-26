package com.example.jetpackcomposeexample.model.helper;

import static com.example.jetpackcomposeexample.controller.AwsDataController.AWS_POST_API_RESPONSE;

import android.util.Log;

import com.example.jetpackcomposeexample.controller.AwsDataController;
import com.example.jetpackcomposeexample.model.helper.dto.Post;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class AwsConnectHelper {
    static HttpsURLConnection connection;

    public static final String TAG = AwsConnectHelper.class.getSimpleName();
    public static void connect(String url){
        new Thread(()->{
            try {
                URL carListUrl = new URL(url);
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
                connection = (HttpsURLConnection) carListUrl.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    in.close();

                    // Output the response
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    Log.d(TAG,"send data what have fetched:"+jsonObject);
                    AwsDataController.sendMessage(AWS_POST_API_RESPONSE, jsonObject);
                } else {
                    Log.d(TAG,"Failed to fetch the car list. Response Code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public static void disConnect(){
        connection.disconnect();
    }
}
