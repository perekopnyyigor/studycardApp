package com.example.studycard.adapters;

import static android.widget.Toast.makeText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studycard.R;
import com.example.studycard.functional.DrawCircle;
import com.example.studycard.objects.Chapter;
import com.example.studycard.objects.Cours;
import com.example.studycard.objects.Lesson;
import com.example.studycard.objects.Topic;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TopicAdapter extends ArrayAdapter<Topic> {

    private LayoutInflater inflater;
    private int layout;
    private List<Topic> topics;
    private Context context;
    private ArrayList<Lesson> lessons;

    public TopicAdapter(Context context, int resource, List<Topic> topics, ArrayList<Lesson> lessons) {
        super(context, resource, topics);
        this.topics = topics;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.lessons=lessons;



    }
    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);


        TextView nameView = view.findViewById(R.id.name);
        TextView circleView = view.findViewById(R.id.progress);

        Topic topic = topics.get(position);

        //flagView.setImageResource(state.getFlagResource());
        Lesson lesson = Lesson.findLesson(String.valueOf(topic.id),lessons);
        if (lesson!=null)
        {
            nameView.setTypeface(nameView.getTypeface(), Typeface.BOLD);

            if (topics.get(position).commercial.equals("0"))
                nameView.setText(topic.name);

            if (topics.get(position).commercial.equals("1"))
                nameView.setText("\uD83D\uDD12 "+topic.name);

            SpannableString spannable = DrawCircle.Draw(context,lesson);
            circleView.setText(spannable);
        }

        else
        {
            nameView.setTypeface(nameView.getTypeface(), Typeface.BOLD);

            if (topics.get(position).commercial.equals("0"))
                nameView.setText(topic.name);

            if (topics.get(position).commercial.equals("1"))
                nameView.setText("\uD83D\uDD12 "+topic.name);

            circleView.setVisibility(View.GONE);
        }







        return view;
    }
}