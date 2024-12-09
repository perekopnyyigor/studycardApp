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

import com.example.studycard.adapters.CardAdapter;
import com.example.studycard.adapters.ChapterAdapter;
import com.example.studycard.objects.Card;
import com.example.studycard.objects.Chapter;
import com.example.studycard.objects.Topic;
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

public class TopicActivity extends AppCompatActivity {

    public String message;
    ArrayList<Card> cards = new ArrayList<Card>();
    ListView cardsList;
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


            cards.clear();
            getData(id);
            jsonParse();
            listCreater();

            makeText(getApplicationContext(), cards.get(0).name+" /"+cards.get(1).name,
                    Toast.LENGTH_SHORT).show();

            cardsList = findViewById(R.id.cardsList);
            TextView nameView = findViewById(R.id.name);
            nameView.setText(name);

            return insets;
        });

    }
    public void startTest(View view)
    {
        Intent intent = new Intent(TopicActivity.this, Test.class);
        intent.putExtra("cards",cards);
        startActivity(intent);
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


                    cards.add(card);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    public void listCreater()
    {

        // получаем элемент ListView
        cardsList = findViewById(R.id.cardsList);
        // создаем адаптер
        CardAdapter cardAdapter = new CardAdapter(this, R.layout.card, cards);
        // устанавливаем адаптер
        cardsList.setAdapter(cardAdapter);
        // слушатель выбора в списке

        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // получаем выбранный пункт
                Card selectedCard = (Card) parent.getItemAtPosition(position);



               /* Toast.makeText(TopicActivity.this, "Был выбран пункт " ,
                        Toast.LENGTH_SHORT).show();*/

            }

        };

        cardsList.setOnItemClickListener(itemListener);
    }
}