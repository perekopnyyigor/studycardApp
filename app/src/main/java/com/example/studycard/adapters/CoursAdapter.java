package com.example.studycard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.studycard.R;
import com.example.studycard.objects.Cours;
import com.example.studycard.objects.Lesson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CoursAdapter extends ArrayAdapter<Cours> {

    private LayoutInflater inflater;
    private int layout;
    private List<Cours> courses;

    public CoursAdapter(Context context, int resource, List<Cours> courses) {
        super(context, resource, courses);
        this.courses = courses;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);



    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        ImageView coursPicture = view.findViewById(R.id.picture);
        TextView nameView = view.findViewById(R.id.name);
        TextView descriptionView = view.findViewById(R.id.description);

        Cours cours = courses.get(position);

        //flagView.setImageResource(state.getFlagResource());
        nameView.setText(cours.name);
        descriptionView.setText(cours.description);
        Picasso.get().load(cours.picture).into(coursPicture);

        return view;
    }
}