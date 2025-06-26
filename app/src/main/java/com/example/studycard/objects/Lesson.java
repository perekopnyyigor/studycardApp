package com.example.studycard.objects;

import com.example.studycard.functional.Server;

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


    public static void getUserLessons(String id, Server.DataCallback callback)
    {

        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)
                .build();

        Server.Request(requestBody, callback,"https://studycard.ru/index_android.php?action=calendar");



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
    public static ArrayList<Lesson> jsonParse(String data)
    {
        ArrayList<Lesson> lessons = new ArrayList<>();

        if(data!=null)
        {
            try {
                JSONArray jsonArray = new JSONArray(data);
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
