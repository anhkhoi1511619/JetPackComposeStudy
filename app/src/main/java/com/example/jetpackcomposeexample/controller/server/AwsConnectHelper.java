package com.example.jetpackcomposeexample.controller.server;

import android.os.Build;
import android.util.Log;
import android.webkit.URLUtil;

import androidx.annotation.RequiresApi;
import androidx.core.util.Consumer;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import com.example.jetpackcomposeexample.model.getUrl.GetUrlRequest;
import com.example.jetpackcomposeexample.model.getUrl.GetUrlResponse;
import com.example.jetpackcomposeexample.model.login.Credentials;
import com.example.jetpackcomposeexample.model.login.dto.LoginRequest;
import com.example.jetpackcomposeexample.model.login.dto.LoginResponse;
import com.example.jetpackcomposeexample.model.post.AwsDataModel;
import com.example.jetpackcomposeexample.model.post.dto.Post;
import com.example.jetpackcomposeexample.model.post.dto.PostRequest;
import com.example.jetpackcomposeexample.utils.TLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;


public class AwsConnectHelper {
    //Use singleton to manage
    static AwsConnectHelper instance = null;
    EventListener eventListener;
    public AwsConnectHelper() {
        eventListener = new EventListener() {
            @Override
            public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
                TLog.d(TAG, "接続開始" + inetSocketAddress.toString());
            }

            @Override
            public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol, IOException ioe) {
                TLog.d(TAG,"接続失敗: " + ioe.getMessage());
            }

            @Override
            public void requestBodyStart(Call call) {
                TLog.d(TAG,"リクエスト書き込み開始");
            }

            @Override
            public void requestFailed(Call call, IOException ioe) {
                TLog.d(TAG,"リクエスト書き込み失敗: " + ioe.getMessage());
            }

            @Override
            public void responseBodyStart(Call call) {
                TLog.d(TAG,"レスポンス読み込み開始");
            }

