package com.example.studycard.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import io.noties.markwon.ext.latex.JLatexMathPlugin;


import com.example.studycard.Prism4j.MyGrammarLocator;
import com.example.studycard.R;
import com.example.studycard.objects.Card;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.noties.markwon.Markwon;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
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
    public String replace(String oldString)
    {
        String newString =  oldString.replace("slash", "\\");
        newString=newString.replace("{m}", "");
        newString=newString.replace("{wr}", "");
        return newString;
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        final Prism4j prism4j = new Prism4j(new MyGrammarLocator());




        TextView nameView = view.findViewById(R.id.name);
        TextView content = view.findViewById(R.id.content);

        Card card = cards.get(position);

        nameView.setText(card.name);


        final Markwon markwon = Markwon.builder(context)

                .usePlugin(GlideImagesPlugin.create(context)) // Используем GlideImagesPlugin
                .usePlugin(MarkwonInlineParserPlugin.create()) // Inline парсер
                .usePlugin(SyntaxHighlightPlugin.create(prism4j, Prism4jThemeDefault.create())) // Подсветка синтаксиса
                .usePlugin(JLatexMathPlugin.create(content.getTextSize(), new JLatexMathPlugin.BuilderConfigure() {
                    @Override
                    public void configureBuilder(@NonNull JLatexMathPlugin.Builder builder) {
                        // ENABLE inlines
                        builder.inlinesEnabled(true);
                    }
                }))
                .build();




        markwon.setMarkdown(content, replace(card.content));


        return view;
    }
}