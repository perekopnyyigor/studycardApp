package com.example.studycard.objects;

import org.json.JSONArray;
import org.json.JSONException;

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

public class Try {
    public static String message;
    public String date;
    public String grade;
    public float gradeF;
    public static void getTryes(String user, String topic)
    {

        message=null;
//----------------------------------------------------

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("user", user)
                .add("topic", topic)
                .build();


        Request request = new Request.Builder()
                .url("https://studycard.ru/index_android.php?action=get_tryes")
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
                    message=responseBody.string();

                }
            }
        });
        while (message ==null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static ArrayList<Try> jsonParse(String message)
    {
        ArrayList<Try> tries = new ArrayList<>();

        if(message!=null)
        {
            try {
                JSONArray jsonArray = new JSONArray(message);
                for(int i=0;i<jsonArray.length();i++)
                {
                    Try _try = new Try();
                    _try.grade = jsonArray.getJSONObject(i).getString("degree");
                    _try.gradeF = Float.parseFloat(_try.grade);
                    String dateTemp = jsonArray.getJSONObject(i).getString("dat");
                    String[] words = dateTemp.split(" ");
                    String[] dats = words[0].split("-");
                    _try.date =dats[2]+"."+dats[1]+"."+dats[0];

                    tries.add(_try);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tries;
    }
}
