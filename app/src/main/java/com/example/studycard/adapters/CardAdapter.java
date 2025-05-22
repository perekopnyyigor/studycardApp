package com.example.studycard.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import io.noties.markwon.ext.latex.JLatexMathPlugin;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.Target;
import com.example.studycard.Prism4j.MyGrammarLocator;
import com.example.studycard.R;
import com.example.studycard.objects.Card;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.ext.tables.TableTheme;
import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.recycler.table.TableEntryPlugin;
import io.noties.markwon.syntax.Prism4jThemeDefault;
import io.noties.markwon.syntax.SyntaxHighlightPlugin;
import io.noties.prism4j.Prism4j;
import io.noties.prism4j.Prism4j;
import io.noties.prism4j.GrammarLocator;


public class CardAdapter extends ArrayAdapter<Card> {

    private LayoutInflater inflater;
    private int layout;
    private List<Card> cards;
    private Context context;

    public CardAdapter(Context context, int resource, List<Card> cards) {
        super(context, resource, cards);
        this.cards = cards;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }
    public String createContent(String content)
    {
        String[] arr = content.split("\\{m\\}");
        String result="";


        for (int i=0;i<arr.length;i++)
        {
            if(arr[i].contains("{n}"))
            {
                result +=" ";
            }
            else
                result += arr[i];

        }



        return result;
    }
    public String replace(String oldString)
    {
        String newString = createContent(oldString);
        newString = newString.replace("slash", "\\");
        newString = newString.replace("apostrof", "\'");
        newString = newString.replace("{m}", "");
        newString=newString.replace("{wr}", "");
        newString=newString.replace("{n}", "");
        newString=newString.replace("{f}", "");
        return newString;
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        final Prism4j prism4j = new Prism4j(new MyGrammarLocator());




        TextView nameView = view.findViewById(R.id.name);
        TextView content = view.findViewById(R.id.content);

        Card card = cards.get(position);

        nameView.setText(card.name);

        int TableHead = ContextCompat.getColor(context, R.color.TableHead);
        int TableOdd = ContextCompat.getColor(context, R.color.TableOdd);

        final Markwon markwon = Markwon.builder(context)
                //.usePlugin(GlideImagesPlugin.create(context)) // Плагин для загрузки изображений
                .usePlugin(GlideImagesPlugin.create(new GlideImagesPlugin.GlideStore() {
                    @NonNull
                    @Override
                    public RequestBuilder<Drawable> load(@NonNull AsyncDrawable drawable) {
                        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                        int screenWidth = displayMetrics.widthPixels;
                        int targetWidth = screenWidth / 2; // Устанавливаем половину ширины экрана
                        int targetHeight = targetWidth; // Пропорциональная высота (например, квадрат)

                        // Загрузка изображения через Glide с настройкой размера
                        return Glide.with(context)
                                .load(drawable.getDestination())
                                .override(targetWidth, targetHeight) // Указываем размеры изображения
                                .fitCenter(); // Настройка масштабирования (или .centerCrop() для обрезки)
                    }

                    @Override
                    public void cancel(@NonNull Target<?> target) {
                        Glide.with(context).clear(target);
                    }
                }))
                .usePlugin(MarkwonInlineParserPlugin.create()) // Inline парсер
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
                .usePlugin(SyntaxHighlightPlugin.create(prism4j, Prism4jThemeDefault.create())) // Подсветка синтаксиса
                .usePlugin(JLatexMathPlugin.create(content.getTextSize(), new JLatexMathPlugin.BuilderConfigure() {
                    @Override
                    public void configureBuilder(@NonNull JLatexMathPlugin.Builder builder) {
                        builder.inlinesEnabled(true); // Включение встроенных формул
                    }
                }))
                .build();




        markwon.setMarkdown(content, replace(card.content));
        //markwon.setMarkdown(content, markdownText);


        return view;
    }
}