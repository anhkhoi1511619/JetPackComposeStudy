package com.example.jetpackcomposeexample.controller.server;

import okhttp3.Interceptor


public class AccessTokenInterceptor implements Interceptor {
    static String lastToken = "";
    static OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
    static MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Request request = newRequestWithAccessToken(chain.request(), lastToken);

        Response response = chain.proceed(request);

        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            synchronized (this) {
                lastToken = loginAccessToken();
                //TLog.d("AWS", "fetched access token: "+lastToken);
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
                //.header("ABT-ReaderId", "0000001")
                .build();
    }

    /**
     * ログイン後、アクセストークンを取得
     */
    public String loginAccessToken() {
//        var input = UserInfoRepository.clientId +":"+ UserInfoRepository.clientSecret;
        //TLog.d("AWS", "fetching access token with "+input);
        //var input = CAR_ID +":"+ CLIENT_SECRET;
//        var credential = Base64
//                .getEncoder()
//                .encodeToString(input.getBytes());
//        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + UserInfoRepository.unitId);
//        var url = "";
//        switch (CurrentOperateInfo.getRuntimeEnvironment()) {
//            case PRODUCTION:
//                url = API_OAUTH_TOKEN;
//                break;
//            case NORMAL_TEST:
//                url = API_OAUTH_TOKEN_STAGING;
//                break;
//            case ANNUAL_POLICY_TEST:
//                url = API_OAUTH_TOKEN_ANNUAL;
//                break;
//            case KUMANO_TOWN:
//                url = API_OAUTH_TOKEN_KUMANO;
//                break;
//        }
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Authorization", "Basic "+credential)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return new JSONObject(response.body().string()).getString("access_token");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 画面を切り替え
     */
    private void changeUI() {
        //TODO: 処理を追加
    }
}
