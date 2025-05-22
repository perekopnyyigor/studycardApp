package com.example.studycard;

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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RegistrActivity extends AppCompatActivity {
    public static String message;
    public static TextView nameView;
    public static TextView pass1View;
    public static TextView pass2View;
    public SharedPreferences sharedPreferences;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registr);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//главная картинка
        ImageView mainPicture = findViewById(R.id.picture);

        Picasso.get().load("https://studycard.ru/image/on-a-table-with-copy-space.webp").into(mainPicture);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        nameView = findViewById(R.id.login);
        pass1View = findViewById(R.id.password1);
        pass2View = findViewById(R.id.password2);

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

                    Intent intent = new Intent(RegistrActivity.this, MainActivity.class);
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
        String pass1 = pass1View.getText().toString();
        String pass2 = pass2View.getText().toString();
        getData(name,pass1,pass2);

        if(message!=null)
        {

            try {
                JSONObject jsonObject = new JSONObject(message);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("login",jsonObject.getString("login"));
                editor.putString("id",jsonObject.getString("id"));
                editor.apply();
                //Toast.makeText(this, jsonObject.getString("id"), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(RegistrActivity.this, MainActivity.class);
                startActivity(intent);



            } catch (JSONException e) {


                String newMessage = selectMessage(message);
                Toast.makeText(this, newMessage, Toast.LENGTH_SHORT).show();



                e.printStackTrace();
            }
        }



    }
    public String selectMessage(String code)
    {
        String message="";
        switch (code)
        {
            case "0":
                message="Пароли не совпадают";
                break;
            case "1":
                message="Заполните все поля";
                break;
            case "2":
                message="Имя пользователя занято";
                break;
        }
        return message;
    }
    public void getData(String name, String password1, String password2)
    {

        message=null;
//----------------------------------------------------

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("name", name)
                .add("password1", password1)
                .add("password2", password2)
                .build();


        Request request = new Request.Builder()
                .url("https://studycard.ru/index_android.php?action=reg")
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
    public void getDataAut(String emale, String name) {

        message = null;
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
                    message = responseBody.string();

                }
            }
        });
        while (message == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}