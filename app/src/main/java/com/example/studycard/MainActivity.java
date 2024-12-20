package com.example.studycard;

import static android.widget.Toast.makeText;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studycard.adapters.CoursAdapter;
import com.example.studycard.adapters.MenuAdapter;
import com.example.studycard.objects.Cours;
import com.example.studycard.objects.PunktMenu;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    public static String message;
    ArrayList<Cours> courses =new ArrayList<>();
    ListView coursesList;
    public static String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //главная картинка
        ImageView mainPicture = findViewById(R.id.picture);

        Picasso.get().load("https://studycard.ru/image/on-a-table-with-copy-space.webp").into(mainPicture);
        //запускаем функции

        // Получаем экземпляр SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);

        // Читаем данные
        String username = sharedPref.getString("login", "defaultName");
        id = sharedPref.getString("id", "0");
        TextView textView = findViewById(R.id.login);
        if (!id.equals("0"))
        {
            textView.setText("Добро пожаловать "+ username);
            createMenuAuto();
        }
        else
        {
            createMenu();
        }


        getData();
        jsonParse();
        listCreater();


    }
    public void createMenuAuto()
    {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        List<PunktMenu> punktMenus = new ArrayList<>();

        PunktMenu punktMenu = new PunktMenu();
        punktMenu.name="Главная";
        punktMenu.picture=R.drawable.main;
        punktMenus.add(punktMenu);

        punktMenu = new PunktMenu();
        punktMenu.name="Мои курсы";
        punktMenu.picture=R.drawable.list;
        punktMenus.add(punktMenu);

        punktMenu = new PunktMenu();
        punktMenu.name="Календарь";
        punktMenu.picture=R.drawable.calendar;
        punktMenus.add(punktMenu);

        punktMenu = new PunktMenu();
        punktMenu.name="Прогресс";
        punktMenu.picture=R.drawable.progress;
        punktMenus.add(punktMenu);

        punktMenu = new PunktMenu();
        punktMenu.name="Выход";
        punktMenu.picture=R.drawable.exit;
        punktMenus.add(punktMenu);

// Установите адаптер для отображения данных
        MenuAdapter menuAdapter = new MenuAdapter(punktMenus);

// Устанавливаем LayoutManager для горизонтальной ориентации
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

// Устанавливаем адаптер
        recyclerView.setAdapter(menuAdapter);
        menuAdapter.setOnItemClickListener(position -> {
            switch (position)
            {
                case 0:
                    getData();
                    jsonParse();
                    listCreater();
                    break;
                case 1:
                    getUserCourses(id);
                    //makeText(this, message, Toast.LENGTH_SHORT).show();
                    jsonParse();
                    listCreater();
                    break;
                case 2:
                    Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                    startActivity(intent);
                    break;

            }

        });
    }
    public void createMenu()
    {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        List<PunktMenu> punktMenus = new ArrayList<>();

        PunktMenu punktMenu = new PunktMenu();
        punktMenu.name="Главная";
        punktMenu.picture=R.drawable.main;
        punktMenus.add(punktMenu);

        punktMenu = new PunktMenu();
        punktMenu.name="Вход";
        punktMenu.picture=R.drawable.enter;
        punktMenus.add(punktMenu);

        punktMenu = new PunktMenu();
        punktMenu.name="Регистрация";
        punktMenu.picture=R.drawable.registr;
        punktMenus.add(punktMenu);

// Установите адаптер для отображения данных
        MenuAdapter menuAdapter = new MenuAdapter(punktMenus);

// Устанавливаем LayoutManager для горизонтальной ориентации
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

// Устанавливаем адаптер
        recyclerView.setAdapter(menuAdapter);
        menuAdapter.setOnItemClickListener(position -> {
            switch (position)
            {
                case 0:

                    break;
                case 1:
                    Intent intent = new Intent(MainActivity.this, Enter_Activity.class);
                    startActivity(intent);
                    break;
            }

    });
    }
    public void jsonParse()
    {
        courses.clear();
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

    }

    public void listCreater()
    {

        // получаем элемент ListView
        coursesList = findViewById(R.id.cousersList);
        // создаем адаптер
        CoursAdapter coursAdapter = new CoursAdapter(this, R.layout.cours, courses);
        // устанавливаем адаптер
        coursesList.setAdapter(coursAdapter);
        // слушатель выбора в списке
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // получаем выбранный пункт
                Cours selectedCours = (Cours)parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, CoursActivity.class);

                intent.putExtra("name", courses.get(position).name);
                intent.putExtra("id", String.valueOf(courses.get(position).id));
                intent.putExtra("picture", courses.get(position).picture);
                startActivity(intent);


                /*Toast.makeText(getApplicationContext(), "Был выбран пункт " + selectedCours.picture,
                        Toast.LENGTH_SHORT).show();*/

            }

        };

        coursesList.setOnItemClickListener(itemListener);
    }

    public void getData()
    {


        message=null;
//----------------------------------------------------

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://studycard.ru/index_android.php?action=all_chapters")
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
    public void getUserCourses(String id)
    {

        message=null;
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

}