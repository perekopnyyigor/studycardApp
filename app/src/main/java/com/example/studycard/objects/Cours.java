package com.example.studycard.objects;


import com.example.studycard.functional.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;


import okhttp3.FormBody;

import okhttp3.RequestBody;


public class Cours {
    public static String mess;
    public int id;
    public String name;
    public String description;
    public String picture;
    ArrayList<Cours> courses =new ArrayList<>();

    public Cours()
    {

    }

    public interface DataCallback {
        void onDataReceived(String data);  // Успешный ответ
        void onError(String errorMessage); // Ошибка
    }

    public static void getAllCourses(Server.DataCallback callback) {

        Server.Request(callback,"https://studycard.ru/index_android.php?action=all_chapters");
    }


    public static void getUserCourses(String id, Server.DataCallback callback)
    {

        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)
                .build();


        Server.Request(requestBody, callback,"https://studycard.ru/index_android.php?action=user_courses");
    }

    public static ArrayList<Cours> jsonParse(String message)
    {
        ArrayList<Cours> courses = new ArrayList<>();
        if(message!=null)
        {
            try {
                JSONArray jsonArray = new JSONArray(message);
                for(int i=0;i<jsonArray.length();i++)
                {
                    Cours cours = new Cours();
                    cours.name = jsonArray.getJSONObject(i).getString("name");
                    cours.id = jsonArray.getJSONObject(i).getInt("id");
                    cours.description = jsonArray.getJSONObject(i).getString("description");
                    cours.picture = "https://studycard.ru"+jsonArray.getJSONObject(i).getString("picture").substring(2);
                    courses.add(cours);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return courses;
    }

    public static void getChapters(String id, Server.DataCallback callback)
    {

        RequestBody requestBody = new FormBody.Builder()
                .add("cours_id", id)
                .build();

        Server.Request(requestBody, callback,"https://studycard.ru/index_android.php?action=open_cours");

    }

    public static ArrayList<Chapter>  jsonParseChapters(String message)
    {
        ArrayList<Chapter> chapters = new ArrayList<Chapter>();
        if(message!=null)
        {
            try {
                JSONObject jsonObject = new JSONObject(message);
                JSONArray jsonArray=jsonObject.getJSONArray("chapters");

                for(int i=0;i<jsonArray.length();i++)
                {
                    Chapter chapter = new Chapter();
                    chapter.name = jsonArray.getJSONObject(i).getString("name");
                    ArrayList<Topic> topics = new ArrayList<Topic>();

                    JSONArray jsontopics = jsonArray.getJSONObject(i).getJSONArray("topics");
                    //String name_topic =jsontopics.getJSONObject(0).getString("name");

                    for (int j=0; j<jsontopics.length();j++)
                    {
                        Topic topic = new Topic();
                        topic.name =jsontopics.getJSONObject(j).getString("name");
                        topic.id =jsontopics.getJSONObject(j).getInt("id");
                        topic.cours_id =jsontopics.getJSONObject(j).getInt("cours");
                        topic.commercial =jsontopics.getJSONObject(j).getString("commercial");
                        topics.add(topic);
                    }
                    chapter.topics=topics;
                    /*makeText(getApplicationContext(), chapter.topics.get(1).name ,
                            Toast.LENGTH_SHORT).show();*/


                    chapters.add(chapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return chapters;
    }
}
