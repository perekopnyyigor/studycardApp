package com.example.studycard.objects;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Lesson {
    public static String message;
    public String date;
    public String date_next;
    public String name;
    public int period;
    public int days;
    public String topic_id;
    public String cours;
    public String cours_id;

    public static void getUserLessons(String id)
    {

        message=null;
//----------------------------------------------------

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)

                .build();


        Request request = new Request.Builder()
                .url("https://studycard.ru/index_android.php?action=calendar")
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
    public static  ArrayList<Lesson>  findLessonForCours(ArrayList<Lesson> lessons, String cours_id)
    {
        ArrayList<Lesson> lessonsForCours=new ArrayList<>();
        for (Lesson lesson: lessons)
        {
            if (lesson.cours_id.equals(cours_id))
                lessonsForCours.add(lesson);
        }
        return lessonsForCours;
    }

    public static Lesson findLesson(String topic_id, ArrayList<Lesson> lessons)
    {
        for (Lesson lesson: lessons)
        {
            if (Objects.equals(lesson.topic_id, topic_id))
            {
                return lesson;

            }

        }
        return null;
    }
    public static ArrayList<Lesson> jsonParse()
    {
        ArrayList<Lesson> lessons = new ArrayList<>();

        if(Lesson.message!=null)
        {
            try {
                JSONArray jsonArray = new JSONArray(Lesson.message);
                for(int i=0;i<jsonArray.length();i++)
                {
                    Lesson lesson = new Lesson();
                    lesson.name = jsonArray.getJSONObject(i).getString("topic_name");
                    lesson.date_next = jsonArray.getJSONObject(i).getString("date_next");
                    lesson.date = jsonArray.getJSONObject(i).getString("date");
                    lesson.days = jsonArray.getJSONObject(i).getInt("days");
                    lesson.period =jsonArray.getJSONObject(i).getInt("period");
                    lesson.topic_id = jsonArray.getJSONObject(i).getString("topic");
                    lesson.cours = jsonArray.getJSONObject(i).getString("cours_name");
                    lesson.cours_id = jsonArray.getJSONObject(i).getString("cours");
                    lessons.add(lesson);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return lessons;
    }


}
