package com.example.studycard;

import static android.widget.Toast.makeText;
import android.app.AlertDialog;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
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
import com.example.studycard.adapters.CoursMenuAdapter;
import com.example.studycard.adapters.MenuAdapter;
import com.example.studycard.functional.Server;
import com.example.studycard.objects.CRM;
import com.example.studycard.objects.Cours;
import com.example.studycard.objects.PunktMenu;
import com.example.studycard.objects.User;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
    SharedPreferences sharedPref;
    //public static String ;
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
        mainPicture.setImageResource(R.drawable.logo);
        //Picasso.get().load("https://studycard.ru/image/on-a-table-with-copy-space.webp").into(mainPicture);
        //запускаем функции
        User.getUser(this);

        checkFirstLaunch();
        TextView textView = findViewById(R.id.login);
        if (!User.id.equals("0"))
        {
            textView.setText("Добро пожаловать "+ User.name+ "!!!");
            createMenuAuto();
        }
        else
        {
            textView.setText("Выберите курс");
            createMenu();
        }

    Cours.getAllCourses(callback);

    }
    private void checkFirstLaunch() {
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean("is_first_launch", true);

        if (isFirstLaunch) {


            Random random = new Random();
            int randomId = random.nextInt(999999);

            // Сохранение ID и отметки о первом запуске
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("is_first_launch", false);
            editor.putString("noId",String.valueOf(randomId));
            editor.apply();



            CRM.userEvent(String.valueOf(randomId), CRM.firstTime);
           // makeText(this, CRM.message, Toast.LENGTH_LONG).show();
        }
    } // Проверка на первый вход

    Server.DataCallback callback = new Server.DataCallback() {
        @Override
        public void onDataReceived(String data) {
            runOnUiThread(() -> {
                        courses=Cours.jsonParse(data);
                        coursListCreater(courses);
            }
            );
        }
        @Override
        public void onError(String errorMessage) {
            runOnUiThread(() ->
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show()
            );
        }
    };

    public void createMenuAuto()//Меню для авторизированного пользователя
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

                    Cours.getAllCourses(callback);
                    break;
                case 1:
                    Cours.getUserCourses(User.id,callback);


                    break;
                case 2:
                    Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                    startActivity(intent);
                    break;
                case 3:
                    sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("login","defaultName");
                    editor.putString("id","0");
                    editor.apply();

                    intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;

            }

        });
    }
    public void createMenu()//Меню для неавторизированного пользователя
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
                case 2:
                    intent = new Intent(MainActivity.this, RegistrActivity.class);
                    startActivity(intent);
                    break;
            }

    });
    }



    public void coursListCreater(ArrayList<Cours> courses) //Меню курсов
    {
        RecyclerView coursesList = findViewById(R.id.recyclerCousers);

        // Установите адаптер для отображения данных
        CoursMenuAdapter coursMenuAdapter = new CoursMenuAdapter( courses);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        coursesList.setLayoutManager(layoutManager);

        // Устанавливаем адаптер
        coursesList.setAdapter(coursMenuAdapter);
        coursMenuAdapter.setOnItemClickListener(position -> {

            if(User.id.equals("0"))
            {
                User.getUser(this);
                CRM.userEvent(User.noId, CRM.openCours);
            }

            Intent intent = new Intent(MainActivity.this, CoursActivity.class);

            intent.putExtra("name", courses.get(position).name);
            intent.putExtra("id", String.valueOf(courses.get(position).id));
            intent.putExtra("picture", courses.get(position).picture);
            startActivity(intent);


                /*Toast.makeText(getApplicationContext(), "Был выбран пункт " + selectedCours.picture,
                        Toast.LENGTH_SHORT).show();*/


        });
    }


}