package com.example.studycard.objects;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class User {
    public static SharedPreferences sharedPref;
    public static String id;
    public static String mess;
    public static String noId;
    public static String name;
    public static ArrayList<Lesson> lessons;

    public static void getUser(Context context)
    {

        sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        name = sharedPref.getString("login", "defaultName");
        id = sharedPref.getString("id", "0");
        noId = sharedPref.getString("noId", "0");
        Lesson.getUserLessons(id);
        lessons = Lesson.jsonParse();
    }
    public static void getUserId(Context context)
    {

        sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        name = sharedPref.getString("login", "defaultName");
        id = sharedPref.getString("id", "0");


    }
    public static void getCommercial(String cours, String user)
    {


        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("user_id", user)
                .add("cours_id", cours)
                .build();


        Request request = new Request.Builder()
                .url("https://studycard.ru/index_android.php?action=getCommercial")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Запрос к серверу не был успешен: " +
                                response.code() + " " + response.message());

                    }

                    // пример получения всех заголовков ответа
                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        // вывод заголовков
                        System.out.println(responseHeaders.name(i) + ": "
                                + responseHeaders.value(i));
                    }
                    // вывод тела ответа
                    mess=responseBody.string();

                }
            }
        });
        while (mess ==null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
