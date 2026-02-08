package com.example.studycard.functional;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.Target;
import com.example.studycard.Prism4j.MyGrammarLocator;
import com.example.studycard.R;
import com.example.studycard.Test;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.syntax.Prism4jThemeDefault;
import io.noties.markwon.syntax.SyntaxHighlightPlugin;
import io.noties.prism4j.Prism4j;





public class Markdown {

    public  TextView contentView;
    public  Context context;

    public Markdown(TextView contentView, Context context)
    {
        this.contentView = contentView;
        this.context = context;
    }
    public  void print(String newContent) {
        newContent = replace(newContent);

        // Проверяем входное содержимое
        if (newContent == null || newContent.isEmpty()) {
            Log.e("MarkdownError", "Пустое содержимое");
            return;
        }

        // Настраиваем буфер
        TextView buffer = new TextView(context);
        buffer.setTextSize(contentView.getTextSize());
        buffer.setMovementMethod(LinkMovementMethod.getInstance());
        buffer.setTextIsSelectable(true);

        int TableHead;
        int TableOdd;


        TableHead = ContextCompat.getColor(context, R.color.TableHead);
        TableOdd = ContextCompat.getColor(context, R.color.TableOdd);


        // Настройка Markwon
        final Markwon markwon = Markwon.builder(context)
                .usePlugin(GlideImagesPlugin.create(new GlideImagesPlugin.GlideStore() {
                    @NonNull
                    @Override
                    public RequestBuilder<Drawable> load(@NonNull AsyncDrawable drawable) {
                        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                        int screenWidth = displayMetrics.widthPixels;
                        int targetWidth = screenWidth  / 10 * 8 ; // Устанавливаем половину ширины экрана
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
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(TablePlugin.create(context))
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
    public  String replace(String oldString)
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

}
