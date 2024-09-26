package com.example.jetpackcomposeexample.controller.server;


import static com.example.jetpackcomposeexample.utils.UrlConstants.LOGIN_API_URL;

import androidx.annotation.NonNull;

import com.example.jetpackcomposeexample.model.login.dto.LoginRequest;
import com.example.jetpackcomposeexample.model.login.dto.LoginResponse;
import com.example.jetpackcomposeexample.utils.TLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccessTokenInterceptor implements Interceptor {
    public static LoginRequest loginRequest;
    public static String lastToken = "";
    static OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
    static MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Request request = newRequestWithAccessToken(chain.request(), lastToken);

        Response response = chain.proceed(request);

        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            TLog.d("AWS", "POST/detailProfile HTTP_UNAUTHORIZED");

            synchronized (this) {
                loginAccessToken();
                changeUI();
                response.close();
                return chain.proceed(newRequestWithAccessToken(request, lastToken));
            }
        }

        return response;
    }

    @NonNull
    private Request newRequestWithAccessToken(@NonNull Request request, @NonNull String accessToken) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }

    /**
     * ログイン後、アクセストークンを取得
     */
    public void loginAccessToken() {
        if(AwsConnectHelper.getInstance().loginByOkHttp(LOGIN_API_URL, loginRequest.id, loginRequest.password)){
            TLog.d("AWS", "fetching access token with "+lastToken);
        }
    }

    /**
     * 画面を切り替え
     */
    private void changeUI() {
        //TODO: 処理を追加
    }
}
