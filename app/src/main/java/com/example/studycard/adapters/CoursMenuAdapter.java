package com.example.studycard.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.studycard.R;
import com.example.studycard.objects.Cours;
import com.example.studycard.objects.PunktMenu;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CoursMenuAdapter extends RecyclerView.Adapter<CoursMenuAdapter.ViewHolder> {
    private List<Cours> data;
    private VariantAdapter.OnItemClickListener clickListener;

    public CoursMenuAdapter(List<Cours> data) {
        this.data = data;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(VariantAdapter.OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cours, parent, false);
        return new ViewHolder(view,clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = data.get(position).name;
        String picture = data.get(position).picture;
        String descript = data.get(position).description;

        holder.description.setText(descript);
        holder.textView.setText(item);
        Picasso.get().load(picture).into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView description;
        ImageView imageView;

        public ViewHolder(View itemView,  VariantAdapter.OnItemClickListener listener) {
            super(itemView);
            textView = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            imageView = itemView.findViewById(R.id.picture);
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
