package com.example.studycard.functional;






import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import androidx.core.content.ContextCompat;

import com.example.studycard.R;
import com.example.studycard.objects.Lesson;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Locale;

public class DrawCircle {
    public static SpannableString Draw(Context context, Lesson lesson) {
        SpannableString spannable = new SpannableString("          "); // Два пробела под два круга
        int i;
        for ( i=0;i<lesson.period;i++)
        {
            Drawable circle = ContextCompat.getDrawable(context, R.drawable.circle);
            if (circle != null) circle.setBounds(0, 0, 50, 50);
            ImageSpan imageSpan = new ImageSpan(circle, ImageSpan.ALIGN_BASELINE);
            spannable.setSpan(imageSpan, i, i+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE); // Первый круг
        }

        java.util.Calendar dateCalendarNow = java.util.Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentFormattedDate = sdf.format(dateCalendarNow.getTime());
        i++;
        LocalDate dateNow = LocalDate.parse(currentFormattedDate);
        LocalDate datePunkt = LocalDate.parse(lesson.date_next);

        if (datePunkt.isBefore(dateNow)) {
            Drawable circle = ContextCompat.getDrawable(context, R.drawable.circle_red);
            if (circle != null) circle.setBounds(0, 0, 50, 50);
            ImageSpan imageSpan = new ImageSpan(circle, ImageSpan.ALIGN_BASELINE);
            spannable.setSpan(imageSpan, i, i+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE); // Первый круг
        }
        else if(datePunkt.isAfter(dateNow))
        {
            Drawable circle = ContextCompat.getDrawable(context, R.drawable.circle_grey);
            if (circle != null) circle.setBounds(0, 0, 50, 50);
            ImageSpan imageSpan = new ImageSpan(circle, ImageSpan.ALIGN_BASELINE);
            spannable.setSpan(imageSpan, i, i+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE); // Первый круг
        }
        else if(datePunkt.isEqual(dateNow))
        {
            Drawable circle = ContextCompat.getDrawable(context, R.drawable.circle_green);
            if (circle != null) circle.setBounds(0, 0, 50, 50);
            ImageSpan imageSpan = new ImageSpan(circle, ImageSpan.ALIGN_BASELINE);
            spannable.setSpan(imageSpan, i, i+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE); // Первый круг
        }


        return spannable;
    }
}
