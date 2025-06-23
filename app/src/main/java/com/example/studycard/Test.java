package com.example.studycard;

import static android.widget.Toast.makeText;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.Target;
import com.example.studycard.Prism4j.MyGrammarLocator;
import com.example.studycard.adapters.VariantAdapter;
import com.example.studycard.objects.Card;
import com.example.studycard.objects.Variant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.syntax.Prism4jThemeDefault;
import io.noties.markwon.syntax.SyntaxHighlightPlugin;
import io.noties.prism4j.Prism4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Test extends AppCompatActivity {
    public static int count=0;
    public static int trueAnswer=0;
    public static int wrongAnswer=0;
    public int count_variant=0;
    public int count_variant_n=0;
    public int quantity_variant=0;
    public static String topic_id;
    public static String cours_id;
    public static String message;
    ArrayList<Card> cards = new ArrayList<Card>();
    ProgressBar degreeBar;
    TextView contentView;
    String content;
    Button nextButton;
    EditText write;
    Button check;
    RecyclerView variantList;
    TextView degreeView;
    String[] mainVariants;
    TextView Review;
    EditText writeReview;
    Button addReview;
    Button openReview;
    TextView nameView;
    int start_pos=0;
    int end_pos=0;
    int degreeOld;
    int degree = 100;
    String last_answer;
    private VariantAdapter variantAdapter;
    //final Markwon markwon=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            //count_variant=0;
            Bundle arguments = getIntent().getExtras();
            cards = (ArrayList<Card>) arguments.getSerializable("cards");
            topic_id = arguments.getString("topic_id");
            cours_id = arguments.getString("cours_id");

            variantList = findViewById(R.id.recyclerView);
            //Счетчик
            TextView countView = findViewById(R.id.count);
            degreeBar = findViewById(R.id.degreeBar);
            degreeBar.setMax(100);
            int lenght = cards.size();
            String countStr = "Вопрос "+(count+1)+" из "+lenght;
            countView.setText(countStr);
            //Название
            String name = cards.get(count).name;
            nameView = findViewById(R.id.name);
            nameView.setText(name);

            //Правильных ответов
            degreeView = findViewById(R.id.degree);
            calculateDegree();

            //Содержание
            content = cards.get(count).content;

            mainVariants = content.split("\\{m\\}");
            contentView = findViewById(R.id.content);
            String main_content = createContent(content,count_variant);
            printMarkdown(main_content);

            makeText(this, "cours id"+cours_id, Toast.LENGTH_SHORT).show();
            //главная картинка

            //Вписывание текста
            write = findViewById(R.id.write);
            check = findViewById(R.id.check);

            if (count_variant==0)
            {


                write.setVisibility(View.GONE);
                check.setVisibility(View.GONE);

                ArrayList<Variant> variants=createVariant(content,count_variant);
                listCreater(variants);
                quantity_variant =variants.size();
                nextButton = findViewById(R.id.next);
                nextButton.setVisibility(View.GONE);
                count_variant_n = countSubstrings(content,"{n}");


            }
            //Review
            Review = findViewById(R.id.Review);
            writeReview = findViewById(R.id.writeReview);
            addReview = findViewById(R.id.addReview);
            openReview = findViewById(R.id.openReview);

            Review.setVisibility(View.GONE);
            writeReview.setVisibility(View.GONE);
            addReview.setVisibility(View.GONE);




            return insets;
        });
    }
    public void openReview(View v)
    {
        variantList.setVisibility(View.GONE);
        contentView.setVisibility(View.GONE);
        openReview.setVisibility(View.GONE);
        nameView.setVisibility(View.GONE);


        Review.setVisibility(View.VISIBLE);
        writeReview.setVisibility(View.VISIBLE);
        addReview.setVisibility(View.VISIBLE);


    }
    public String createJson() throws JSONException {

        //Данные юзера
        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String user_id = sharedPref.getString("id", "0");
        //Карта
        String card_id = String.valueOf( cards.get(count).id);

        String content = writeReview.getText().toString();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id",user_id);
        jsonObject.put("card_id",card_id);
        jsonObject.put("content",content);

        makeText(this, jsonObject.toString(), Toast.LENGTH_SHORT).show();
        return jsonObject.toString();
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
                .url("https://studycard.ru/index_android.php?action=add_error")
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
    public void sendReview(View v)
    {
        variantList.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.VISIBLE);
        openReview.setVisibility(View.VISIBLE);
        nameView.setVisibility(View.VISIBLE);

        String messageOut;
        try {
            messageOut=createJson();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        getData(messageOut);

        Review.setVisibility(View.GONE);
        writeReview.setVisibility(View.GONE);
        addReview.setVisibility(View.GONE);
    }
    public String calculateDegree()
    {

        float degree = (float) trueAnswer /(trueAnswer+wrongAnswer)*100;


        int progressInt= (int)degree;
        degreeBar.setProgress(progressInt);
        degreeView.setText(String.valueOf("Правильных ответов "+progressInt+" %"));

        ObjectAnimator animator = ObjectAnimator.ofInt(degreeBar, "progress", degreeOld, progressInt);
        animator.setDuration(1000); // Длительность 1 секунда
        animator.start();

        if(progressInt >= 95)
        {
            int redColor = ContextCompat.getColor(this, R.color.green);
            degreeBar.setProgressTintList(ColorStateList.valueOf(redColor));
        }
        if(progressInt<95 && progressInt>=90)
        {
            int redColor = ContextCompat.getColor(this, R.color.greenYellow);
            degreeBar.setProgressTintList(ColorStateList.valueOf(redColor));
        }
        if(progressInt<90 && progressInt>=85)
        {
            int redColor = ContextCompat.getColor(this, R.color.yellow);
            degreeBar.setProgressTintList(ColorStateList.valueOf(redColor));
        }
        if(progressInt<85 && progressInt>=80)
        {
            int redColor = ContextCompat.getColor(this, R.color.yellowOrange);
            degreeBar.setProgressTintList(ColorStateList.valueOf(redColor));
        }
        if(progressInt<80 && progressInt>=75)
        {
            int redColor = ContextCompat.getColor(this, R.color.orange);
            degreeBar.setProgressTintList(ColorStateList.valueOf(redColor));
        }
        if(progressInt<75 && progressInt>=70)
        {
            int redColor = ContextCompat.getColor(this, R.color.orangeRed);
            degreeBar.setProgressTintList(ColorStateList.valueOf(redColor));
        }
        if(progressInt<70)
        {
            int redColor = ContextCompat.getColor(this, R.color.red);
            degreeBar.setProgressTintList(ColorStateList.valueOf(redColor));
        }




        degreeOld = (int) degree;
        return String.valueOf(progressInt);
    }
    public String replace(String oldString)
    {
        StringBuilder newString = new StringBuilder(oldString.replace("slash", "\\"));
        newString = new StringBuilder(newString.toString().replace("apostrof", "\'"));
        newString = new StringBuilder(newString.toString().replace("{wr}", ""));
        newString = new StringBuilder(newString.toString().replace("{m}", ""));
        newString = new StringBuilder(newString.toString().replace("{f}", ""));
        if(newString.toString().contains("{t}"))
        {

            String[] group = newString.toString().split("\\{t\\}");
            newString = new StringBuilder(group[0]);
            for (int i=1;i<group.length;i++)
            {
                newString.append(group[i].substring(1));
            }


        }
        return newString.toString();
    }
    public String replaceVariant(String oldString)
    {
        String newString =  oldString.replace("slash", "\\");
        newString = newString.replace("apostrof", "\'");

        return newString;
    }
    //кнопка далее
    public void next(View v)
    {
        if(cards.size()==(count+1))// если последняя карточка
        {
            count_variant=0;
            count_variant_n=0;
            count=0;

            Intent intent = new Intent(Test.this, EndTestActivity.class);
            intent.putExtra("topic_id", topic_id);
            intent.putExtra("degree", calculateDegree());
            intent.putExtra("cours_id",cours_id);
            trueAnswer=0;
            wrongAnswer=0;
            startActivity(intent);
        }
        else// если не последняя карточка
        {
            count_variant=0;
            count_variant_n=0;
            count++;
            Intent intent = new Intent(Test.this, Test.class);
            intent.putExtra("cards",cards);
            intent.putExtra("topic_id",topic_id);
            intent.putExtra("cours_id",cours_id);

            startActivity(intent);
        }
    }
    public static int countSubstrings(String str, String sub) {
        if (str == null || sub == null || sub.isEmpty()) {
            return 0; // Обработка некорректных случаев
        }

        int count = 0;
        int index = 0;

        while ((index = str.indexOf(sub, index)) != -1) {
            count++;
            index += sub.length(); // Смещаем индекс на длину подстроки
        }

        return count;
    }
    //проверить введенный ответ
    public void check(View v)
    {
        ArrayList<Variant> variants=createVariant(content,count_variant);

        String variantStr = write.getText().toString();
        Variant rightVariant =variants.get(0);



        if(Objects.equals(variantStr, rightVariant.content))
        {
            count_variant++;
            trueAnswer++;
            calculateDegree();



            write.setVisibility(View.GONE);
            check.setVisibility(View.GONE);
            variantList.setVisibility(View.VISIBLE);
            if((quantity_variant-count_variant_n)==(count_variant))
            {

                String main_content = createContent(content,count_variant);
                updateContentWithBuffer1(main_content);


                nextButton.setVisibility(View.VISIBLE);
                variantList.setVisibility(View.GONE);




            }
            else {


                //варианты
                listCreater(createVariant(content, count_variant));

                //Содержание
                String main_content = createContent(content, count_variant);
                updateContentWithBuffer1(main_content);
            }



        }
        else
        {
            wrongAnswer++;
            calculateDegree();
        }


    }
    public void updatePartialContent(String newContent, int start, int end) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(contentView.getText());
        spannable.replace(start, end, newContent); // Здесь метод replace работает
        contentView.setText(spannable);

    }

    public void updateContentWithAnimation(String newContent) {
        contentView.animate().alpha(0).setDuration(100).withEndAction(() -> {
            printMarkdown(newContent); // Обновляем содержимое
            contentView.animate().alpha(1).setDuration(700).start();
        }).start();
    }


    public void printMarkdown(String main_content)
    {
        int TableHead = ContextCompat.getColor(this, R.color.TableHead);
        int TableOdd = ContextCompat.getColor(this, R.color.TableOdd);
        main_content = replace(main_content);
        final Prism4j prism4j = new Prism4j(new MyGrammarLocator());
        final Markwon markwon = Markwon.builder(Test.this)

                .usePlugin(GlideImagesPlugin.create(new GlideImagesPlugin.GlideStore() {
                    @NonNull
                    @Override
                    public RequestBuilder<Drawable> load(@NonNull AsyncDrawable drawable) {
                        DisplayMetrics displayMetrics = Test.this.getResources().getDisplayMetrics();
                        int screenWidth = displayMetrics.widthPixels;
                        int targetWidth = screenWidth / 2; // Устанавливаем половину ширины экрана
                        int targetHeight = targetWidth; // Пропорциональная высота (например, квадрат)

                        // Загрузка изображения через Glide с настройкой размера
                        return Glide.with(Test.this)
                                .load(drawable.getDestination())
                                .override(targetWidth, targetHeight) // Указываем размеры изображения
                                .fitCenter(); // Настройка масштабирования (или .centerCrop() для обрезки)
                    }

                    @Override
                    public void cancel(@NonNull Target<?> target) {
                        Glide.with(Test.this).clear(target);
                    }
                }))
                .usePlugin(MarkwonInlineParserPlugin.create()) // Inline парсер
                .usePlugin(SyntaxHighlightPlugin.create(prism4j, Prism4jThemeDefault.create())) // Подсветка синтаксиса
                .usePlugin(TablePlugin.create(builder ->
                        builder
                                .tableBorderColor(Color.RED)
                                .tableBorderWidth(0)
                                .tableCellPadding(0)

                                .tableHeaderRowBackgroundColor(TableHead)
                                .tableEvenRowBackgroundColor(Color.WHITE)
                                .tableOddRowBackgroundColor(TableOdd)
                                .build()
                ))
                .usePlugin(JLatexMathPlugin.create(contentView.getTextSize(), new JLatexMathPlugin.BuilderConfigure() {
                    @Override
                    public void configureBuilder(@NonNull JLatexMathPlugin.Builder builder) {
                        // ENABLE inlines
                        builder.inlinesEnabled(true);
                    }
                }))
                .build();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //markwon.setMarkdown(contentView, replace(main_content));

        // Рендеринг в буфер
        markwon.setMarkdown(contentView, replace(main_content));

        // Получаем Spannable из буфера
        CharSequence renderedText = contentView.getText();



        contentView.post(() -> {
            contentView.setText(renderedText);
            contentView.invalidate();
            contentView.requestLayout();
        });

    }
