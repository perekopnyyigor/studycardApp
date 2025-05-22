package com.example.studycard;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studycard.adapters.ChapterAdapter;
import com.example.studycard.adapters.CoursAdapter;
import com.example.studycard.objects.CRM;
import com.example.studycard.objects.Chapter;
import com.example.studycard.objects.Cours;
import com.example.studycard.objects.Lesson;
import com.example.studycard.objects.Topic;
import com.example.studycard.objects.User;
import com.squareup.picasso.Picasso;

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

public class CoursActivity extends AppCompatActivity {
    public static String message;
    public static ArrayList<Chapter> chapters = new ArrayList<Chapter>();
    ListView chapterList;
    String cours_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cours);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            if(User.id.equals("0"))
            {
                User.getUser(this);
                CRM.userEvent(User.noId, CRM.openCours);
            }

            Bundle arguments = getIntent().getExtras();
            String name = arguments.getString("name");
            cours_id = arguments.getString("id");
            String picture = arguments.getString("picture");

            TextView nameView = findViewById(R.id.name);
            ImageView imageView = findViewById(R.id.picture);

            Picasso.get().load(picture).into(imageView);
            nameView.setText(name);

            chapters.clear();
            getData(cours_id);
            jsonParse();
            listCreater();




            return insets;
        });
    }

    public void getData(String id)
    {

        makeText(getApplicationContext(), id,
                Toast.LENGTH_SHORT).show();
        message=null;
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
    public void jsonParse()
    {
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

    }
    public void listCreater()
    {

        ArrayList<Lesson>  lessons = Lesson.findLessonForCours(User.lessons, cours_id);
        //lessons =User.lessons;
        //makeText(this, User.lessons.get(0).cours_id+"/"+cours_id, Toast.LENGTH_SHORT).show();
        // получаем элемент ListView
        chapterList = findViewById(R.id.cousersList);
        // создаем адаптер
        ChapterAdapter chapterAdapter = new ChapterAdapter(this, R.layout.chapter, chapters, lessons);
        // устанавливаем адаптер
        chapterList.setAdapter(chapterAdapter);
        // слушатель выбора в списке
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // получаем выбранный пункт
                Chapter selectedChapter = (Chapter)parent.getItemAtPosition(position);



                /*makeText(getApplicationContext(), "Был выбран пункт " + selectedChapter.name,
                        Toast.LENGTH_SHORT).show();*/

            }

        };

        chapterList.setOnItemClickListener(itemListener);
    }
}