package com.example.jetpackcomposeexample.controller.server;

import android.util.Log;

import androidx.core.util.Consumer;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import com.example.jetpackcomposeexample.model.login.Credentials;
import com.example.jetpackcomposeexample.model.login.dto.CredentialsRequest;
import com.example.jetpackcomposeexample.model.post.AwsDataModel;
import com.example.jetpackcomposeexample.model.post.dto.Post;
import com.example.jetpackcomposeexample.model.post.dto.PostRequest;
import com.example.jetpackcomposeexample.utils.TLog;
import com.example.jetpackcomposeexample.utils.UrlConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    //Use singleton to manage
    static AwsConnectHelper instance = null;
    public static synchronized AwsConnectHelper getInstance() {
        if(instance == null) {
            instance = new AwsConnectHelper();
        }
        return instance;
    }
    final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();
    HttpsURLConnection connectionHttps;
    HttpURLConnection connectionHttp;
    final String TAG = AwsConnectHelper.class.getSimpleName();
    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    public void fetchContent(String url, Consumer<Post> callback){
        executor.execute(()->{
            Post post;
            if(UrlConstants.IS_LOCAL_HOST) {
                post = fetchContentByHTTP(url);
            } else {
                post = fetchContentByOkHttp(url);
//                post = fetchContentByHTPPs(url);
            }
            callback.accept(post);
        });
    }
    public void login(String url, Consumer<Boolean> callback, Credentials credentials) {
        executor.execute(()->callback.accept(loginByOkHttp(url, credentials.getLogin(), credentials.getPassword())));
    }
    public void upload(String url, Consumer<Boolean> callback) {
        executor.execute(()->callback.accept(uploadLog(url)));
    }

    public void download(String url, Consumer<Boolean> callback) {
        executor.execute(()-> callback.accept(download(url, "/sdcard/DCIM/ProfileApp/")));
    }
    Boolean download(String url, String saveTo) {
        try {
            File dir = new File(saveTo);
            if(dir.mkdirs()) {
                Log.d(TAG, "Folder is created successfully");
            } else {
                Log.d(TAG, "Folder is exists");
            }

            File file = new File(saveTo+"/"+"11");
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            if(response.body() == null) return false;

            BufferedSource source = response.body().source();
            BufferedSink sink = Okio.buffer(Okio.sink(file));
            Buffer buffer = sink.getBuffer();
            final int DOWNLOAD_CHUNK_SIZE = 8*1024;
            while (source.read(buffer, DOWNLOAD_CHUNK_SIZE) != -1) {
                sink.emit();
            }
            sink.flush();
            sink.close();
            source.close();
            return true;
        } catch (IOException e) {
            Log.d(TAG, "download fail");
        }
        return false;
    }
    Boolean uploadLog(String url) {
//        return uploadLogByHTTP(url);
        if(UrlConstants.IS_LOCAL_HOST) return uploadLogByHTTP(url);
        return uploadLogByHTTPS(url);
    }
    Boolean uploadLogByHTTP(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-type", "application/gzip");
            connection.connect();

            BufferedOutputStream count = new BufferedOutputStream(connection.getOutputStream());
            BufferedInputStream fin = new BufferedInputStream(Files.newInputStream(Paths.get("/sdcard/DCIM/ProfileApp/logcat.tar.gz")));///sdcard/DCIM/ProfileApp/logcat.tar.gz
            int x;
            final int UPLOAD_CHUNK_SIZE = 1024;
            byte[] bytes = new byte[UPLOAD_CHUNK_SIZE];
            while ((x = fin.read(bytes, 0, bytes.length)) > 0) {
                count.write(bytes, 0, x);
            }
            fin.close();
            count.close();
            int code = connection.getResponseCode();
            String message = connection.getResponseMessage();
            if(code == 200) {
                Log.d(TAG, "upload finished done with code = "+code+", message: "+message);
                return true;
            }
            Log.d(TAG, "upload finished fail with code = "+code+", message: "+message);
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    Boolean uploadLogByHTTPS(String url) {
        try {
            disableCertificateValidationHTTPs();
            connectionHttps = (HttpsURLConnection) (new URL(url)).openConnection();
            connectionHttps.setDoInput(true);
            connectionHttps.setDoOutput(true);
            connectionHttps.setRequestMethod("PUT");
            connectionHttps.setRequestProperty("Content-type", "application/gzip");
            connectionHttps.connect();

            BufferedOutputStream count = new BufferedOutputStream(connectionHttps.getOutputStream());
            BufferedInputStream fin = new BufferedInputStream(Files.newInputStream(Paths.get("/sdcard/DCIM/ProfileApp/logcat.tar.gz")));///sdcard/DCIM/ProfileApp/logcat.tar.gz
            int x;
            final int UPLOAD_CHUNK_SIZE = 1024;
            byte[] bytes = new byte[UPLOAD_CHUNK_SIZE];
            while ((x = fin.read(bytes, 0, bytes.length)) > 0) {
                count.write(bytes, 0, x);
            }
            fin.close();
            count.close();
            int code = connectionHttps.getResponseCode();
            String message = connectionHttps.getResponseMessage();
            if(code == 200) {
                Log.d(TAG, "upload finished done with code = "+code+", message: "+message);
                return true;
            }
            Log.d(TAG, "upload finished fail with code = "+code+", message: "+message);
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    Boolean loginByOkHttp(String url, String id, String password) {
        client = disableCertificateValidation();

        CredentialsRequest requestBody = new CredentialsRequest().fill(id, password);
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, requestBody.serialize().toString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                TLog.d(TAG, "Login from OkHttps Successfully");
                return true;
            }
        } catch (IOException e) {
            Log.d(TAG, "Exception");
        }
        return false;
    }
    Post fetchContentByOkHttp(String url) {
        client = disableCertificateValidation();

        PostRequest requestBody = new PostRequest().fill();
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, requestBody.serialize().toString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject object = new JSONObject(response.body().string());
                TLog.d(TAG, "Received data what have fetched from OkHttps:" + object);
                return AwsDataModel.deserializePost(object);
            }
        } catch (IOException | JSONException e) {
            Log.d(TAG, "Exception");
        }
        return AwsDataModel.deserializePost(new JSONObject());
    }
    Post fetchContentByHTTP(String url){
        JSONObject result = new JSONObject();
        try {
            URL urlConnect = new URL(url);
            connectionHttp = (HttpURLConnection) urlConnect.openConnection();
            connectionHttp.setRequestMethod("POST");
            connectionHttp.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            PostRequest request = new PostRequest().fill();
            PrintStream ps = new PrintStream(connectionHttp.getOutputStream());
            ps.print(request.serialize());
            ps.close();
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
                TLog.d(TAG,"Received data what have fetched from HTTP:"+result);
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
    Post fetchContentByHTPPs(String url){
        JSONObject result = new JSONObject();
        try {
            URL urlConnect = new URL(url);
            disableCertificateValidationHTTPs();
            connectionHttps = (HttpsURLConnection) urlConnect.openConnection();
            connectionHttps.setRequestMethod("POST");
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
    OkHttpClient disableCertificateValidation() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an OkHttpClient that trusts all certificates
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true);

            return builder.build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new OkHttpClient();
    }
    void disableCertificateValidationHTTPs() {
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
    }
    void disConnect(){
        connectionHttps.disconnect();
    }
}
