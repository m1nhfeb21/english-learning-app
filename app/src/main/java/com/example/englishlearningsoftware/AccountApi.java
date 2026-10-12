package com.example.englishlearningsoftware;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/** Makes network requests with Java callbacks; the Activity updates views with runOnUiThread. */
public class AccountApi {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final HttpUrl baseUrl;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .build();

    public interface ResultCallback {
        void onResult(int code, String body);
        void onFailure();
    }

    public AccountApi(String baseUrl) {
        this.baseUrl = HttpUrl.get(baseUrl);
    }

    // Preserve the original API contract. Never log login URLs or passwords.
    Request loginRequest(String loginInput, String password) {
        boolean isEmail = loginInput.contains("@");
        HttpUrl url = baseUrl.newBuilder()
                .addPathSegments("api/accounts/login")
                .addQueryParameter("email", isEmail ? loginInput : "")
                .addQueryParameter("phoneNumber", isEmail ? "" : loginInput)
                .addQueryParameter("password", password)
                .build();
        return new Request.Builder().url(url).get().build();
    }

    Request registerRequest(String json) {
        HttpUrl url = baseUrl.newBuilder().addPathSegments("api/accounts/register").build();
        return new Request.Builder().url(url).post(RequestBody.create(json, JSON)).build();
    }

    public Call login(String loginInput, String password, ResultCallback callback) {
        return enqueue(loginRequest(loginInput, password), callback);
    }

    public Call register(String json, ResultCallback callback) {
        return enqueue(registerRequest(json), callback);
    }

    private Call enqueue(Request request, final ResultCallback callback) {
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException error) {
                if (!call.isCanceled()) {
                    callback.onFailure();
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                // Close the response even if reading its body fails.
                try (Response result = response) {
                    ResponseBody body = result.body();
                    String text = body == null ? "" : body.string();
                    if (!call.isCanceled()) {
                        callback.onResult(result.code(), text);
                    }
                } catch (IOException error) {
                    if (!call.isCanceled()) {
                        callback.onFailure();
                    }
                }
            }
        });
        return call;
    }
}
