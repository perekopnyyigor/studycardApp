package com.example.studycard.objects;

import android.widget.Toast;

import com.example.studycard.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static void getAllCourses(DataCallback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://studycard.ru/index_android.php?action=all_chapters")
                .build();

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


    public static void getUserCourses(String id, DataCallback callback)
    {


//----------------------------------------------------

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)
                .build();


        Request request = new Request.Builder()
                .url("https://studycard.ru/index_android.php?action=user_courses")
                .post(requestBody)
                .build();

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

    public static void getChapters(String id)
    {

        //makeText(getApplicationContext(), id,Toast.LENGTH_SHORT).show();
        mess=null;
//----------------------------------------------------

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("cours_id", id)
                .build();


        Request request = new Request.Builder()
                .url("https://studycard.ru/index_android.php?action=open_cours")
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