//создать варианты
    public ArrayList<Variant> createVariant(String content,int start)
    {

        ArrayList<Variant> variants = new ArrayList<>();
        String[] arr = mainVariants;// content.split("\\{m\\}");

        for (int i=start*2;i<arr.length;i++)
        {
            if (i%2==1)
            {
                Variant variant = new Variant();
                variant.content = replaceVariant(arr[i]);

                if (arr[i].contains("{wr}"))
                {
                    variant.type=2;
                    variant.content=variant.content.replace("{wr}", "");
                }
                else if(arr[i].contains("{f}"))
                {
                    variant.type=3;
                    variant.content=variant.content.replace("{f}", "");
                }
                else if(arr[i].contains("{n}"))
                {
                    variant.type=4;
                    variant.content=variant.content.replace("{n}", "");
                    //Считаем количество доп вариантов

                }
                else
                {
                    variant.type=1;

                }

                if(arr[i].contains("{t}"))
                {
                    //variant.type=5;
                    String[] group = arr[i].split("\\{t\\}");


                    variant.group=group[1];
                    group[0]=group[0].replace("{f}", "");
                    variant.content = replaceVariant(group[0]);

                }
                else
                {

                    variant.group="0";
                }
                variants.add(variant);
            }
        }

       return variants;
    }
    public  int countCharInString(String text, char target) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == target) {
                count++;
            }
        }
        return count;
    }
    public String createContent(String content, int start)
    {
        String[] arr = mainVariants;// content.split("\\{m\\}");
        String result1="";
        String result2="";

        for (int i=0;i<start*2;i++)
        {
            if(arr[i].contains("{n}"))
            {
                result1 +=" ";
            }
            else
                result1 += arr[i];

        }

        for (int i=start*2;i<arr.length;i++)
        {

            if (i%2==0)
            {
                if(arr[i].contains("{n}"))
                {
                    result2 +=" ";
                }
                else
                    result2 += arr[i];
            }
            else
            {
                if(arr[i].contains("{n}"))
                {
                    result2 +=" ";
                }
                else
                {
                    if(i==start*2+1)
                    {
                        if(arr[i].contains("{f}"))
                            result2 += "\\text{\\textbf{???}}";
                        else
                            result2 += "**???**";
                    }

                    else
                    {
                        if(arr[i].contains("{f}"))
                            result2 += "...";
                        else
                            result2 += "...";
                    }
                }

            }
        }

        return result1+result2;
    }

    public void createPosition(String content, int start)
    {
        String[] arr = content.split("\\{m\\}");
        String result1="";
        String result2="";

        for (int i=0;i<start*2-1;i++)
        {

            result1 += arr[i];

        }
        int move = countCharInString(result1,'$');
        start_pos = result1.length()-move;
        end_pos = start_pos+3;
    }
    public void updateContentWithBuffer(String newContent) {
        newContent = replace(newContent);
        // Буферный TextView для промежуточного рендеринга
        TextView buffer = new TextView(this);
        buffer.setTextSize(contentView.getTextSize());
        int TableHead = ContextCompat.getColor(this, R.color.TableHead);
        int TableOdd = ContextCompat.getColor(this, R.color.TableOdd);
        // Настройка Markwon
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(GlideImagesPlugin.create(new GlideImagesPlugin.GlideStore() {
            @NonNull
            @Override
            public RequestBuilder<Drawable> load(@NonNull AsyncDrawable drawable) {
                DisplayMetrics displayMetrics = Test.this.getResources().getDisplayMetrics();
                int screenWidth = displayMetrics.widthPixels;
                int targetWidth = screenWidth / 2; // Устанавливаем половину ширины экрана
                int targetHeight = targetWidth; // Пропорциональная высота (например, квадрат)

                // Загрузка изображения через Glide с настройкой размера
                return Glide.with(Test.this)
                        .load(drawable.getDestination())
                        .override(targetWidth, targetHeight) // Указываем размеры изображения
                        .fitCenter(); // Настройка масштабирования (или .centerCrop() для обрезки)
            }

            @Override
            public void cancel(@NonNull Target<?> target) {
                Glide.with(Test.this).clear(target);
            }
        }))
                .usePlugin(MarkwonInlineParserPlugin.create()) // Inline Markdown

                .usePlugin(TablePlugin.create(builder ->
                        builder
                                .tableBorderColor(Color.RED)
                                .tableBorderWidth(0)
                                .tableCellPadding(0)

                                .tableHeaderRowBackgroundColor(TableHead)
                                .tableEvenRowBackgroundColor(Color.WHITE)
                                .tableOddRowBackgroundColor(TableOdd)
                                .build()
                ))
                .usePlugin(SyntaxHighlightPlugin.create(
                        new Prism4j(new MyGrammarLocator()),
                        Prism4jThemeDefault.create())) // Подсветка синтаксиса
                .usePlugin(JLatexMathPlugin.create(contentView.getTextSize(), builder -> {
                    builder.inlinesEnabled(true); // Включаем встроенный LaTeX
                }))
                .build();

        // Рендеринг в буфер
        markwon.setMarkdown(buffer, newContent);

        // Получаем Spannable из буфера
        CharSequence renderedText = buffer.getText();

        contentView.post(() -> {
            contentView.setText(renderedText);
            contentView.invalidate();
            contentView.requestLayout();
        });
    }

    public void updateContentWithBuffer1(String newContent) {
        newContent = replace(newContent);

        // Проверяем входное содержимое
        if (newContent == null || newContent.isEmpty()) {
            Log.e("MarkdownError", "Пустое содержимое");
            return;
        }

        // Настраиваем буфер
        TextView buffer = new TextView(this);
        buffer.setTextSize(contentView.getTextSize());
        buffer.setMovementMethod(LinkMovementMethod.getInstance());
        buffer.setTextIsSelectable(true);

        int TableHead;
        int TableOdd;


            TableHead = ContextCompat.getColor(this, R.color.TableHead);
            TableOdd = ContextCompat.getColor(this, R.color.TableOdd);


        // Настройка Markwon
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(GlideImagesPlugin.create(new GlideImagesPlugin.GlideStore() {
                    @NonNull
                    @Override
                    public RequestBuilder<Drawable> load(@NonNull AsyncDrawable drawable) {
                        DisplayMetrics displayMetrics = Test.this.getResources().getDisplayMetrics();
                        int screenWidth = displayMetrics.widthPixels;
                        int targetWidth = screenWidth / 2; // Устанавливаем половину ширины экрана
                        int targetHeight = targetWidth; // Пропорциональная высота (например, квадрат)

                        // Загрузка изображения через Glide с настройкой размера
                        return Glide.with(Test.this)
                                .load(drawable.getDestination())
                                .override(targetWidth, targetHeight) // Указываем размеры изображения
                                .fitCenter(); // Настройка масштабирования (или .centerCrop() для обрезки)
                    }

                    @Override
                    public void cancel(@NonNull Target<?> target) {
                        Glide.with(Test.this).clear(target);
                    }
                }))
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(TablePlugin.create(this))
                .usePlugin(SyntaxHighlightPlugin.create(
                        new Prism4j(new MyGrammarLocator()),
                        Prism4jThemeDefault.create()))
                .usePlugin(JLatexMathPlugin.create(contentView.getTextSize(), new JLatexMathPlugin.BuilderConfigure() {
                    @Override
                    public void configureBuilder(@NonNull JLatexMathPlugin.Builder builder) {
                        // ENABLE inlines
                        builder.inlinesEnabled(true);
                    }
                }))
                .build();

        // Рендеринг Markdown
        try {
            markwon.setMarkdown(buffer, newContent);
        } catch (Exception e) {
            Log.e("MarkdownRenderError", "Ошибка при рендеринге Markdown", e);
            buffer.setText("Ошибка отображения содержимого");
        }

        // Применяем рендеринг
        CharSequence renderedText = buffer.getText();
        contentView.post(() -> {
            contentView.setText(renderedText);
            contentView.invalidate();
            contentView.requestLayout();
        });
    }


    public void changeVariant(String rightVariant, String selectedVariant)
    {
        int indexSelect=0;
        int indexRight=0;
        for (int i = 0; i<mainVariants.length; i++)
        {
            String[] content = mainVariants[i].split("\\{t\\}");

            content[0]=content[0].replace("{f}", "");
            content[0]=replaceVariant(content[0]);
            if(Objects.equals(content[0],rightVariant))
                indexRight=i;
            if(Objects.equals(content[0],selectedVariant))
                indexSelect=i;

        }
        String temp=mainVariants[indexRight];
        mainVariants[indexRight]=mainVariants[indexSelect];
        mainVariants[indexSelect]=temp;
    }



    public void listCreater(ArrayList<Variant> variants) {
        // Получаем RecyclerView
        variantList.setVisibility(View.VISIBLE);
        //variantList.setVisibility(View.VISIBLE);
        if(variants.get(0).type!=2)
        {
            ArrayList<Variant> variantsForList = new ArrayList<>();
            for(int i=0; i<variants.size();i++)
            {
                if(variants.get(i).content.contains("{n}"))
                {

                    variants.get(i).content=variants.get(i).content.replace("{n}", "");


                }

                if (variants.get(i).type!=2)
                    variantsForList.add(variants.get(i));



            }
            Collections.shuffle(variantsForList);
            // Создаем адаптер
            VariantAdapter variantAdapter = new VariantAdapter(this, variantsForList);

            // Устанавливаем LayoutManager для RecyclerView
            variantList.setLayoutManager(new LinearLayoutManager(this));

            // Устанавливаем LayoutManager для отображения сеткой
            int numberOfColumns = 2; // Количество столбцов
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
            variantList.setLayoutManager(gridLayoutManager);

            // Устанавливаем адаптер
            variantList.setAdapter(variantAdapter);

            // Настраиваем обработчик кликов
            variantAdapter.setOnItemClickListener(position -> {
                Variant selectedVariant = variantsForList.get(position);
                Variant rightVariant =variants.get(0);


                boolean consent =Objects.equals(selectedVariant.group, rightVariant.group) && !Objects.equals(rightVariant.group, "0" );

                if(Objects.equals(selectedVariant.content, rightVariant.content) || consent )
                {
                   if(consent)
                   {
                       changeVariant(rightVariant.content, selectedVariant.content);
                       /*int indexSelect=0;
                       int indexRight=0;
                       for (int i = 0; i<mainVariants.length; i++)
                       {
                           String[] content = mainVariants[i].split("\\{t\\}");

                           content[0]=content[0].replace("{f}", "");
                           content[0]=replaceVariant(content[0]);
                           if(Objects.equals(content[0],rightVariant.content))
                               indexRight=i;
                           if(Objects.equals(content[0],selectedVariant.content))
                               indexSelect=i;

                       }
                       String temp=mainVariants[indexRight];
                       mainVariants[indexRight]=mainVariants[indexSelect];
                       mainVariants[indexSelect]=temp;*/
                   }
                    count_variant++;
                    trueAnswer++;
                    calculateDegree();



                    if((quantity_variant-count_variant_n)==(count_variant))
                    {

                            String main_content = createContent(content,count_variant);
                        updateContentWithBuffer1(main_content);


                            nextButton.setVisibility(View.VISIBLE);
                            variantList.setVisibility(View.GONE);




                    }
                    else
                    {
                        variantList.setVisibility(View.GONE);
                        contentView.setVisibility(View.GONE);
                        String main_content = createContent(content,count_variant);
                        updateContentWithBuffer1(main_content);
                        //варианты
                        listCreater(createVariant(content,count_variant));
                        contentView.setVisibility(View.VISIBLE);
                        //Содержание



                        //printMarkdown(main_content);

                    }

                }
                else
                {
                    wrongAnswer++;
                    calculateDegree();
                }


            });

        }
        else
        {
            variantList.setVisibility(View.GONE);
            check.setVisibility(View.VISIBLE);
            write.setVisibility(View.VISIBLE);

        }

    }
}