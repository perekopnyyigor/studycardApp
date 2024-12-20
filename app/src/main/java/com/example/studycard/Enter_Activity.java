package com.example.studycard;

import static android.widget.Toast.makeText;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studycard.objects.Cours;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Enter_Activity extends AppCompatActivity {
    public static String message;
    public static TextView nameView;
    public static TextView passView;
    public SharedPreferences sharedPreferences;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        nameView = findViewById(R.id.login);
        passView = findViewById(R.id.password);
        //главная картинка
        ImageView mainPicture = findViewById(R.id.picture);

        Picasso.get().load("https://studycard.ru/image/on-a-table-with-copy-space.webp").into(mainPicture);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(view -> signIn());
    }
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Авторизация успешна
            String email = account.getEmail();
            String displayName = account.getDisplayName();
            //makeText(this, email, Toast.LENGTH_SHORT).show();
            getDataAut(email, displayName);


            // Обработайте данные пользователя
            if(message!=null)
            {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("login",jsonObject.getString("login"));
                    editor.putString("id",jsonObject.getString("id"));
                    editor.apply();
                    //makeText(this, jsonObject.getString("id"), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Enter_Activity.this, MainActivity.class);
                    startActivity(intent);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (ApiException e) {
            // Обработка ошибок
            e.printStackTrace();
        }
    }
    public void enter(View v)
    {
        String name = nameView.getText().toString();
        String pass = passView.getText().toString();
        getData(name,pass);

        if(message!=null)
        {

            try {
                JSONObject jsonObject = new JSONObject(message);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("login",jsonObject.getString("login"));
                editor.putString("id",jsonObject.getString("id"));
                editor.apply();
                //makeText(this, jsonObject.getString("id"), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Enter_Activity.this, MainActivity.class);
                startActivity(intent);



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
    public void getData(String name, String password)
    {

        message=null;
//----------------------------------------------------

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("name", name)
                .add("password", password)
                .build();


        Request request = new Request.Builder()
                .url("https://studycard.ru/index_android.php?action=enter_action")
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
    public void getDataAut(String emale, String name)
    {

        message=null;
//----------------------------------------------------

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("emale", emale)
                .add("name", name)
                .build();


        Request request = new Request.Builder()
                .url("https://studycard.ru/index_android.php?action=google")
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