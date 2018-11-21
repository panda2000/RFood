package ru.seminma.rfood;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

/**
 * Created by max on 07.11.18.
 */

public class MealtimeFoodCursorLoader extends CursorLoader {

    DB db;
    long id;

    public MealtimeFoodCursorLoader(Context context, DB db, long new_id) {
        super(context);
        this.db = db;
        id = new_id;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = db.getMealtimeFoodData(id);
            /*try {
               // TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        return cursor;
    }

}
