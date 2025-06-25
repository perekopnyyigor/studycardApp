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

    String access ="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cours);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);



            Bundle arguments = getIntent().getExtras();
            String name = arguments.getString("name");
            cours_id = arguments.getString("id");
            String picture = arguments.getString("picture");



            TextView nameView = findViewById(R.id.name);
            ImageView imageView = findViewById(R.id.picture);

            Picasso.get().load(picture).into(imageView);
            nameView.setText(name);

            chapters.clear();


            Cours.getChapters(cours_id);
            chapters = Cours.jsonParseChapters(Cours.mess);

            listCreater();


            User.getUserId(this);
            if(!User.id.equals("0"))
            {
                User.getCommercial(cours_id,User.id);
                access=User.mess;
               // makeText(this, User.mess, Toast.LENGTH_SHORT).show();
            }


            return insets;
        });
    }



    public void listCreater()
    {

        ArrayList<Lesson>  lessons = Lesson.findLessonForCours(User.lessons, cours_id);
        //lessons =User.lessons;
        //makeText(this, User.lessons.get(0).cours_id+"/"+cours_id, Toast.LENGTH_SHORT).show();
        // получаем элемент ListView
        chapterList = findViewById(R.id.cousersList);
        // создаем адаптер
        ChapterAdapter chapterAdapter = new ChapterAdapter(this, R.layout.chapter, chapters, lessons, access);
        // устанавливаем адаптер
        chapterList.setAdapter(chapterAdapter);
        // слушатель выбора в списке
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // получаем выбранный пункт
                Chapter selectedChapter = (Chapter)parent.getItemAtPosition(position);


            }

        };

        chapterList.setOnItemClickListener(itemListener);
    }
}