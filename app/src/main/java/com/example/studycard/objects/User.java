package com.example.studycard.objects;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences;

import com.example.studycard.functional.Server;

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

    }

    public static ArrayList<Lesson> getUserLessons(String id)
    {
/*
        ArrayList<Lesson> lessons = new ArrayList<Lesson>();

        Lesson.getUserLessons(id);
        lessons = Lesson.jsonParse();*/

        return lessons;
    }

    public static void getUserId(Context context)
    {

        sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        name = sharedPref.getString("login", "defaultName");
        id = sharedPref.getString("id", "0");


    }
    public static void getCommercial(String cours, String user, Server.DataCallback callback)
    {

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("user_id", user)
                .add("cours_id", cours)
                .build();


        Server.Request(requestBody, callback, "https://studycard.ru/index_android.php?action=getCommercial");



    }
}
