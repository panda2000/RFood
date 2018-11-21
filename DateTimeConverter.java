package ru.seminma.rfood;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeConverter {
    public static String UTC2String (long timeStamp){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Time time = new Time(timeStamp);
            return sdf.format(time);
        } catch (Exception e){
            return "error UTC2String";
        }
    }

    public static long String2UTS (String string_date){
        long milliseconds=0;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date d = sdf.parse(string_date);
            milliseconds = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return milliseconds;
    }
}
