package com.example.studycard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

import com.example.studycard.objects.CalendarPunkt;
import com.example.studycard.objects.Chapter;
import com.example.studycard.objects.Cours;
import com.example.studycard.objects.CustomCalendar;

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
    public ArrayList<CalendarPunkt> CalendarPunkts= new ArrayList<>();
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

        // Получаем экземпляр SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        // Читаем данные
        String username = sharedPref.getString("login", "defaultName");
        id = sharedPref.getString("id", "0");
        getUserLessons(id);
        jsonParse();

        ArrayList<CustomCalendar> calendars = null;
        try {
            calendars = CustomCalendar.createCalendar(CalendarPunkts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        //Toast.makeText(this, calendars.get(0).calendarPunkts.get(0).name+" ", Toast.LENGTH_SHORT).show();
        listCreater(calendars);




    }
    public void getUserLessons(String id)
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
    public void jsonParse()
    {
        CalendarPunkts.clear();
        if(message!=null)
        {
            try {
                JSONArray jsonArray = new JSONArray(message);
                for(int i=0;i<jsonArray.length();i++)
                {
                    CalendarPunkt calendarPunkt = new CalendarPunkt();
                    calendarPunkt.name = jsonArray.getJSONObject(i).getString("topic_name");
                    calendarPunkt.date_next = jsonArray.getJSONObject(i).getString("date_next");
                    calendarPunkt.date = jsonArray.getJSONObject(i).getString("date");
                    calendarPunkt.days = jsonArray.getJSONObject(i).getInt("days");
                    calendarPunkt.period =jsonArray.getJSONObject(i).getInt("period");
                    calendarPunkt.topic_id = jsonArray.getJSONObject(i).getString("topic");
                    calendarPunkt.cours = jsonArray.getJSONObject(i).getString("cours_name");

                    CalendarPunkts.add(calendarPunkt);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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