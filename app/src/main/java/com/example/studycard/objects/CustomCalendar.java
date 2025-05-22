package com.example.studycard.objects;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;

public class CustomCalendar { // Изменено имя класса
    public String date;
    public ArrayList<Lesson> calendarPunkts = new ArrayList<>(); // Инициализация

    @RequiresApi(api = Build.VERSION_CODES.O)

    public static ArrayList<CustomCalendar> createCalendar(ArrayList<Lesson> lessons) throws ParseException {
        ArrayList<CustomCalendar> calendarResult = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        java.util.Calendar currentCalendar = java.util.Calendar.getInstance();

        // Цикл на 5 дней
        for (int i = 0; i < 5; i++) {
            CustomCalendar calendar = new CustomCalendar();
            ArrayList<Lesson> lessonsTemp = new ArrayList<>();

            calendar.date = sdf.format(currentCalendar.getTime());

            for (Lesson lesson : lessons) {
                if (calendar.date.equals(lesson.date_next)) {
                    lessonsTemp.add(lesson);
                }
            }

            if(i==0)
                calendar.date = "Сегодня";
            else if(i==1)
                calendar.date = "Завтра";





            calendar.calendarPunkts = lessonsTemp;
            calendarResult.add(calendar);

            currentCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }

        // Обработка пропущенных пунктов
        CustomCalendar missedCalendar = new CustomCalendar();
        ArrayList<Lesson> missedPunkts = new ArrayList<>();
        java.util.Calendar dateCalendarNow = java.util.Calendar.getInstance();
        String currentFormattedDate = sdf.format(dateCalendarNow.getTime());

        for (Lesson lesson : lessons) {
            try {
                LocalDate dateNow = LocalDate.parse(currentFormattedDate);
                LocalDate datePunkt = LocalDate.parse(lesson.date_next);

                if (datePunkt.isBefore(dateNow)) {
                    missedPunkts.add(lesson);
                }
            } catch (DateTimeParseException e) {
                e.printStackTrace(); // Логируем ошибку, если формат некорректен
            }
        }

        missedCalendar.date = "Пропущенное";
        missedCalendar.calendarPunkts = missedPunkts;
        calendarResult.add(missedCalendar);

        return calendarResult;
    }

    public CustomCalendar() {
        // Конструктор по умолчанию
    }




}
