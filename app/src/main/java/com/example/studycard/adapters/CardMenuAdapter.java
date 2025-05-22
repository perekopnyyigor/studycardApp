package com.example.studycard.adapters;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.Target;
import com.example.studycard.Prism4j.MyGrammarLocator;
import com.example.studycard.R;
import com.example.studycard.objects.Card;
import com.example.studycard.objects.Cours;
import com.example.studycard.objects.PunktMenu;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.syntax.Prism4jThemeDefault;
import io.noties.markwon.syntax.SyntaxHighlightPlugin;
import io.noties.prism4j.Prism4j;

public class CardMenuAdapter extends RecyclerView.Adapter<CardMenuAdapter.ViewHolder> {
    private List<Card> data;
    private final Context context;
    private CardMenuAdapter.OnItemClickListener clickListener;

    public CardMenuAdapter(Context context, List<Card> data) {
        this.data = data;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(CardMenuAdapter.OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new ViewHolder(view,clickListener);
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

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = data.get(position).name;
        String cardContent = data.get(position).content;

        final Prism4j prism4j = new Prism4j(new MyGrammarLocator());

        holder.nameView.setText(name);
        TextView content=holder.contentView;

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




        markwon.setMarkdown(content, replace(cardContent));


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView contentView;


        public ViewHolder(View itemView,  CardMenuAdapter.OnItemClickListener listener) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name);
            contentView = itemView.findViewById(R.id.content);

            // Клик по элементу
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);

                    }
                }
            });
        }
    }
}

