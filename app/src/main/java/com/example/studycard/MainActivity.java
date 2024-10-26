package com.example.studycard;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studycard.adapters.CoursAdapter;
import com.example.studycard.objects.Cours;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    public static String message;
    ArrayList<Cours> courses =new ArrayList<>();
    ListView coursesList;
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
    }

    public void sendMessage(View view)
    {
        getData();
        jsonParse();
        listCreater();



    }
    public void jsonParse()
    {
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
                    cours.picture = "https://studycard.ru"+jsonArray.getJSONObject(i).getString("picture");
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
                Toast.makeText(getApplicationContext(), "Был выбран пункт " + selectedCours.picture,
                        Toast.LENGTH_SHORT).show();
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

}