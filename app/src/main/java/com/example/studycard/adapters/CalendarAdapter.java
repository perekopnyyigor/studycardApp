package com.example.studycard.adapters;

import static android.widget.Toast.makeText;


import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
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

import com.example.studycard.objects.CalendarPunkt;
import com.example.studycard.objects.Chapter;
import com.example.studycard.objects.Cours;
import com.example.studycard.objects.CustomCalendar;
import com.example.studycard.objects.Lesson;
import com.example.studycard.objects.Topic;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends ArrayAdapter<CustomCalendar> {

    private LayoutInflater inflater;
    private int layout;
    private List<CustomCalendar> calendars;
    private Context context;


    public CalendarAdapter(Context context, int resource, List<CustomCalendar> calendars) {
        super(context, resource, calendars);
        this.calendars = calendars;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);


        TextView nameView = view.findViewById(R.id.dataPunkt);



        CustomCalendar calendar = calendars.get(position);



        nameView.setText(calendar.date);

        listCreater(view,calendar.calendarPunkts,context);


        return view;
    }
    public void listCreater(View view, ArrayList<Lesson> calendarPunkts, Context context)
    {

        // получаем элемент ListView
        ListView calendarList = view.findViewById(R.id.topicsCalendarList);
        //изменяем высоту
        int height = 100*calendarPunkts.size();
        ViewGroup.LayoutParams params = calendarList.getLayoutParams();
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, context.getResources().getDisplayMetrics());;

        calendarList.setLayoutParams(params);
        calendarList.requestLayout();
        // создаем адаптер
        CalendarPunktAdapter calendarPunktAdapter = new CalendarPunktAdapter(context, R.layout.calendar_topic, calendarPunkts);
        // устанавливаем адаптер
        calendarList.setAdapter(calendarPunktAdapter);

        // слушатель выбора в списке
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // получаем выбранный пункт
                Lesson calendarPunkt = (Lesson) parent.getItemAtPosition(position);

                Intent intent = new Intent(context, TopicActivity.class);

                intent.putExtra("name", calendarPunkts.get(position).name);
                intent.putExtra("id", calendarPunkts.get(position).topic_id);
                intent.putExtra("cours_id", calendarPunkts.get(position).cours_id);
                context.startActivity(intent);




            }

        };

        calendarList.setOnItemClickListener(itemListener);
    }
}