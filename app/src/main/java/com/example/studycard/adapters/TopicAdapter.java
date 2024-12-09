package com.example.studycard.adapters;

import static android.widget.Toast.makeText;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studycard.R;
import com.example.studycard.objects.Chapter;
import com.example.studycard.objects.Cours;
import com.example.studycard.objects.Topic;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TopicAdapter extends ArrayAdapter<Topic> {

    private LayoutInflater inflater;
    private int layout;
    private List<Topic> topics;
    private Context context;

    public TopicAdapter(Context context, int resource, List<Topic> topics) {
        super(context, resource, topics);
        this.topics = topics;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.context = context;

    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        //ImageView coursPicture = view.findViewById(R.id.picture);
        TextView nameView = view.findViewById(R.id.name);
        //TextView descriptionView = view.findViewById(R.id.description);
        //makeText(context, position+" topic", Toast.LENGTH_SHORT).show();
        Topic topic = topics.get(position);

        //flagView.setImageResource(state.getFlagResource());
        nameView.setText(topic.name);
        //descriptionView.setText(chapter.description);
        //Picasso.get().load(chapter.picture).into(coursPicture);
        // получаем элемент ListView


        return view;
    }
}