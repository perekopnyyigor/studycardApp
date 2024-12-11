package com.example.studycard.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.studycard.R;
import com.example.studycard.objects.PunktMenu;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private List<PunktMenu> data;
    private VariantAdapter.OnItemClickListener clickListener;

    public MenuAdapter(List<PunktMenu> data) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_menu, parent, false);
        return new ViewHolder(view,clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = data.get(position).name;
        int picture = data.get(position).picture;

        holder.textView.setText(item);
        holder.imageView.setImageResource(picture);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView,  VariantAdapter.OnItemClickListener listener) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
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
