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
    public ArrayList<CalendarPunkt> calendarPunkts = new ArrayList<>(); // Инициализация

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<CustomCalendar> createCalendar(ArrayList<CalendarPunkt> allCalendarPunkts) throws ParseException {
        ArrayList<CustomCalendar> calendarResult = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        java.util.Calendar currentCalendar = java.util.Calendar.getInstance();

        // Цикл на 5 дней
        for (int i = 0; i < 5; i++) {
            CustomCalendar calendar = new CustomCalendar();
            ArrayList<CalendarPunkt> calendarTempPunkts = new ArrayList<>();

            calendar.date = sdf.format(currentCalendar.getTime());

            for (CalendarPunkt punkt : allCalendarPunkts) {
                if (calendar.date.equals(punkt.date_next)) {
                    calendarTempPunkts.add(punkt);
                }
            }

            calendar.calendarPunkts = calendarTempPunkts;
            calendarResult.add(calendar);

            currentCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }

        // Обработка пропущенных пунктов
        CustomCalendar missedCalendar = new CustomCalendar();
        ArrayList<CalendarPunkt> missedPunkts = new ArrayList<>();

        String currentFormattedDate = sdf.format(currentCalendar.getTime());

        for (CalendarPunkt punkt : allCalendarPunkts) {
            try {
                LocalDate dateNow = LocalDate.parse(currentFormattedDate);
                LocalDate datePunkt = LocalDate.parse(punkt.date_next);

                if (datePunkt.isBefore(dateNow)) {
                    missedPunkts.add(punkt);
                }
            } catch (DateTimeParseException e) {
                e.printStackTrace(); // Логируем ошибку, если формат некорректен
            }
        }

        missedCalendar.date = "Пропущено";
        missedCalendar.calendarPunkts = missedPunkts;
        calendarResult.add(missedCalendar);

        return calendarResult;
    }

    public CustomCalendar() {
        // Конструктор по умолчанию
    }
}
