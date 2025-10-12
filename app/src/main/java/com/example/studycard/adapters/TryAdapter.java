package com.example.studycard.adapters;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
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
import com.example.studycard.objects.Try;
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

public class TryAdapter extends RecyclerView.Adapter<TryAdapter.ViewHolder> {
    private List<Try> tries;
    private final Context context;
    private TryAdapter.OnItemClickListener clickListener;

    public TryAdapter(Context context, List<Try> tries) {
        this.tries = tries;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(TryAdapter.OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout._try, parent, false);
        return new ViewHolder(view,clickListener);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String grade = tries.get(position).grade;
        String date = tries.get(position).date;



        holder.gradeView.setText(grade);
        holder.dateView.setText(date);







    }

    @Override
    public int getItemCount() {
        return tries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView gradeView;
        TextView dateView;


        public ViewHolder(View itemView,  TryAdapter.OnItemClickListener listener) {
            super(itemView);
            gradeView = itemView.findViewById(R.id.grade);
            dateView = itemView.findViewById(R.id.date);

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

