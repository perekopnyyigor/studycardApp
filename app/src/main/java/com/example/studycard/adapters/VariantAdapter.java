package com.example.studycard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studycard.Prism4j.MyGrammarLocator;
import com.example.studycard.R;
import com.example.studycard.objects.Variant;

import java.util.List;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.syntax.Prism4jThemeDefault;
import io.noties.markwon.syntax.SyntaxHighlightPlugin;
import io.noties.prism4j.Prism4j;

public class VariantAdapter extends RecyclerView.Adapter<VariantAdapter.ViewHolder> {

    private final List<Variant> variants;
    private final Context context;
    //public final Markwon markwon; // Глобальная инициализация Markwon
    private OnItemClickListener clickListener;

    // Интерфейс для обработки кликов
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public VariantAdapter(Context context, List<Variant> variants) {
        this.variants = variants;
        this.context = context;




    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.variant, parent, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Variant variant = variants.get(position);
        TextView content = holder.itemView.findViewById(R.id.content);


        // Устанавливаем Markdown-контент
        String markdownContent = variant.content; // Получение контента

        if (variant.type==3)
        {
            markdownContent = "$$"+variant.content+"$$";

        }
        if (markdownContent == null || markdownContent.isEmpty()) {
            markdownContent = "Нет данных"; // Защита от null
        }

        // Настройка Markwon
        Prism4j prism4j = new Prism4j(new MyGrammarLocator());
        Markwon markwon = Markwon.builder(context)
                .usePlugin(GlideImagesPlugin.create(context)) // Для изображений
                .usePlugin(MarkwonInlineParserPlugin.create()) // Inline парсер
                .usePlugin(SyntaxHighlightPlugin.create(prism4j, Prism4jThemeDefault.create())) // Подсветка синтаксиса
                .usePlugin(JLatexMathPlugin.create(content.getTextSize(), new JLatexMathPlugin.BuilderConfigure() {
                    @Override
                    public void configureBuilder(@android.support.annotation.NonNull JLatexMathPlugin.Builder builder) {
                        // ENABLE inlines
                        builder.inlinesEnabled(true);
                    }
                }))
                .build();

        markwon.setMarkdown(holder.content, markdownContent);

        // Устанавливаем клик для элемента
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(position);
                //Toast.makeText(context, "clickListener", Toast.LENGTH_SHORT).show();
            }
        });

        content.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(position);
                //Toast.makeText(context, "clickText", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return variants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView content;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            content = itemView.findViewById(R.id.content);

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
