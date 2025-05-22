package com.example.studycard;

import static android.widget.Toast.makeText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studycard.objects.Card;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EndTestActivity extends AppCompatActivity {
    public static String message;
    public static String user_id;
    public  Button RegButt;
    public  Button CommButt;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_end_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RegButt = findViewById(R.id.reg);
        CommButt = findViewById(R.id.comm);
        RegButt.setVisibility(View.GONE);
        CommButt.setVisibility(View.GONE);
        //главная картинка
        ImageView mainPicture = findViewById(R.id.picture);
        mainPicture.setImageResource(R.drawable.logo);

        //Данные юзера
        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        user_id = sharedPref.getString("id", "0");
        if(!user_id.equals("0"))
        {
            String messageOut;
            try {
                messageOut=createJson();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            getData(messageOut);


            CommButt.setVisibility(View.VISIBLE);
        }

        else
        {
            RegButt.setVisibility(View.VISIBLE);

            message = "Зарегистрируйтесь чтобы составить расписание повторений и отслеживать свой прогресс";
        }

        TextView resultView = findViewById(R.id.result);
        resultView.setText(message);
    }
    public String createJson() throws JSONException {


        //Данные теста
        Bundle arguments = getIntent().getExtras();
        String topic_id = arguments.getString("topic_id");
        String degree = arguments.getString("degree");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id",user_id);
        jsonObject.put("topic_id",topic_id);
        jsonObject.put("greed",degree);


        return jsonObject.toString();
    }
    public void exit(View v)
    {
        Intent intent = new Intent(EndTestActivity.this, MainActivity.class);
        startActivity(intent);
    }
    public void reg(View v)
    {
        Intent intent = new Intent(EndTestActivity.this, RegistrActivity.class);
        startActivity(intent);
    }
    public void comm(View v)
    {
        Intent intent = new Intent(EndTestActivity.this, CommercialActivity.class);
        startActivity(intent);
    }
    public void getData(String messageOut)
    {


        message=null;
//----------------------------------------------------

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("data_json", messageOut )
                .build();


        Request request = new Request.Builder()
                .url("https://studycard.ru/index_android.php?action=add_try")
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