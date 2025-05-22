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
import com.example.studycard.objects.Lesson;
import com.example.studycard.objects.Topic;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CalendarPunktAdapter extends ArrayAdapter<Lesson> {

    private LayoutInflater inflater;
    private int layout;
    private List<Lesson> calendarPunkts;
    private Context context;

    public CalendarPunktAdapter(Context context, int resource, List<Lesson> calendarPunkts) {
        super(context, resource, calendarPunkts);
        this.calendarPunkts = calendarPunkts;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.context = context;

    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        TextView nameView = view.findViewById(R.id.name);
        TextView coursView = view.findViewById(R.id.cours);



        Lesson calendarPunkt = calendarPunkts.get(position);

        nameView.setText(calendarPunkt.name);

        coursView.setText(calendarPunkt.cours);



        return view;
    }
}
