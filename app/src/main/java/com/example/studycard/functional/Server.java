package com.example.studycard.functional;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Server {
    public interface DataCallback {
        void onDataReceived(String data);  // Успешный ответ
        void onError(String errorMessage); // Ошибка
    }

    public static void Request(RequestBody requestBody, DataCallback callback, String url)
    {


        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        call(callback, request);

    }

    public static void Request( DataCallback callback, String url)
    {




        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        call(callback, request);


    }
    public static void call(DataCallback callback, okhttp3.Request request)
    {
        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Передаем ошибку в колбэк
                callback.onError("Ошибка сети: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        callback.onError("Ошибка сервера: " + response.code());
                        return;
                    }
                    String data = responseBody.string();
                    callback.onDataReceived(data); // Передаем данные
                }
            }
        });
    }
}
