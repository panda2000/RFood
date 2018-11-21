package ru.seminma.rfood;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

/**
 * Created by max on 07.11.18.
 */

public class MealtimeCursorLoader extends CursorLoader {

    DB db;
    long startDate;
    long stopDate;

    public MealtimeCursorLoader(Context context, DB db, long startDate, long stopDate) {
        super(context);
        this.db = db;
        this.startDate = startDate;
        this.stopDate = stopDate;

    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = db.getAllMealtimeDataComments(startDate, stopDate);

            /*try {
               // TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        return cursor;
    }

}
