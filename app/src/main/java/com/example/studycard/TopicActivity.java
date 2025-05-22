package com.example.studycard;

import static android.widget.Toast.makeText;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.example.studycard.adapters.CalendarAdapter;
import com.example.studycard.adapters.CardAdapter;
import com.example.studycard.adapters.CardMenuAdapter;
import com.example.studycard.adapters.ChapterAdapter;
import com.example.studycard.adapters.CoursMenuAdapter;
import com.example.studycard.adapters.MenuAdapter;
import com.example.studycard.adapters.TryAdapter;
import com.example.studycard.objects.Card;
import com.example.studycard.objects.Chapter;
import com.example.studycard.objects.CustomCalendar;
import com.example.studycard.objects.PunktMenu;
import com.example.studycard.objects.Topic;
import com.example.studycard.objects.Try;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TopicActivity extends AppCompatActivity {

    public String message;
    ArrayList<Card> cards = new ArrayList<Card>();
    ArrayList<Card> cardsTopic = new ArrayList<Card>();
    ArrayList<Card> cardsTheory = new ArrayList<Card>();
    ArrayList<Card> cardsPractic = new ArrayList<Card>();
    LineChart lineChart;
    String topic_id;
    String cours_id;
    String user_id;
    int period;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_topic);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            Bundle arguments = getIntent().getExtras();
            String name = arguments.getString("name");
            String id = arguments.getString("id");
            cours_id = arguments.getString("cours_id");
            topic_id=id;

            // Получаем экземпляр SharedPreferences
            SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
            // Читаем данные
            user_id = sharedPref.getString("id", "0");

            createMenu();

            cards.clear();
            cardsTopic.clear();
            getData(id);
            jsonParse();
            cardListCreater(cards);


            lineChart = findViewById(R.id.lineChart);
            lineChart.setVisibility(View.GONE);
            TextView nameView = findViewById(R.id.name);
            nameView.setText(name);

            return insets;
        });

    }
    public void startTest()
    {

        if(!Objects.equals(user_id, "0")) {
            addLesson();
        }
        else
        {
            period=0;
        }
        ArrayList<Card> checkCards= Card.CheckPeriod(cards, period);


        Intent intent = new Intent(this, Test.class);
        intent.putExtra("cards",checkCards);
        intent.putExtra("topic_id",topic_id);
        startActivity(intent);

    }
    public void createMenu()
    {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        List<PunktMenu> punktMenus = new ArrayList<>();

        PunktMenu punktMenu = new PunktMenu();
        punktMenu.name="Тест";
        punktMenu.picture=R.drawable.test;
        punktMenus.add(punktMenu);

        punktMenu = new PunktMenu();
        punktMenu.name="Вопросы";
        punktMenu.picture=R.drawable.questions;
        punktMenus.add(punktMenu);

        if(!Objects.equals(user_id, "0")) {

            punktMenu = new PunktMenu();
            punktMenu.name = "Результат";
            punktMenu.picture = R.drawable.result;
            punktMenus.add(punktMenu);
        }

        punktMenu = new PunktMenu();
        punktMenu.name="Теория";
        punktMenu.picture=R.drawable.theory;
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
                    startTest();
                    break;
                case 1:
                    lineChart.setVisibility(View.GONE);
                    cardListCreater(cards);
                    break;
                case 2:
                    lineChart.setVisibility(View.VISIBLE);
                    getProgress();
                    break;
                case 3:
                    lineChart.setVisibility(View.GONE);
                    cardListCreater(cardsTheory);
                    break;

            }

        });
    }
    public void getData(String id)
    {


        message=null;
//----------------------------------------------------

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("topic_id", id)
                .build();


        Request request = new Request.Builder()
                .url("https://studycard.ru/index_android.php?action=get_topic")
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
    public void addLesson()
    {


        message=null;
//----------------------------------------------------

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("topic_id", topic_id)
                .add("user_id", user_id)
                .add("cours_id", cours_id)
                .build();


        Request request = new Request.Builder()
                .url("https://studycard.ru/index_android.php?action=add_lesson")
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
        period = Integer.parseInt(message);
    }
    public void jsonParse()
    {
        if(message!=null)
        {
            try {
                JSONObject jsonObject = new JSONObject(message);
                JSONArray jsonArray=jsonObject.getJSONArray("cards");

                for(int i=0;i<jsonArray.length();i++)
                {
                    Card card = new Card();
                    card.name = jsonArray.getJSONObject(i).getString("name");
                    card.id = jsonArray.getJSONObject(i).getInt("id");
                    card.content = jsonArray.getJSONObject(i).getString("markdown_mark");
                    card.visible = jsonArray.getJSONObject(i).getInt("visible");
                    for(int j=0;j<8; j++)
                    {
                        card.period[j] = jsonArray.getJSONObject(i).getInt("period"+j);
                    }


                    //makeText(this, "visible "+card.visible, Toast.LENGTH_SHORT).show();
                    if (card.visible==0)
                        cardsTheory.add(card);

                    if (card.visible==0 || card.visible==2)
                        cardsTopic.add(card);
                    if (card.visible==1 || card.visible==2)
                        cards.add(card);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    public void chart(ArrayList<Try> tries)
    {


        // 1. Подготовка данных (Entry = X, Y)
        List<Entry> entries = new ArrayList<>();
        int i=0;
        for (Try _try : tries)
        {

            entries.add(new Entry(i, _try.gradeF));
            i++;
        }



        // 2. Создание DataSet и настройка внешнего вида
        LineDataSet dataSet = new LineDataSet(entries, "Прогесс");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Плавные линии
        dataSet.setCubicIntensity(0.2f); // Умеренная кривизна
        dataSet.setColor(Color.parseColor("#6A8CAF")); // Мягкий синий
        dataSet.setCircleColor(Color.parseColor("#FF6B6B")); // Коралловый
        dataSet.setLineWidth(3f); // Толщина линии
        dataSet.setCircleRadius(4f); // Размер точек
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.parseColor("#333333")); // Тёмно-серый текст

        // Заполнение области под линией (опционально)
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#E6EFF7")); // Светло-голубой
        dataSet.setFillAlpha(100); // Прозрачность заливки

        // Настройка графика
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        //lineChart.getDescription().setText("Динамика продаж 2024");
        lineChart.getDescription().setTextColor(Color.parseColor("#666666"));
        lineChart.setBackgroundColor(Color.WHITE); // Белый фон

        // Оси
        lineChart.getXAxis().setTextColor(Color.parseColor("#666666"));
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#666666"));
        lineChart.getAxisRight().setEnabled(false); // Отключаем правую ось

        // Анимация
        lineChart.animateY(500); // Плавное появление


    }
    public void getProgress()
    {
        Try.getTryes(user_id,topic_id);
        ArrayList<Try> tries = Try.jsonParse(Try.message);
        //makeText(this, Try.message, Toast.LENGTH_SHORT).show();

        chart(tries);
        listTriesCreater(tries);


    }
    public void listTriesCreater(ArrayList<Try> tries)
    {


        RecyclerView cardsList = findViewById(R.id.cardsList);

        // Установите адаптер для отображения данных
        TryAdapter tryAdapter = new TryAdapter(this, tries);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cardsList.setLayoutManager(layoutManager);

        // Устанавливаем адаптер
        cardsList.setAdapter(tryAdapter);
        tryAdapter.setOnItemClickListener(position -> {




                /*Toast.makeText(getApplicationContext(), "Был выбран пункт " + selectedCours.picture,
                        Toast.LENGTH_SHORT).show();*/


        });
    }
    public void cardListCreater(ArrayList<Card> cardsTopic)
    {
        RecyclerView cardsList = findViewById(R.id.cardsList);

        // Установите адаптер для отображения данных
        CardMenuAdapter cardMenuAdapter = new CardMenuAdapter(this, cardsTopic);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cardsList.setLayoutManager(layoutManager);

        // Устанавливаем адаптер
        cardsList.setAdapter(cardMenuAdapter);
        cardMenuAdapter.setOnItemClickListener(position -> {




                /*Toast.makeText(getApplicationContext(), "Был выбран пункт " + selectedCours.picture,
                        Toast.LENGTH_SHORT).show();*/


        });

    }

}