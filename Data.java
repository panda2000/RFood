package ru.seminma.rfood;


import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashSet;

public class Data  {

    DB db;



    //---------------------------------------------------------------------------
    String [] mealTime = {"06-00","07-30","09-30","12-00"};
    String [] journal2 = {"06-00 сыр","07-30 курица","09-30 яблоко","12-00 суп"};

    ArrayList <String> journal = new ArrayList<String>();

    public Data (Context ctx){
        // открываем подключение к БД
        db = new DB(ctx);
        db.open();

    }

    public String [] getMainData () {
        return mealTime;
    }

    public String [] getJournal () {return journal.toArray(new String[journal.size()]);
    }

    public Cursor getFoodTypeData (){
        // добавляем запись
        return db.getFoodTypeData();
    }

    public Cursor getFoodData(long mealtimeID, long foodTypeID){
        // добавляем запись
        return db.getFoodData(mealtimeID, foodTypeID);
    }

    public void addJournalItem (String item, String time, int image){
        // добавляем запись
        db.addRec(item, time, image);
    }

   // public void deleteJournalItem (long id){
   //     db.delRec(id);
   // }

    public long newMealtime (long timestamp){
        return db.newMealtime(timestamp);
    }

    public int deleteMealtime (long id){
        return db.deleteMealtime(id);
    }

    public String getMealtime(long id) {
        return db.getMealtime(id);
    }

    public String getMealtimeDay(long id) {
        return db.getMealtimeDay(id);
    }

    public void addMealtimeFood (String mealtimeID, HashSet<Long> foodsId){
        for (long food: foodsId) {
            db.addMealtimeFood (mealtimeID, food);
        }
    }

    public void delMealtimeFood (String mealtimeID, HashSet<Long> foodsId){
        long mealtime  = Long.parseLong(mealtimeID);
        for (long food: foodsId) {
            deleteMealtimeFood (mealtime, food);
        }
        Cursor cursor = db.getMealtimeFoodData (mealtime);
        if (cursor.getCount() == 0) {
            deleteMealtime (mealtime);
        }

    }

    public int deleteMealtimeFood (long mealtimeID, long foodID){
        return db.deleteMealtimeFood(mealtimeID, foodID);
    }

    public  void close () {
        // закрываем подключение при выходе
        db.close();
    }

    public String getMessage (long startDate, long stopDate){
        // Генерируем список для отправки
        String message = "";

        message += "-----------------\n";

        Cursor mealtimeCursor = db.getAllMealtimeData(startDate, stopDate);

        if (mealtimeCursor.moveToFirst()) {
           // message += cursor.getString(cursor.getColumnIndex("time")) + "  " + cursor.getString(cursor.getColumnIndex("txt")) + "\n";
            do {
                message += mealtimeCursor.getString(mealtimeCursor.getColumnIndex("name")) + "\n";

                long id = Long.parseLong(mealtimeCursor.getString(mealtimeCursor.getColumnIndex("_id")));

                Cursor foodCursor = db.getMealtimeFoodData(id);

                if(foodCursor.moveToFirst()){
                    do{
                        message += "    " + foodCursor.getString(foodCursor.getColumnIndex("food")) + "\n";
                    }while (foodCursor.moveToNext());
                }
                message += "\n";

            } while (mealtimeCursor.moveToNext());
        }

        message += "-----------------\n";

        return message;
    }


    public DB getDB () {
        return db;
    }
}
