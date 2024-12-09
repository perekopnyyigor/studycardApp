package com.example.studycard;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import com.example.studycard.Prism4j.MyGrammarLocator;
import com.example.studycard.adapters.VariantAdapter;
import com.example.studycard.objects.Card;
import com.example.studycard.objects.Variant;

import java.util.ArrayList;
import java.util.Objects;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.syntax.Prism4jThemeDefault;
import io.noties.markwon.syntax.SyntaxHighlightPlugin;
import io.noties.prism4j.Prism4j;

public class Test extends AppCompatActivity {
    public static int count=0;
    public int count_variant=0;
    public int quantity_variant=0;
    ArrayList<Card> cards = new ArrayList<Card>();

    TextView contentView;
    String content;
    Button nextButton;
    EditText write;
    Button check;
    RecyclerView variantList;
    private VariantAdapter variantAdapter;
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

            variantList = findViewById(R.id.recyclerView);
            //Счетчик`
            TextView countView = findViewById(R.id.count);
            int lenght = cards.size();
            String countStr = String.valueOf(count+1)+" / "+String.valueOf(lenght);
            countView.setText(countStr);

            //Название
            String name = cards.get(count).name;
            TextView nameView = findViewById(R.id.name);
            nameView.setText(name);




            //Содержание
            content = cards.get(count).content;
            contentView = findViewById(R.id.content);
            String main_content = createContent(content,count_variant);
            printMarkdown(main_content);

            makeText(this, "Create", Toast.LENGTH_SHORT).show();
            //варианты


            if (count_variant==0)
            {
                ArrayList<Variant> variants=createVariant(content,count_variant);
                listCreater(variants);
                quantity_variant =variants.size();
                nextButton = findViewById(R.id.next);
                nextButton.setVisibility(View.GONE);
            }


            //Вписывание текста
            write = findViewById(R.id.write);
            check = findViewById(R.id.check);
            write.setVisibility(View.GONE);
            check.setVisibility(View.GONE);

            return insets;
        });
    }
    public String replace(String oldString)
    {
        String newString =  oldString.replace("slash", "\\");
        newString=newString.replace("{wr}", "");
        newString=newString.replace("{m}", "");
        return newString;
    }
    public void next(View v)
    {
        count_variant=0;
        count++;
        Intent intent = new Intent(Test.this, Test.class);
        intent.putExtra("cards",cards);
        startActivity(intent);
    }
    public void check(View v)
    {
        ArrayList<Variant> variants=createVariant(content,count_variant);

        String variantStr = write.getText().toString();
        Variant rightVariant =variants.get(0);



        if(Objects.equals(variantStr, rightVariant.content))
        {
            count_variant++;



            write.setVisibility(View.GONE);
            check.setVisibility(View.GONE);
            variantList.setVisibility(View.VISIBLE);




            //варианты
            listCreater(createVariant(content,count_variant));

            //Содержание
            String main_content = createContent(content,count_variant);
            printMarkdown(main_content);

            makeText(this, quantity_variant+"/"+count_variant, Toast.LENGTH_SHORT).show();

        }
        //String[] arr = content.split("\\{m\\}");
        /*if(quantity_variant==count_variant)
        {
            nextButton.setVisibility(View.VISIBLE);

        }*/

    }
    public void printMarkdown(String main_content)
    {
        final Prism4j prism4j = new Prism4j(new MyGrammarLocator());

        final Markwon markwon = Markwon.builder(Test.this)

                .usePlugin(GlideImagesPlugin.create(Test.this)) // Используем GlideImagesPlugin
                .usePlugin(MarkwonInlineParserPlugin.create()) // Inline парсер
                .usePlugin(SyntaxHighlightPlugin.create(prism4j, Prism4jThemeDefault.create())) // Подсветка синтаксиса
                .usePlugin(JLatexMathPlugin.create(contentView.getTextSize(), new JLatexMathPlugin.BuilderConfigure() {
                    @Override
                    public void configureBuilder(@NonNull JLatexMathPlugin.Builder builder) {
                        // ENABLE inlines
                        builder.inlinesEnabled(true);
                    }
                }))
                .build();


        markwon.setMarkdown(contentView, replace(main_content));
    }

    public ArrayList<Variant> createVariant(String content,int start)
    {

        ArrayList<Variant> variants = new ArrayList<>();
        String[] arr = content.split("\\{m\\}");

        for (int i=start*2;i<arr.length;i++)
        {
            if (i%2==1)
            {
                Variant variant = new Variant();
                variant.content = arr[i];

                if (arr[i].contains("{wr}"))
                {
                    variant.type=2;
                    variant.content=variant.content.replace("{wr}", "");
                }
                else
                    variant.type=1;

                variants.add(variant);
            }
        }
       return variants;
    }

    public String createContent(String content, int start)
    {
        String[] arr = content.split("\\{m\\}");
        String result1="";
        String result2="";

        for (int i=0;i<start*2;i++)
        {

                result1 += arr[i];

        }

        for (int i=start*2;i<arr.length;i++)
        {
            if (i%2==0)
            {
                result2 += arr[i];
            }
            else
            {
                result2 += "...";
            }
        }

        return result1+result2;
    }

    public void listCreater(ArrayList<Variant> variants) {
        // Получаем RecyclerView

        //variantList.setVisibility(View.VISIBLE);
        if(variants.get(0).type==1)
        {
            ArrayList<Variant> variantsForList = new ArrayList<>();
            for(int i=0; i<variants.size();i++)
            {
                if (variants.get(i).type==1)
                    variantsForList.add(variants.get(i));
            }

            // Создаем адаптер
            VariantAdapter variantAdapter = new VariantAdapter(this, variantsForList);

            // Устанавливаем LayoutManager для RecyclerView
            variantList.setLayoutManager(new LinearLayoutManager(this));

            // Устанавливаем адаптер
            variantList.setAdapter(variantAdapter);

            // Настраиваем обработчик кликов
            variantAdapter.setOnItemClickListener(position -> {
                Variant selectedVariant = variants.get(position);
                Variant rightVariant =variants.get(0);



                if(Objects.equals(selectedVariant.content, rightVariant.content) )
                {
                    count_variant++;
                    makeText(this, quantity_variant+"/"+count_variant, Toast.LENGTH_SHORT).show();
                    if(quantity_variant==(count_variant))
                    {
                        String main_content = createContent(content,count_variant);
                        printMarkdown(main_content);

                        nextButton.setVisibility(View.VISIBLE);
                        variantList.setVisibility(View.GONE);

                    }
                    else
                    {

                        //варианты
                        listCreater(createVariant(content,count_variant));

                        //Содержание
                        String main_content = createContent(content,count_variant);
                        printMarkdown(main_content);
                    }

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