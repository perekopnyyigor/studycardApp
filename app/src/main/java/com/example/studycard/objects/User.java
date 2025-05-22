package com.example.studycard.objects;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences;

import java.util.ArrayList;


public class User {
    public static SharedPreferences sharedPref;
    public static String id;
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
}