            @Override
            public void responseFailed(Call call, IOException ioe) {
                TLog.d(TAG,"レスポンス読み込み失敗: " + ioe.getMessage());
            }
        };
        this.client = new OkHttpClient().newBuilder()
                      .eventListener(eventListener)
                      .build();
    }

    public static synchronized AwsConnectHelper getInstance() {
        if(instance == null) {
            instance = new AwsConnectHelper();
        }
        return instance;
    }
    final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json");
    final MediaType MT_GZIP = MediaType.parse("application/gzip");

    OkHttpClient client;
    HttpsURLConnection connectionHttps;
    final String TAG = AwsConnectHelper.class.getSimpleName();
    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    ExecutorService sendExecutor = Executors.newSingleThreadExecutor();
    Future<Response> future;

    public Response send(Request r) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        Date timeStamp = new Date();
        Callable<Response> task = () -> client.newCall(r).execute();
        future = sendExecutor.submit(task);
        Response response = future.get(1500, TimeUnit.SECONDS);
        Date now = new Date();
        long timeElapsed = now.getTime() - timeStamp.getTime();
        Log.i(TAG, "Request finished in " + timeElapsed + "ms: " + response);
        return response;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void uploadLogOkHttp(String url, String file, Consumer<Boolean> callback) {
        executor.execute(()->callback.accept(uploadLogOkHttp(url, file)));
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean uploadLogOkHttp(String url, String file) {
        Response response = null;
        boolean isSuccessful = false;
        try {
            if (url.isEmpty()) {
                return false;
            }
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .eventListener(eventListener)
//                    .addInterceptor(logger)
                    .retryOnConnectionFailure(true)
                    .build();
            byte[] data = Files.readAllBytes(Paths.get(file));
            RequestBody body = RequestBody.create(
                    data,
                    MT_GZIP);
            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .header("Content-Length", String.valueOf(data.length))
                    .header("Content-Type", "application/gzip")
                    .build();
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                TLog.d(TAG, "Log uploaded successfully!");
            } else {
                Log.e(TAG,"Failed to upload file: " + response.code() + " " + response.message());
            }
            isSuccessful = response.isSuccessful();
        } catch (IOException e) {
            Log.e(TAG, "error while making upload request, " + e);
        } finally {
            if (response != null) {
                response.close(); // 必ず close() を呼び出す
            }
        }
        return isSuccessful;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void download(String url, String saveTo, Consumer<Boolean> callback) {
        executor.execute(() -> callback.accept(download(url, saveTo)));
    }

    public boolean download(String url, String saveTo) {
        if (url == null || !url.startsWith("http")) {
            Log.w(TAG, "DOWNLOAD, Link not start with http (" + url + "), skip");
            return false;
        }
        Response response = null;
        boolean isSuccessful = false;
        try {
            String filename = URLUtil.guessFileName(url, null, null);
            Log.d(TAG, "DOWNLOAD, file name = " + filename);
            File dir = new File(saveTo);
            dir.mkdirs();
            File file = new File(saveTo + "/" + filename);
            Request request = new Request.Builder().url(url).build();
            response = client.newCall(request).execute();
            if (response.body() == null) {
                return false;
            }
            BufferedSource source = response.body().source();
            BufferedSink sink = Okio.buffer(Okio.sink(file));
            Buffer buffer = sink.getBuffer();
            final int DOWNLOAD_CHUNK_SIZE = 8 * 1024;
            while (source.read(buffer, DOWNLOAD_CHUNK_SIZE) != -1) {
                sink.emit();
            }
            sink.flush();
            sink.close();
            source.close();
            isSuccessful = true;
        } catch (IOException e) {
            Log.e(TAG, "download failed " + e.getMessage());
        } finally {
            if (response != null) {
                response.close(); // 必ず close() を呼び出す
            }
        }
        return isSuccessful;
    }

    public void getUrl(String url, GetUrlRequest request, Consumer<GetUrlResponse> callback) {
        executor.execute(()->callback.accept(getUrl(url, request)));
    }
    public GetUrlResponse getUrl(String url, GetUrlRequest urlRequest) {
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, urlRequest.serialize().toString());
        TLog.d(TAG, "Requesting URL:"+url+" request data: " + urlRequest.serialize().toString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = null;
        GetUrlResponse urlResponse = null;
        try {
            response = send(request);
            if (response.isSuccessful()) {
                JSONObject object = new JSONObject(response.body().string());
                TLog.d(TAG, "Received data what have fetched from OkHttps:" + object);
                GetUrlResponse urlRes = new GetUrlResponse();
                urlRes.deserialize(object);
                urlResponse = urlRes;
            }
        } catch (IOException | JSONException e) {
            TLog.d(TAG, "IOException | JSONException");
        } catch (ExecutionException | InterruptedException| TimeoutException e) {
            TLog.d(TAG, "ExecutionException | InterruptedException | TimeoutException");
        } finally {
            if (response != null) {
                response.close(); // 必ず close() を呼び出す
            }
            if (future != null) {
                future.cancel(true); // 必ず close() を呼び出す
            }
        }
        return urlResponse;
    }
    //    public void upload(String url, Consumer<Boolean> callback) {
//        executor.execute(()->callback.accept(uploadLog(url)));
//    }
//
//    public void download(String url, Consumer<Boolean> callback) {
//        executor.execute(()-> callback.accept(download(url, "/sdcard/DCIM/ProfileApp/")));
//    }
//    Boolean download(String url, String saveTo) {
//        try {
//            File dir = new File(saveTo);
//            if(dir.mkdirs()) {
//                Log.d(TAG, "Folder is created successfully");
//            } else {
//                Log.d(TAG, "Folder is exists");
//            }
//
//            File file = new File(saveTo+"/"+"11");
//            Request request = new Request.Builder().url(url).build();
//            Response response = client.newCall(request).execute();
//            if(response.body() == null) return false;
//
//            BufferedSource source = response.body().source();
//            BufferedSink sink = Okio.buffer(Okio.sink(file));
//            Buffer buffer = sink.getBuffer();
//            final int DOWNLOAD_CHUNK_SIZE = 8*1024;
//            while (source.read(buffer, DOWNLOAD_CHUNK_SIZE) != -1) {
//                sink.emit();
//            }
//            sink.flush();
//            sink.close();
//            source.close();
//            return true;
//        } catch (IOException e) {
//            Log.d(TAG, "download fail");
//        }
//        return false;
//    }
//    Boolean uploadLog(String url) {
//        if(UrlConstants.IS_LOCAL_HOST) return uploadLogByHTTP(url);
//        return uploadLogByHTTPS(url);
//    }
//    Boolean uploadLogByHTTP(String url) {
//        try {
//            HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//            connection.setRequestMethod("PUT");
//            connection.setRequestProperty("Content-type", "application/gzip");
//            connection.connect();
//
//            BufferedOutputStream count = new BufferedOutputStream(connection.getOutputStream());
//            BufferedInputStream fin = new BufferedInputStream(Files.newInputStream(Paths.get("/sdcard/DCIM/ProfileApp/logcat.tar.gz")));///sdcard/DCIM/ProfileApp/logcat.tar.gz
//            int x;
//            final int UPLOAD_CHUNK_SIZE = 1024;
//            byte[] bytes = new byte[UPLOAD_CHUNK_SIZE];
//            while ((x = fin.read(bytes, 0, bytes.length)) > 0) {
//                count.write(bytes, 0, x);
//            }
//            fin.close();
//            count.close();
//            int code = connection.getResponseCode();
//            String message = connection.getResponseMessage();
//            if(code == 200) {
//                Log.d(TAG, "upload finished done with code = "+code+", message: "+message);
//                return true;
//            }
//            Log.d(TAG, "upload finished fail with code = "+code+", message: "+message);
//            return false;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    Boolean uploadLogByHTTPS(String url) {
//        try {
//            connectionHttps = (HttpsURLConnection) (new URL(url)).openConnection();
//            connectionHttps.setDoInput(true);
//            connectionHttps.setDoOutput(true);
//            connectionHttps.setRequestMethod("PUT");
//            connectionHttps.setRequestProperty("Content-type", "application/gzip");
//            connectionHttps.connect();
//
//            BufferedOutputStream count = new BufferedOutputStream(connectionHttps.getOutputStream());
//            BufferedInputStream fin = new BufferedInputStream(Files.newInputStream(Paths.get("/sdcard/DCIM/ProfileApp/logcat.tar.gz")));///sdcard/DCIM/ProfileApp/logcat.tar.gz
//            int x;
//            final int UPLOAD_CHUNK_SIZE = 1024;
//            byte[] bytes = new byte[UPLOAD_CHUNK_SIZE];
//            while ((x = fin.read(bytes, 0, bytes.length)) > 0) {
//                count.write(bytes, 0, x);
//            }
//            fin.close();
//            count.close();
//            int code = connectionHttps.getResponseCode();
//            String message = connectionHttps.getResponseMessage();
//            if(code == 200) {
//                Log.d(TAG, "upload finished done with code = "+code+", message: "+message);
//                return true;
//            }
//            Log.d(TAG, "upload finished fail with code = "+code+", message: "+message);
//            return false;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void login(String url, Consumer<Boolean> callback, Credentials credentials) {
        executor.execute(()->callback.accept(loginByOkHttp(url, credentials.getLogin(), credentials.getPassword())));
    }
    Boolean loginByOkHttp(String url, String id, String password) {
        LoginRequest requestBody = new LoginRequest().fill(id, password);
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, requestBody.serialize().toString());
        TLog.d(TAG, "Requesting URL:"+url+" request data: " + requestBody.serialize().toString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = null;
        boolean isSuccessful = false;
        try {
            response = send(request);
            if (response.isSuccessful()) {
                JSONObject object = new JSONObject(response.body().string());
                TLog.d(TAG, "Received data what have fetched from OkHttps:" + object);
                LoginResponse loginRes = new LoginResponse();
                loginRes.deserialize(object);
                isSuccessful = true;
            }
        } catch (IOException | JSONException e) {
            Log.d(TAG, "Exception");
        } catch (ExecutionException | InterruptedException| TimeoutException e) {
            TLog.d(TAG, "ExecutionException | InterruptedException | TimeoutException");
        } finally {
            if (response != null) {
                response.close(); // 必ず close() を呼び出す
            }
            if (future != null) {
                future.cancel(true); // 必ず close() を呼び出す
            }
        }
        return isSuccessful;
    }
    public void fetchDetailProfile(int id, String url, Consumer<Post> callback){
        executor.execute(()->callback.accept(fetchDetailProfileByOkHttp(id,url)));
    }
    Post fetchDetailProfileByOkHttp(int id, String url) {
        PostRequest requestBody = new PostRequest().fill(id);
        TLog.d(TAG, "Requesting URL:"+url+" request data: ID" + id);
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, requestBody.serialize().toString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", LoginResponse.getAuthorization())//TODO:Add Interceptor
                .build();
        Response response = null;
        Post post = null;
        try {
            response = send(request);
            if (response.isSuccessful()) {
                JSONObject object = new JSONObject(response.body().string());
                TLog.d(TAG, "Received data what have fetched from OkHttps:" + object);
                post = AwsDataModel.deserializeDetailProfile(object);
            }
        } catch (IOException | JSONException e) {
            Log.d(TAG, "Exception");
        }  catch (ExecutionException | InterruptedException| TimeoutException e) {
            TLog.d(TAG, "ExecutionException | InterruptedException | TimeoutException");
        } finally {
            if (response != null) {
                response.close(); // 必ず close() を呼び出す
            }
            if (future != null) {
                future.cancel(true); // 必ず close() を呼び出す
            }
        }
        return post;
    }
    void disConnect(){
        connectionHttps.disconnect();
    }
}
