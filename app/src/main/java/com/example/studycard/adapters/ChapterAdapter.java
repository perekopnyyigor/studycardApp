package com.example.studycard.adapters;

import static android.widget.Toast.makeText;


import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studycard.CoursActivity;
import com.example.studycard.MainActivity;
import com.example.studycard.R;
import com.example.studycard.TopicActivity;
import com.example.studycard.objects.CRM;
import com.example.studycard.objects.Chapter;
import com.example.studycard.objects.Cours;
import com.example.studycard.objects.Lesson;
import com.example.studycard.objects.Topic;
import com.example.studycard.objects.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends ArrayAdapter<Chapter> {

    private LayoutInflater inflater;
    private int layout;
    private List<Chapter> chapters;
    private Context context;
    private ArrayList<Lesson> lessons;

    public ChapterAdapter(Context context, int resource, List<Chapter> chapters, ArrayList<Lesson> lessons) {
        super(context, resource, chapters);
        this.chapters = chapters;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.context = context;

        this.lessons = lessons;


    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);


        TextView nameView = view.findViewById(R.id.name);



        Chapter chapter = chapters.get(position);



        nameView.setText(chapter.name);

        listCreater(view,chapter.topics,context);


        return view;
    }
    public void listCreater(View view, ArrayList<Topic> topics, Context context)
    {

        // получаем элемент ListView
        ListView topicsList = view.findViewById(R.id.topicsList);
        //изменяем высоту
        int height = 90*topics.size();
        ViewGroup.LayoutParams params = topicsList.getLayoutParams();
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, context.getResources().getDisplayMetrics());;

        topicsList.setLayoutParams(params);
        topicsList.requestLayout();
        // создаем адаптер
        TopicAdapter topicAdapter = new TopicAdapter(context, R.layout.topic, topics, lessons);
        // устанавливаем адаптер
        topicsList.setAdapter(topicAdapter);

        // слушатель выбора в списке
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //CRM
                if(User.id.equals("0"))
                {
                    User.getUser(context);
                    CRM.userEvent(User.noId, CRM.openTopic);

                }

                // получаем выбранный пункт
                Topic selectedTopic = (Topic)parent.getItemAtPosition(position);

                Intent intent = new Intent(context, TopicActivity.class);

                intent.putExtra("name", topics.get(position).name);
                intent.putExtra("id", String.valueOf(topics.get(position).id));
                intent.putExtra("cours_id", String.valueOf(topics.get(position).cours_id));
                intent.putExtra("commercial", String.valueOf(topics.get(position).commercial));
                context.startActivity(intent);




            }

        };

        topicsList.setOnItemClickListener(itemListener);
    }
}