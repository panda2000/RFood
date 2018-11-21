package ru.seminma.rfood;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

class MyCursorLoader extends CursorLoader {

    DB db;

    public MyCursorLoader(Context context, DB db) {
        super(context);
        this.db = db;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = db.getAllData();
            /*try {
               // TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        return cursor;
    }

}

