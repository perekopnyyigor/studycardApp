package com.example.studycard;

import static com.example.studycard.objects.Lesson.jsonParse;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studycard.adapters.CalendarAdapter;
import com.example.studycard.adapters.ChapterAdapter;


import com.example.studycard.objects.Chapter;
import com.example.studycard.objects.Cours;
import com.example.studycard.objects.CustomCalendar;
import com.example.studycard.objects.Lesson;
import com.example.studycard.objects.User;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
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

public class CalendarActivity extends AppCompatActivity {
    public static String message;
    public static String id;
    ListView calendarList;
    //public ArrayList<CalendarPunkt> CalendarPunkts= new ArrayList<>();
    public ArrayList<Lesson> lessons = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView mainPicture = findViewById(R.id.picture);

        Picasso.get().load("https://studycard.ru/image/on-a-table-with-copy-space.webp").into(mainPicture);

        //запускаем функции
        // Получаем экземпляр SharedPreferences
        /*SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        // Читаем данные
        String username = sharedPref.getString("login", "defaultName");
        id = sharedPref.getString("id", "0");
        Lesson.getUserLessons(id);*/
        lessons= User.lessons;

        ArrayList<CustomCalendar> calendars = null;
        try {
            calendars = CustomCalendar.createCalendar(lessons);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        //Toast.makeText(this, calendars.get(0).calendarPunkts.get(0).name+" ", Toast.LENGTH_SHORT).show();
        listCreater(calendars);

    }


    public void listCreater(ArrayList<CustomCalendar> calendars)
    {

        // получаем элемент ListView
        calendarList = findViewById(R.id.calendarList);
        // создаем адаптер
        CalendarAdapter calendarAdapter = new CalendarAdapter(this, R.layout.calendar_punk, calendars);
        // устанавливаем адаптер
        calendarList.setAdapter(calendarAdapter);
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

        calendarList.setOnItemClickListener(itemListener);
    }
}